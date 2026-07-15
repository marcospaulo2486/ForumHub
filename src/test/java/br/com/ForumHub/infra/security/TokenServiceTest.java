package br.com.ForumHub.infra.security;

import br.com.ForumHub.domain.Usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenServiceTest {

    private TokenService tokenService;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "segredo-de-teste-unitario-hmac256");
        usuario = new Usuario(1L, "usuario@forumhub.com", "senha_irrelevante_aqui");
    }

    @Test
    @DisplayName("Deve gerar um token JWT não nulo e não vazio para um usuário válido")
    void gerarToken_comUsuarioValido_deveRetornarTokenNaoVazio() {
        String token = tokenService.gerarToken(usuario);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    @DisplayName("Deve gerar token com formato JWT de três partes separadas por ponto")
    void gerarToken_comUsuarioValido_deveRetornarTokenComFormatoJWT() {
        String token = tokenService.gerarToken(usuario);

        String[] partes = token.split("\\.");
        assertEquals(3, partes.length);
    }

    @Test
    @DisplayName("Deve recuperar o login do usuário como subject do token gerado")
    void getSubject_comTokenValido_deveRetornarLoginDoUsuario() {
        String token = tokenService.gerarToken(usuario);

        String subject = tokenService.getSubject(token);

        assertEquals("usuario@forumhub.com", subject);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException para token completamente inválido")
    void getSubject_comTokenMalformado_deveLancarRuntimeException() {
        RuntimeException excecao = assertThrows(
                RuntimeException.class,
                () -> tokenService.getSubject("nao.e.um.jwt.valido")
        );

        assertEquals("Token JWT inválido ou expirado!", excecao.getMessage());
    }

    @Test
    @DisplayName("Deve lançar RuntimeException para token gerado com secret diferente")
    void getSubject_comTokenDeOutroSecret_deveLancarRuntimeException() {
        TokenService outroService = new TokenService();
        ReflectionTestUtils.setField(outroService, "secret", "secret-completamente-diferente-xyz");
        String tokenDeOutroSecret = outroService.gerarToken(usuario);

        assertThrows(
                RuntimeException.class,
                () -> tokenService.getSubject(tokenDeOutroSecret)
        );
    }

    @Test
    @DisplayName("Deve lançar RuntimeException para token adulterado (assinatura alterada)")
    void getSubject_comTokenAdulterado_deveLancarRuntimeException() {
        String tokenValido = tokenService.gerarToken(usuario);
        String tokenAdulterado = tokenValido.substring(0, tokenValido.length() - 5) + "XXXXX";

        assertThrows(
                RuntimeException.class,
                () -> tokenService.getSubject(tokenAdulterado)
        );
    }

    @Test
    @DisplayName("Deve lançar RuntimeException para string vazia")
    void getSubject_comStringVazia_deveLancarRuntimeException() {
        assertThrows(
                RuntimeException.class,
                () -> tokenService.getSubject("")
        );
    }

    @Test
    @DisplayName("Deve gerar token com expiração de aproximadamente 2 horas")
    void gerarToken_comExpiracaoDeDuasHoras() {
        String token = tokenService.gerarToken(usuario);

        DecodedJWT decoded = JWT.decode(token);
        Instant expiresAt = decoded.getExpiresAt().toInstant();
        Instant now = Instant.now();
        long hoursBetween = ChronoUnit.HOURS.between(now, expiresAt);

        assertTrue(hoursBetween >= 1 && hoursBetween <= 2,
                "Token deve expirar em até 2 horas, mas expira em " + hoursBetween + " horas");
    }
}
