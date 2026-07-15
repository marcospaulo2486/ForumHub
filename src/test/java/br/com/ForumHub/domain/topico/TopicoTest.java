package br.com.ForumHub.domain.topico;

import br.com.ForumHub.domain.Usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TopicoTest {

    private Usuario autor;

    @BeforeEach
    void setUp() {
        autor = new Usuario(1L, "autor@forum.com", "hash123");
    }

    private Topico criarTopicoBase() {
        return new Topico(
                10L,
                "Título Original",
                "Mensagem Original",
                LocalDateTime.of(2025, 1, 15, 10, 0),
                "ATIVO",
                autor,
                "Java Fundamentals"
        );
    }

    @Test
    @DisplayName("Deve atualizar todos os campos quando todos os valores são não-nulos")
    void atualizar_comTodosOsCampos_deveAtualizarTudo() {
        Topico topico = criarTopicoBase();

        topico.atualizar("Novo Título", "Nova Mensagem", "Spring Boot");

        assertEquals("Novo Título", topico.getTitulo());
        assertEquals("Nova Mensagem", topico.getMensagem());
        assertEquals("Spring Boot", topico.getCurso());
    }

    @Test
    @DisplayName("Não deve alterar o título quando passado como null")
    void atualizar_comTituloNulo_naoDeveAlterarTitulo() {
        Topico topico = criarTopicoBase();

        topico.atualizar(null, "Nova Mensagem", "Spring Boot");

        assertEquals("Título Original", topico.getTitulo());
        assertEquals("Nova Mensagem", topico.getMensagem());
        assertEquals("Spring Boot", topico.getCurso());
    }

    @Test
    @DisplayName("Não deve alterar a mensagem quando passada como null")
    void atualizar_comMensagemNula_naoDeveAlterarMensagem() {
        Topico topico = criarTopicoBase();

        topico.atualizar("Novo Título", null, "Spring Boot");

        assertEquals("Novo Título", topico.getTitulo());
        assertEquals("Mensagem Original", topico.getMensagem());
        assertEquals("Spring Boot", topico.getCurso());
    }

    @Test
    @DisplayName("Não deve alterar o curso quando passado como null")
    void atualizar_comCursoNulo_naoDeveAlterarCurso() {
        Topico topico = criarTopicoBase();

        topico.atualizar("Novo Título", "Nova Mensagem", null);

        assertEquals("Novo Título", topico.getTitulo());
        assertEquals("Nova Mensagem", topico.getMensagem());
        assertEquals("Java Fundamentals", topico.getCurso());
    }

    @Test
    @DisplayName("Não deve alterar nenhum campo quando todos os valores são null")
    void atualizar_comTudoNulo_naoDeveAlterarNada() {
        Topico topico = criarTopicoBase();

        topico.atualizar(null, null, null);

        assertEquals("Título Original", topico.getTitulo());
        assertEquals("Mensagem Original", topico.getMensagem());
        assertEquals("Java Fundamentals", topico.getCurso());
    }

    @Test
    @DisplayName("Deve preservar id, status, autor e dataCriacao após atualização")
    void atualizar_naoDeveAlterarCamposImutaveis() {
        Topico topico = criarTopicoBase();
        LocalDateTime dataAntes = topico.getDataCriacao();

        topico.atualizar("Novo Título", "Nova Mensagem", "DevOps");

        assertEquals(10L, topico.getId());
        assertEquals("ATIVO", topico.getStatus());
        assertEquals(autor, topico.getAutor());
        assertEquals(dataAntes, topico.getDataCriacao());
    }
}
