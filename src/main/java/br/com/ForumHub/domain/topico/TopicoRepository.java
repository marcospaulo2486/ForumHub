package br.com.ForumHub.domain.topico;

import br.com.ForumHub.domain.topico.Topico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopicoRepository extends JpaRepository<Topico, Long> {

    Optional<Topico> findByTituloAndMensagem(String titulo, String mensagem);

    Optional<Topico> findByTituloAndMensagemAndIdNot(String titulo, String mensagem, Long id);

}



