package br.com.ForumHub.domain.resposta;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Schema(description = "Detalhes de uma resposta retornado pela API")
public class RespostaDetalheDTO {

    @Schema(description = "ID da resposta", example = "1")
    private Long id;

    @Schema(description = "Conteúdo da resposta", example = "Use Optional.ofNullable()...")
    private String mensagem;

    @Schema(description = "Data de criação da resposta", example = "2025-07-15T10:30:00")
    private LocalDateTime dataCriacao;

    @Schema(description = "E-mail do autor da resposta", example = "usuario@email.com")
    private String autor;

    @Schema(description = "Se a resposta é a solução aceita", example = "false")
    private boolean solucao;

    public RespostaDetalheDTO(Resposta resposta) {
        this.id = resposta.getId();
        this.mensagem = resposta.getMensagem();
        this.dataCriacao = resposta.getDataCriacao();
        this.autor = resposta.getAutor().getUsername();
        this.solucao = resposta.isSolucao();
    }
}
