package br.com.ForumHub.controller;

import br.com.ForumHub.domain.Usuario.DadosAutenticacao;
import br.com.ForumHub.domain.Usuario.Usuario;
import br.com.ForumHub.domain.Usuario.UsuarioRepository;
import br.com.ForumHub.infra.security.DadosTokenJWT;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = AutenticacaoController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
class AutenticacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager manager;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("POST /login com credenciais válidas deve retornar 200 com token")
    void efetuarLogin_comCredenciaisValidas_deveRetornar200() throws Exception {
        Usuario usuario = new Usuario(1L, "user@email.com", "senha123");
        Authentication authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());

        when(manager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenService.gerarToken(usuario)).thenReturn("token.jwt.aqui");

        String json = """
                {
                    "login": "user@email.com",
                    "senha": "senha123"
                }
                """;

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token.jwt.aqui"));
    }

    @Test
    @DisplayName("POST /login com credenciais inválidas deve retornar 401")
    void efetuarLogin_comCredenciaisInvalidas_deveRetornar401() throws Exception {
        when(manager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        String json = """
                {
                    "login": "user@email.com",
                    "senha": "senha_errada"
                }
                """;

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /login com campos em branco deve retornar 400 ou 500 (sem validação @NotBlank no DTO)")
    void efetuarLogin_comCamposEmBranco_deveRetornarErro() throws Exception {
        String json = """
                {
                    "login": "",
                    "senha": ""
                }
                """;

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is5xxServerError());
    }

}
