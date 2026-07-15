package br.com.ForumHub.domain.Usuario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private AutenticacaoService service;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario(1L, "user@email.com", "hash123");
    }

    @Test
    @DisplayName("Deve carregar usuário pelo login quando existe")
    void loadUserByUsername_comLoginExistente_deveRetornarUsuario() {
        when(repository.findByLogin("user@email.com")).thenReturn(usuario);

        var resultado = service.loadUserByUsername("user@email.com");

        assertEquals(usuario, resultado);
    }

    @Test
    @DisplayName("Deve lançar exceção quando login não existe")
    void loadUserByUsername_comLoginInexistente_deveLancarExcecao() {
        when(repository.findByLogin("inexistente@email.com")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("inexistente@email.com"));
    }

}
