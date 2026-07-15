package br.com.ForumHub.infra.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigurationsTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /topicos deve retornar 200 sem autenticação")
    void listar_topicos_semAuth_deveRetornar200() throws Exception {
        mockMvc.perform(get("/topicos"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /topicos/{id} deve permitir acesso sem autenticação")
    void buscarPorId_semAuth_deveLiberarAcesso() throws Exception {
        mockMvc.perform(get("/topicos/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /login deve permitir acesso sem autenticação (retorna 401 por credenciais inválidas)")
    void login_semAuth_deveLiberarAcesso() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /topicos deve retornar 403 sem autenticação")
    void cadastrar_topico_semAuth_deveRetornar403() throws Exception {
        mockMvc.perform(post("/topicos")
                        .contentType("application/json")
                        .content("{\"titulo\":\"t\",\"mensagem\":\"m\",\"curso\":\"c\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /topicos/1 deve retornar 403 sem autenticação")
    void deletar_topico_semAuth_deveRetornar403() throws Exception {
        mockMvc.perform(delete("/topicos/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /topicos/1 deve retornar 403 sem autenticação")
    void atualizar_topico_semAuth_deveRetornar403() throws Exception {
        mockMvc.perform(put("/topicos/1")
                        .contentType("application/json")
                        .content("{\"titulo\":\"t\",\"mensagem\":\"m\",\"curso\":\"c\"}"))
                .andExpect(status().isForbidden());
    }
}
