package br.com.ForumHub.domain.topico;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Schema(description = "Detalhes de um tópico retornado pela API")
public class TopicoDetalheDTO {

    @Schema(description = "ID do tópico", example = "1")
    private Long id;

    @Schema(description = "Título do tópico", example = "Como usar Optional no Java?")
    private String titulo;

    @Schema(description = "Corpo/mensagem do tópico", example = "Gostaria de entender como funciona o Optional...")
    private String mensagem;

    @Schema(description = "Data de criação do tópico", example = "2025-07-14T22:15:31")
    private LocalDateTime dataCriacao;

    @Schema(description = "Status do tópico", example = "ATIVO")
    private String status;

    @Schema(description = "E-mail do autor do tópico", example = "usuario@email.com")
    private String autor;

    @Schema(description = "Categoria/curso do tópico", example = "Java")
    private String curso;

    public TopicoDetalheDTO(Topico topico) {
        this.id = topico.getId();
        this.titulo = topico.getTitulo();
        this.mensagem = topico.getMensagem();
        this.dataCriacao = topico.getDataCriacao();
        this.status = topico.getStatus();
        this.autor = topico.getAutor().getUsername();
        this.curso = topico.getCurso();
    }
}
