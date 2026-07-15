package br.com.ForumHub.infra;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TratadorDeErrosTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController(), new TestValidatedController())
                .setControllerAdvice(new TratadorDeErros())
                .build();
    }

    @Test
    @DisplayName("EntityNotFoundException deve retornar 404")
    void entityNotFoundException_deveRetornar404() throws Exception {
        mockMvc.perform(get("/test/entity-not-found"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("HttpMessageNotReadableException deve retornar 400 com mensagem")
    void httpMessageNotReadableException_deveRetornar400() throws Exception {
        mockMvc.perform(get("/test/message-not-readable"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Requisição mal formada"));
    }

    @Test
    @DisplayName("IllegalArgumentException deve retornar 400 com mensagem")
    void illegalArgumentException_deveRetornar400() throws Exception {
        mockMvc.perform(get("/test/illegal-argument"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Erro de negócio"));
    }

    @Test
    @DisplayName("AuthenticationException deve retornar 401")
    void authenticationException_deveRetornar401() throws Exception {
        mockMvc.perform(get("/test/authentication-error"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Falha na autenticação"));
    }

    @Test
    @DisplayName("AccessDeniedException deve retornar 403")
    void accessDeniedException_deveRetornar403() throws Exception {
        mockMvc.perform(get("/test/access-denied"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Acesso negado"));
    }

    @Test
    @DisplayName("Exception genérica deve retornar 500 com mensagem")
    void exceptionGenerica_deveRetornar500() throws Exception {
        mockMvc.perform(get("/test/generic-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Erro: Erro interno genérico"));
    }

    @Test
    @DisplayName("MethodArgumentNotValidException deve retornar 400 com lista de erros de campo")
    void methodArgumentNotValidException_deveRetornar400ComErrosDeCampo() throws Exception {
        mockMvc.perform(post("/test/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].campo").value(org.hamcrest.Matchers.hasItems("titulo", "mensagem")))
                .andExpect(jsonPath("$[*].mensagem").value(org.hamcrest.Matchers.hasItem("must not be blank")));
    }

    @RestController
    static class TestController {
        @GetMapping("/test/entity-not-found")
        public void throwEntityNotFound() {
            throw new EntityNotFoundException();
        }

        @GetMapping("/test/message-not-readable")
        public void throwMessageNotReadable() {
            throw new HttpMessageNotReadableException("Requisição mal formada");
        }

        @GetMapping("/test/illegal-argument")
        public void throwIllegalArgument() {
            throw new IllegalArgumentException("Erro de negócio");
        }

        @GetMapping("/test/authentication-error")
        public void throwAuthenticationError() {
            throw new AuthenticationException("Falha na autenticação") {};
        }

        @GetMapping("/test/access-denied")
        public void throwAccessDenied() {
            throw new AccessDeniedException("Acesso negado");
        }

        @GetMapping("/test/generic-error")
        public void throwGenericError() {
            throw new RuntimeException("Erro interno genérico");
        }
    }

    static record DadosTest(@jakarta.validation.constraints.NotBlank String titulo,
                            @jakarta.validation.constraints.NotBlank String mensagem) {}

    @RestController
    static class TestValidatedController {
        @PostMapping("/test/validate")
        public void validate(@jakarta.validation.Valid @RequestBody DadosTest dados) {
        }
    }

}
