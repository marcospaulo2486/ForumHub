package br.com.ForumHub.controller;


import br.com.ForumHub.domain.topico.Topico;
import br.com.ForumHub.domain.topico.TopicoDetalheDTO;
import br.com.ForumHub.domain.topico.TopicoFormDTO;
import br.com.ForumHub.domain.topico.TopicoRepository;
import br.com.ForumHub.domain.topico.ValidacaoRegra;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/topicos")
public class TopicoController {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private ValidacaoRegra validacaoRegra;

    @PostMapping
    public ResponseEntity<Topico> criar(@RequestBody @Valid TopicoFormDTO dados, UriComponentsBuilder uriBuilder) {
        validacaoRegra.validarTopicoDuplicado(dados.getTitulo(), dados.getMensagem());

        var novoTopico = new Topico(null, dados.getTitulo(), dados.getMensagem(), LocalDateTime.now(), "ATIVO",
                dados.getAutor(), dados.getCurso(), null);
        topicoRepository.save(novoTopico);

        URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(novoTopico.getId()).toUri();

        return ResponseEntity.created(uri).body(novoTopico);
    }
    @GetMapping("/{id}")
    public ResponseEntity<TopicoDetalheDTO> buscarPorId(@PathVariable Long id) {
        Optional<Topico> topicoEncontrado = topicoRepository.findById(id);

        if (topicoEncontrado.isPresent()) {
            return ResponseEntity.ok(new TopicoDetalheDTO(topicoEncontrado.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping
    public ResponseEntity<List<TopicoDetalheDTO>> listar() {
        List<Topico> topicos = topicoRepository.findAll();
        List<TopicoDetalheDTO> listaDTO = topicos.stream()
                .map(TopicoDetalheDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(listaDTO);
    }
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<TopicoDetalheDTO> atualizar(@PathVariable Long id, @RequestBody @Valid TopicoFormDTO dados) {
        Optional<Topico> optionalTopico = topicoRepository.findById(id);

        if (optionalTopico.isPresent()) {
            validacaoRegra.validarTopicoDuplicadoUpdate(dados.getTitulo(), dados.getMensagem(), id);

            var topico = optionalTopico.get();
            topico.atualizar(dados.getTitulo(), dados.getMensagem(), dados.getAutor(), dados.getCurso());

            return ResponseEntity.ok(new TopicoDetalheDTO(topico));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (topicoRepository.existsById(id)) {
            topicoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}



