package br.com.ForumHub.domain.Usuario;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioTest {

    @Test
    @DisplayName("getAuthorities deve retornar ROLE_USER")
    void getAuthorities_deveRetornarRoleUser() {
        Usuario usuario = new Usuario(1L, "user@email.com", "hash");

        var authorities = usuario.getAuthorities();

        assertEquals(1, authorities.size());
        assertEquals("ROLE_USER", authorities.iterator().next().getAuthority());
    }

    @Test
    @DisplayName("getPassword deve retornar a senha")
    void getPassword_deveRetornarSenha() {
        Usuario usuario = new Usuario(1L, "user@email.com", "minha_senha");

        assertEquals("minha_senha", usuario.getPassword());
    }

    @Test
    @DisplayName("getUsername deve retornar o login")
    void getUsername_deveRetornarLogin() {
        Usuario usuario = new Usuario(1L, "user@email.com", "hash");

        assertEquals("user@email.com", usuario.getUsername());
    }

    @Test
    @DisplayName("isAccountNonExpired deve retornar true")
    void isAccountNonExpired_deveRetornarTrue() {
        Usuario usuario = new Usuario(1L, "user@email.com", "hash");

        assertTrue(usuario.isAccountNonExpired());
    }

    @Test
    @DisplayName("isAccountNonLocked deve retornar true")
    void isAccountNonLocked_deveRetornarTrue() {
        Usuario usuario = new Usuario(1L, "user@email.com", "hash");

        assertTrue(usuario.isAccountNonLocked());
    }

    @Test
    @DisplayName("isCredentialsNonExpired deve retornar true")
    void isCredentialsNonExpired_deveRetornarTrue() {
        Usuario usuario = new Usuario(1L, "user@email.com", "hash");

        assertTrue(usuario.isCredentialsNonExpired());
    }

    @Test
    @DisplayName("isEnabled deve retornar true")
    void isEnabled_deveRetornarTrue() {
        Usuario usuario = new Usuario(1L, "user@email.com", "hash");

        assertTrue(usuario.isEnabled());
    }
}
