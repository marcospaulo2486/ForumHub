package br.com.ForumHub.controller;

import br.com.ForumHub.domain.Usuario.UsuarioRepository;
import br.com.ForumHub.infra.security.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = CadastroController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
class CadastroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private TokenService tokenService;

    @Test
    @DisplayName("POST /register com dados válidos deve retornar 201")
    void cadastrar_comDadosValidos_deveRetornar201() throws Exception {
        when(usuarioRepository.findByLogin("novo@email.com")).thenReturn(null);
        when(passwordEncoder.encode("senha123")).thenReturn("hash_gerado");

        String json = """
                {
                    "login": "novo@email.com",
                    "senha": "senha123"
                }
                """;

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        verify(usuarioRepository).save(any());
    }

    @Test
    @DisplayName("POST /register com login duplicado deve retornar 400")
    void cadastrar_comLoginDuplicado_deveRetornar400() throws Exception {
        when(usuarioRepository.findByLogin("existente@email.com"))
                .thenReturn(new org.springframework.security.core.userdetails.User(
                        "existente@email.com", "hash", java.util.List.of()));

        String json = """
                {
                    "login": "existente@email.com",
                    "senha": "senha123"
                }
                """;

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("POST /register com login em branco deve retornar 400")
    void cadastrar_comLoginEmBranco_deveRetornar400() throws Exception {
        String json = """
                {
                    "login": "",
                    "senha": "senha123"
                }
                """;

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /register com senha em branco deve retornar 400")
    void cadastrar_comSenhaEmBranco_deveRetornar400() throws Exception {
        String json = """
                {
                    "login": "novo@email.com",
                    "senha": ""
                }
                """;

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /register com login nulo deve retornar 400")
    void cadastrar_comLoginNulo_deveRetornar400() throws Exception {
        String json = """
                {
                    "senha": "senha123"
                }
                """;

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /register com senha nula deve retornar 400")
    void cadastrar_comSenhaNula_deveRetornar400() throws Exception {
        String json = """
                {
                    "login": "novo@email.com"
                }
                """;

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}
