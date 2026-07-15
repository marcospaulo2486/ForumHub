package br.com.ForumHub.domain.Usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados necessários para cadastro de novo usuário")
public class DadosCadastroUsuario {

    @NotBlank(message = "O login não pode estar em branco")
    @Schema(description = "E-mail do usuário (usado como login)", example = "usuario@email.com")
    private String login;

    @NotBlank(message = "A senha não pode estar em branco")
    @Schema(description = "Senha do usuário", example = "123456")
    private String senha;
}
