package br.com.ForumHub.domain.topico;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class TopicoDetalheDTO {
    private Long id;
    private String titulo;
    private String mensagem;
    private LocalDateTime dataCriacao;
    private String status;
    private String autor; // 
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

