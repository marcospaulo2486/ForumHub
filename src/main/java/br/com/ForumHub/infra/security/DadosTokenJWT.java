package br.com.ForumHub.infra.security;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta com o token JWT gerado")
public record DadosTokenJWT(
        @Schema(description = "Token JWT para autenticação nas requisições", example = "eyJhbGciOiJIUzI1NiJ9...")
        String token
) {
}
