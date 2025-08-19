package br.com.ForumHub.domain.topico;


import br.com.ForumHub.domain.Usuario.Usuario;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TopicoFormDTO {

    @NotBlank(message = "O título não pode estar em branco")
    private String titulo;

    @NotBlank(message = "A mensagem não pode estar em branco")
    private String mensagem;

    @NotBlank(message = "O autor não pode estar em branco")
    private Usuario autor;

    @NotBlank(message = "O curso não pode estar em branco")
    private String curso;

}
