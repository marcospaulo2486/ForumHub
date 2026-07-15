package br.com.ForumHub.domain.Usuario;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados necessários para autenticação")
public record DadosAutenticacao(
        @Schema(description = "E-mail do usuário", example = "usuario@email.com")
        String login,

        @Schema(description = "Senha do usuário", example = "123456")
        String senha
) {
}
