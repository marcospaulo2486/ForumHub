package br.com.ForumHub.domain.topico;

import br.com.ForumHub.domain.Usuario.DadosAutenticacao;
import br.com.ForumHub.domain.Usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Table(name = "topicos")
@Entity(name = "topico")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Topico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String mensagem;
    private LocalDateTime dataCriacao;
    private String status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario autor;
    private String curso;



    public void atualizar(String titulo, String mensagem, String curso) {
        
        if (titulo != null) {
            this.titulo = titulo;
        }
        if (mensagem != null) {
            this.mensagem = mensagem;
        }
        if (curso != null) {
            this.curso = curso;
        }
       
    }
}
