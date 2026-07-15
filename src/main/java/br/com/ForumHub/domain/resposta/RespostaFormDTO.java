package br.com.ForumHub.domain.resposta;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para cadastrar uma resposta em um tópico")
public class RespostaFormDTO {

    @NotNull(message = "Mensagem não pode ser nula")
    @NotBlank(message = "Mensagem não pode ficar em branco")
    @Schema(description = "Conteúdo da resposta", example = "Use Optional.ofNullable() para tratar valores nulos")
    private String mensagem;
}
