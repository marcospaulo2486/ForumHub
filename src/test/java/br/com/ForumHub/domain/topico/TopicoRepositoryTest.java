package br.com.ForumHub.domain.topico;

import br.com.ForumHub.domain.Usuario.Usuario;
import br.com.ForumHub.domain.Usuario.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TopicoRepositoryTest {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario autor;

    @BeforeEach
    void setUp() {
        autor = usuarioRepository.save(new Usuario(null, "autor@email.com", "hash_seguro"));
    }

    private Topico criarTopico(String titulo, String mensagem, String curso) {
        return new Topico(
                null,
                titulo,
                mensagem,
                LocalDateTime.now(),
                "ATIVO",
                autor,
                curso
        );
    }

    @Test
    @DisplayName("Deve encontrar tópico por título e mensagem")
    void findByTituloAndMensagem_quandoExiste_deveRetornarTopico() {
        topicoRepository.save(criarTopico("Título Teste", "Mensagem Teste", "Java"));

        Optional<Topico> resultado = topicoRepository.findByTituloAndMensagem("Título Teste", "Mensagem Teste");

        assertTrue(resultado.isPresent());
        assertEquals("Título Teste", resultado.get().getTitulo());
        assertEquals("Mensagem Teste", resultado.get().getMensagem());
    }

    @Test
    @DisplayName("Não deve encontrar tópico quando título e mensagem não existem")
    void findByTituloAndMensagem_quandoNaoExiste_deveRetornarVazio() {
        Optional<Topico> resultado = topicoRepository.findByTituloAndMensagem("Inexistente", "Não existe");

        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Deve encontrar tópico por título, mensagem e ID diferente")
    void findByTituloAndMensagemAndIdNot_quandoOutroTopicoExiste_deveRetornarTopico() {
        Topico topico1 = topicoRepository.save(criarTopico("Título A", "Mensagem A", "Java"));
        topicoRepository.save(criarTopico("Título B", "Mensagem B", "Spring"));

        Optional<Topico> resultado = topicoRepository.findByTituloAndMensagemAndIdNot(
                "Título B", "Mensagem B", topico1.getId());

        assertTrue(resultado.isPresent());
        assertEquals("Título B", resultado.get().getTitulo());
    }

    @Test
    @DisplayName("Não deve encontrar conflito quando só o próprio tópico tem o título e mensagem")
    void findByTituloAndMensagemAndIdNot_quandoSoOProprio_deveRetornarVazio() {
        Topico topico = topicoRepository.save(criarTopico("Único", "Mensagem Única", "Java"));

        Optional<Topico> resultado = topicoRepository.findByTituloAndMensagemAndIdNot(
                "Único", "Mensagem Única", topico.getId());

        assertFalse(resultado.isPresent());
    }

}
