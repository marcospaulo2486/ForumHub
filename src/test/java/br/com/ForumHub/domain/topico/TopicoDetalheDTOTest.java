package br.com.ForumHub.domain.topico;

import br.com.ForumHub.domain.Usuario.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TopicoDetalheDTOTest {

    @Test
    @DisplayName("Deve mapear todos os campos do Topico para o DTO corretamente")
    void constructor_deveMapearTodosOsCampos() {
        Usuario autor = new Usuario(1L, "autor@forum.com", "hash123");
        LocalDateTime data = LocalDateTime.of(2025, 7, 10, 14, 30);
        Topico topico = new Topico(10L, "Título", "Mensagem", data, "ATIVO", autor, "Java");

        TopicoDetalheDTO dto = new TopicoDetalheDTO(topico);

        assertEquals(10L, dto.getId());
        assertEquals("Título", dto.getTitulo());
        assertEquals("Mensagem", dto.getMensagem());
        assertEquals(data, dto.getDataCriacao());
        assertEquals("ATIVO", dto.getStatus());
        assertEquals("autor@forum.com", dto.getAutor());
        assertEquals("Java", dto.getCurso());
    }

    @Test
    @DisplayName("Deve mapear autor como o login do usuário")
    void constructor_deveMapearAutorComoUsername() {
        Usuario autor = new Usuario(2L, "joao@email.com", "hash456");
        Topico topico = new Topico(5L, "T2", "M2", LocalDateTime.now(), "ATIVO", autor, "Spring");

        TopicoDetalheDTO dto = new TopicoDetalheDTO(topico);

        assertEquals("joao@email.com", dto.getAutor());
    }
}
