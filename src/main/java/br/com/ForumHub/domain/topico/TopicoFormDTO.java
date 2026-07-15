package br.com.ForumHub.domain.topico;

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
@Schema(description = "Dados necessários para criar ou atualizar um tópico")
public class TopicoFormDTO {
    @NotBlank(message = "O título não pode estar em branco")
    @Schema(description = "Título do tópico (deve ser único)", example = "Como usar Optional no Java?")
    private String titulo;

    @NotBlank(message = "A mensagem não pode estar em branco")
    @Schema(description = "Corpo/mensagem do tópico (deve ser único)", example = "Gostaria de entender como funciona o Optional...")
    private String mensagem;

    @NotBlank(message = "O curso não pode estar em branco")
    @Schema(description = "Categoria/curso do tópico", example = "Java")
    private String curso;
}
