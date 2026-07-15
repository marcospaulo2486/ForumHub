package br.com.ForumHub.domain.topico;

import br.com.ForumHub.domain.Usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidacaoRegraTest {

    @Mock
    private TopicoRepository topicoRepository;

    @InjectMocks
    private ValidacaoRegra validacaoRegra;

    private Topico topicoExistente;

    @BeforeEach
    void setUp() {
        Usuario autor = new Usuario(1L, "autor@forum.com", "hash123");
        topicoExistente = new Topico(
                5L,
                "Título Duplicado",
                "Mensagem Duplicada",
                LocalDateTime.of(2025, 3, 10, 9, 30),
                "ATIVO",
                autor,
                "Java Avançado"
        );
    }

    @Test
    @DisplayName("Deve lançar exceção quando título e mensagem já existem no banco")
    void validarTopicoDuplicado_quandoJaExiste_deveLancarIllegalArgumentException() {
        when(topicoRepository.findByTituloAndMensagem("Título Duplicado", "Mensagem Duplicada"))
                .thenReturn(Optional.of(topicoExistente));

        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> validacaoRegra.validarTopicoDuplicado("Título Duplicado", "Mensagem Duplicada")
        );

        assertEquals(
                "Tópico duplicado! Já existe um tópico com o mesmo título e mensagem.",
                excecao.getMessage()
        );
    }

    @Test
    @DisplayName("Não deve lançar exceção quando título e mensagem são únicos")
    void validarTopicoDuplicado_quandoNaoExiste_naoDeveLancarExcecao() {
        when(topicoRepository.findByTituloAndMensagem(anyString(), anyString()))
                .thenReturn(Optional.empty());

        assertDoesNotThrow(
                () -> validacaoRegra.validarTopicoDuplicado("Título Único", "Mensagem Única")
        );
    }

    @Test
    @DisplayName("Deve lançar exceção quando outro tópico (com ID diferente) tem o mesmo título e mensagem no update")
    void validarTopicoDuplicadoUpdate_quandoOutroTopicoConflita_deveLancarIllegalArgumentException() {
        when(topicoRepository.findByTituloAndMensagemAndIdNot("Título Duplicado", "Mensagem Duplicada", 99L))
                .thenReturn(Optional.of(topicoExistente));

        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> validacaoRegra.validarTopicoDuplicadoUpdate("Título Duplicado", "Mensagem Duplicada", 99L)
        );

        assertEquals(
                "Tópico duplicado! Já existe um tópico com o mesmo título e mensagem.",
                excecao.getMessage()
        );
    }

    @Test
    @DisplayName("Não deve lançar exceção quando somente o próprio tópico possui esse título e mensagem")
    void validarTopicoDuplicadoUpdate_quandoSomenteOProprioTopico_naoDeveLancarExcecao() {
        when(topicoRepository.findByTituloAndMensagemAndIdNot(anyString(), anyString(), anyLong()))
                .thenReturn(Optional.empty());

        assertDoesNotThrow(
                () -> validacaoRegra.validarTopicoDuplicadoUpdate("Título X", "Mensagem X", 5L)
        );
    }

    @Test
    @DisplayName("Deve lançar exceção independente do ID quando título e mensagem conflitam no update")
    void validarTopicoDuplicadoUpdate_comIdDiferente_deveLancarExcecaoSeHouverConflito() {
        when(topicoRepository.findByTituloAndMensagemAndIdNot("Título Conflito", "Mensagem Conflito", 1L))
                .thenReturn(Optional.of(topicoExistente));

        assertThrows(
                IllegalArgumentException.class,
                () -> validacaoRegra.validarTopicoDuplicadoUpdate("Título Conflito", "Mensagem Conflito", 1L)
        );
    }
}
