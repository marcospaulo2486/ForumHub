package br.com.ForumHub.domain.topico;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValidacaoRegra {


    @Autowired
    private TopicoRepository topicoRepository;

    public void validarTopicoDuplicado(String titulo, String mensagem) {
        if (topicoRepository.findByTituloAndMensagem(titulo, mensagem).isPresent()) {
            throw new IllegalArgumentException("Tópico duplicado! Já existe um tópico com o mesmo título e mensagem.");
        }
    }

    public void validarTopicoDuplicadoUpdate(String titulo, String mensagem, Long id) {
        if (topicoRepository.findByTituloAndMensagemAndIdNot(titulo, mensagem, id).isPresent()) {
            throw new IllegalArgumentException("Tópico duplicado! Já existe um tópico com o mesmo título e mensagem.");
        }
    }


}
