package br.com.ForumHub.controller;

import br.com.ForumHub.domain.Usuario.Usuario;
import br.com.ForumHub.domain.Usuario.UsuarioRepository;
import br.com.ForumHub.domain.resposta.Resposta;
import br.com.ForumHub.domain.resposta.RespostaRepository;
import br.com.ForumHub.domain.topico.Topico;
import br.com.ForumHub.domain.topico.TopicoRepository;
import br.com.ForumHub.domain.topico.ValidacaoRegra;
import br.com.ForumHub.infra.security.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = TopicoController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
class TopicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TopicoRepository topicoRepository;

    @MockBean
    private ValidacaoRegra validacaoRegra;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private RespostaRepository respostaRepository;

    private Usuario usuarioLogado;
    private Topico topicoDoUsuario;

    @BeforeEach
    void setUp() {
        usuarioLogado = new Usuario(1L, "autor@forum.com", "hash_senha");

        topicoDoUsuario = new Topico(
                10L,
                "Título do Tópico",
                "Mensagem do Tópico",
                LocalDateTime.of(2025, 6, 1, 12, 0),
                "ATIVO",
                usuarioLogado,
                "Spring Boot"
        );
    }

    private void autenticarComo(Usuario usuario) {
        var auth = new UsernamePasswordAuthenticationToken(
                usuario, null, usuario.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("GET /topicos deve retornar 200 e lista vazia")
    void listar_semAutenticacao_deveRetornar200ComListaVazia() throws Exception {
        when(topicoRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/topicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /topicos deve retornar lista com um tópico quando houver dados")
    void listar_comUmTopicoCadastrado_deveRetornarListaComUmItem() throws Exception {
        when(topicoRepository.findAll()).thenReturn(List.of(topicoDoUsuario));

        mockMvc.perform(get("/topicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Título do Tópico"))
                .andExpect(jsonPath("$[0].curso").value("Spring Boot"));
    }

    @Test
    @DisplayName("GET /topicos/{id} deve retornar 200 com o tópico quando o ID existe")
    void buscarPorId_comIdExistente_deveRetornar200() throws Exception {
        when(topicoRepository.findById(10L)).thenReturn(Optional.of(topicoDoUsuario));

        mockMvc.perform(get("/topicos/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.titulo").value("Título do Tópico"));
    }

    @Test
    @DisplayName("GET /topicos/{id} deve retornar 404 quando o ID não existe")
    void buscarPorId_comIdInexistente_deveRetornar404() throws Exception {
        when(topicoRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/topicos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /topicos com título em branco deve retornar 400")
    void cadastrar_comTituloEmBranco_deveRetornar400() throws Exception {
        autenticarComo(usuarioLogado);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioLogado));

        String json = """
                {
                    "titulo": "",
                    "mensagem": "Mensagem válida aqui",
                    "curso": "Java"
                }
                """;

        mockMvc.perform(post("/topicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /topicos com mensagem em branco deve retornar 400")
    void cadastrar_comMensagemEmBranco_deveRetornar400() throws Exception {
        autenticarComo(usuarioLogado);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioLogado));

        String json = """
                {
                    "titulo": "Título válido",
                    "mensagem": "",
                    "curso": "Java"
                }
                """;

        mockMvc.perform(post("/topicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /topicos com curso em branco deve retornar 400")
    void cadastrar_comCursoEmBranco_deveRetornar400() throws Exception {
        autenticarComo(usuarioLogado);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioLogado));

        String json = """
                {
                    "titulo": "Título válido",
                    "mensagem": "Mensagem válida",
                    "curso": ""
                }
                """;

        mockMvc.perform(post("/topicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /topicos com tópico duplicado deve retornar 400")
    void cadastrar_comTopicoDuplicado_deveRetornar400() throws Exception {
        autenticarComo(usuarioLogado);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioLogado));
        doThrow(new IllegalArgumentException("Tópico duplicado! Já existe um tópico com o mesmo título e mensagem."))
                .when(validacaoRegra).validarTopicoDuplicado(any(), any());

        String json = """
                {
                    "titulo": "Título Duplicado",
                    "mensagem": "Mensagem Duplicada",
                    "curso": "Java"
                }
                """;

        mockMvc.perform(post("/topicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /topicos/{id} deve retornar 204 quando o tópico existe")
    void deletar_comIdExistente_deveRetornar204() throws Exception {
        when(topicoRepository.existsById(10L)).thenReturn(true);

        mockMvc.perform(delete("/topicos/10"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /topicos/{id} deve retornar 404 quando o tópico não existe")
    void deletar_comIdInexistente_deveRetornar404() throws Exception {
        when(topicoRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/topicos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /topicos/{id} deve retornar 404 quando o tópico não existe")
    void atualizar_comIdInexistente_deveRetornar404() throws Exception {
        autenticarComo(usuarioLogado);
        when(topicoRepository.findById(99L)).thenReturn(Optional.empty());

        String json = """
                {
                    "titulo": "Novo Título",
                    "mensagem": "Nova Mensagem",
                    "curso": "Spring"
                }
                """;

        mockMvc.perform(put("/topicos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /topicos/{id} deve retornar 403 quando o usuário não é o dono do tópico")
    void atualizar_comUsuarioNaoDono_deveRetornar403() throws Exception {
        Usuario outroDono = new Usuario(2L, "outro@forum.com", "hash_outro");
        Topico topicoDeOutroUsuario = new Topico(
                10L,
                "Título do Tópico",
                "Mensagem do Tópico",
                LocalDateTime.of(2025, 6, 1, 12, 0),
                "ATIVO",
                outroDono,
                "Spring Boot"
        );
        autenticarComo(usuarioLogado);
        when(topicoRepository.findById(10L)).thenReturn(Optional.of(topicoDeOutroUsuario));

        String json = """
                {
                    "titulo": "Tentativa de alteração",
                    "mensagem": "Invasão",
                    "curso": "Hacking"
                }
                """;

        mockMvc.perform(put("/topicos/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /topicos/{id} deve retornar 200 quando o usuário é o dono e dados são válidos")
    void atualizar_comUsuarioDono_deveRetornar200() throws Exception {
        autenticarComo(usuarioLogado);
        when(topicoRepository.findById(10L)).thenReturn(Optional.of(topicoDoUsuario));

        String json = """
                {
                    "titulo": "Título Atualizado",
                    "mensagem": "Mensagem Atualizada",
                    "curso": "Spring Boot Avançado"
                }
                """;

        mockMvc.perform(put("/topicos/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    @DisplayName("POST /topicos com dados válidos deve retornar 201 com body do tópico criado")
    void cadastrar_comDadosValidos_deveRetornar201ComBody() throws Exception {
        autenticarComo(usuarioLogado);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioLogado));

        String json = """
                {
                    "titulo": "Novo Tópico",
                    "mensagem": "Mensagem do novo tópico",
                    "curso": "Java"
                }
                """;

        mockMvc.perform(post("/topicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Novo Tópico"))
                .andExpect(jsonPath("$.mensagem").value("Mensagem do novo tópico"))
                .andExpect(jsonPath("$.curso").value("Java"))
                .andExpect(jsonPath("$.status").value("ATIVO"))
                .andExpect(jsonPath("$.autor").value("autor@forum.com"));
    }

    @Test
    @DisplayName("POST /topicos deve retornar 400 quando usuário autenticado não é encontrado no banco")
    void cadastrar_comUsuarioNaoEncontrado_deveRetornar400() throws Exception {
        autenticarComo(usuarioLogado);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        String json = """
                {
                    "titulo": "Título Válido",
                    "mensagem": "Mensagem Válida",
                    "curso": "Java"
                }
                """;

        mockMvc.perform(post("/topicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /topicos/{id} deve retornar 400 quando tópico duplicado na atualização")
    void atualizar_comTopicoDuplicado_deveRetornar400() throws Exception {
        autenticarComo(usuarioLogado);
        when(topicoRepository.findById(10L)).thenReturn(Optional.of(topicoDoUsuario));
        doThrow(new IllegalArgumentException("Tópico duplicado! Já existe um tópico com o mesmo título e mensagem."))
                .when(validacaoRegra).validarTopicoDuplicadoUpdate(any(), any(), any());

        String json = """
                {
                    "titulo": "Título Duplicado",
                    "mensagem": "Mensagem Duplicada",
                    "curso": "Java"
                }
                """;

        mockMvc.perform(put("/topicos/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /topicos/{id}/respostas deve retornar 200 com lista vazia quando não há respostas")
    void listarRespostas_comTopicoExistente_deveRetornar200ListaVazia() throws Exception {
        when(topicoRepository.findById(10L)).thenReturn(Optional.of(topicoDoUsuario));
        when(respostaRepository.findByTopicoIdOrderByDataCriacaoAsc(10L)).thenReturn(List.of());

        mockMvc.perform(get("/topicos/10/respostas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /topicos/{id}/respostas deve retornar 200 com respostas quando existem")
    void listarRespostas_comRespostasExistente_deveRetornar200ComLista() throws Exception {
        var resposta = new Resposta(
                1L,
                "Resposta teste",
                LocalDateTime.of(2025, 7, 15, 10, 0),
                topicoDoUsuario,
                usuarioLogado,
                false
        );
        when(topicoRepository.findById(10L)).thenReturn(Optional.of(topicoDoUsuario));
        when(respostaRepository.findByTopicoIdOrderByDataCriacaoAsc(10L)).thenReturn(List.of(resposta));

        mockMvc.perform(get("/topicos/10/respostas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].mensagem").value("Resposta teste"))
                .andExpect(jsonPath("$[0].autor").value("autor@forum.com"))
                .andExpect(jsonPath("$[0].solucao").value(false));
    }

    @Test
    @DisplayName("GET /topicos/{id}/respostas deve retornar 404 quando tópico não existe")
    void listarRespostas_comTopicoInexistente_deveRetornar404() throws Exception {
        when(topicoRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/topicos/99/respostas"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /topicos/{id}/respostas com dados válidos deve retornar 201")
    void cadastrarResposta_comDadosValidos_deveRetornar201() throws Exception {
        autenticarComo(usuarioLogado);
        when(topicoRepository.findById(10L)).thenReturn(Optional.of(topicoDoUsuario));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioLogado));

        String json = """
                {
                    "mensagem": "Minha resposta para o tópico"
                }
                """;

        mockMvc.perform(post("/topicos/10/respostas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensagem").value("Minha resposta para o tópico"))
                .andExpect(jsonPath("$.autor").value("autor@forum.com"))
                .andExpect(jsonPath("$.solucao").value(false));

        verify(respostaRepository).save(any());
    }

    @Test
    @DisplayName("POST /topicos/{id}/respostas com mensagem em branco deve retornar 400")
    void cadastrarResposta_comMensagemEmBranco_deveRetornar400() throws Exception {
        autenticarComo(usuarioLogado);
        when(topicoRepository.findById(10L)).thenReturn(Optional.of(topicoDoUsuario));

        String json = """
                {
                    "mensagem": ""
                }
                """;

        mockMvc.perform(post("/topicos/10/respostas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(respostaRepository, never()).save(any());
    }

    @Test
    @DisplayName("POST /topicos/{id}/respostas com mensagem nula deve retornar 400")
    void cadastrarResposta_comMensagemNula_deveRetornar400() throws Exception {
        autenticarComo(usuarioLogado);
        when(topicoRepository.findById(10L)).thenReturn(Optional.of(topicoDoUsuario));

        String json = """
                {}
                """;

        mockMvc.perform(post("/topicos/10/respostas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(respostaRepository, never()).save(any());
    }

    @Test
    @DisplayName("POST /topicos/{id}/respostas com tópico inexistente deve retornar 404")
    void cadastrarResposta_comTopicoInexistente_deveRetornar404() throws Exception {
        autenticarComo(usuarioLogado);
        when(topicoRepository.findById(99L)).thenReturn(Optional.empty());

        String json = """
                {
                    "mensagem": "Resposta para tópico inexistente"
                }
                """;

        mockMvc.perform(post("/topicos/99/respostas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());

        verify(respostaRepository, never()).save(any());
    }
}
