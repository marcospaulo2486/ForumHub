package br.com.ForumHub.controller;

import br.com.ForumHub.domain.Usuario.UsuarioRepository;
import br.com.ForumHub.domain.Usuario.Usuario;
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
import org.springframework.security.core.context.SecurityContextHolder;

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

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid TopicoFormDTO dados, UriComponentsBuilder uriBuilder) {

        // 1. Validar se não é tópico duplicado
        validacaoRegra.validarTopicoDuplicado(dados.getTitulo(), dados.getMensagem());

        // 2. Pegar usuário autenticado do contexto de segurança
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var usuarioLogado = (Usuario) authentication.getPrincipal();

        // 3. Buscar usuário completo do banco
        Usuario autor = usuarioRepository.findById(usuarioLogado.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // 4. Criar tópico com dados automáticos
        var topico = new Topico(
                null,                    // ID gerado automaticamente
                dados.getTitulo(),          // Do DTO
                dados.getMensagem(),        // Do DTO
                LocalDateTime.now(),        // Data atual automática
                "ATIVO",                    // Status automático
                autor,                      // Usuário autenticado
                dados.getCurso()           // Do DTO
        );

        // 5. Salvar e retornar
        topicoRepository.save(topico);

        var uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
        return ResponseEntity.created(uri).body(new TopicoDetalheDTO(topico));
    }
    @GetMapping
    public ResponseEntity<List<TopicoDetalheDTO>> listar() {
        List<Topico> topicos = topicoRepository.findAll();
        List<TopicoDetalheDTO> listaDTO = topicos.stream()
                .map(TopicoDetalheDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(listaDTO);
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
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<TopicoDetalheDTO> atualizar(@PathVariable Long id, @RequestBody @Valid TopicoFormDTO dados) {
        Optional<Topico> optionalTopico = topicoRepository.findById(id);

        if (optionalTopico.isPresent()) {
            var topico = optionalTopico.get();

            // VALIDAÇÃO DE SEGURANÇA - Verificar se usuário é dono
            var usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!topico.getAutor().getId().equals(usuarioLogado.getId())) {
                return ResponseEntity.status(403).build(); // Forbidden - não é dono
            }

            validacaoRegra.validarTopicoDuplicadoUpdate(dados.getTitulo(), dados.getMensagem(), id);
            topico.atualizar(dados.getTitulo(), dados.getMensagem(), dados.getCurso());

            return ResponseEntity.ok(new TopicoDetalheDTO(topico));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
//    @PutMapping("/{id}")
//    @Transactional
//    public ResponseEntity<TopicoDetalheDTO> atualizar(@PathVariable Long id, @RequestBody @Valid TopicoFormDTO dados) {
//        Optional<Topico> optionalTopico = topicoRepository.findById(id);
//
//        if (optionalTopico.isPresent()) {
//            validacaoRegra.validarTopicoDuplicadoUpdate(dados.getTitulo(), dados.getMensagem(), id);
//
//            var topico = optionalTopico.get();
//            topico.atualizar(dados.getTitulo(), dados.getMensagem(), dados.getCurso());
//
//            return ResponseEntity.ok(new TopicoDetalheDTO(topico));
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
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



