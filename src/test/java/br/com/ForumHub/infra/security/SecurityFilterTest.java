package br.com.ForumHub.infra.security;

import br.com.ForumHub.domain.Usuario.Usuario;
import br.com.ForumHub.domain.Usuario.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityFilterTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private UsuarioRepository repository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private SecurityFilter securityFilter;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve passar adiante quando não há token no header")
    void doFilterInternal_semToken_devePassarAdiante() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        securityFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Deve autenticar quando token é válido")
    void doFilterInternal_comTokenValido_deveAutenticar() throws Exception {
        Usuario usuario = new Usuario(1L, "user@email.com", "hash123");

        when(request.getHeader("Authorization")).thenReturn("Bearer token.valido.jwt");
        when(tokenService.getSubject("token.valido.jwt")).thenReturn("user@email.com");
        when(repository.findByLogin("user@email.com")).thenReturn(usuario);

        securityFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(usuario, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Test
    @DisplayName("Deve propagar exceção quando token é inválido")
    void doFilterInternal_comTokenInvalido_deveLancarExcecao() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token.invalido");
        when(tokenService.getSubject("token.invalido"))
                .thenThrow(new RuntimeException("Token JWT inválido ou expirado!"));

        assertThrows(RuntimeException.class,
                () -> securityFilter.doFilterInternal(request, response, filterChain));

        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve passar adiante quando header Authorization não é Bearer")
    void doFilterInternal_comHeaderInvalido_devePassarAdiante() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        securityFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

}
