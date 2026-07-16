package br.com.ForumHub.controller;

import br.com.ForumHub.domain.Usuario.UsuarioRepository;
import br.com.ForumHub.domain.Usuario.Usuario;
import br.com.ForumHub.domain.resposta.Resposta;
import br.com.ForumHub.domain.resposta.RespostaDetalheDTO;
import br.com.ForumHub.domain.resposta.RespostaFormDTO;
import br.com.ForumHub.domain.resposta.RespostaRepository;
import br.com.ForumHub.domain.topico.Topico;
import br.com.ForumHub.domain.topico.TopicoDetalheDTO;
import br.com.ForumHub.domain.topico.TopicoFormDTO;
import br.com.ForumHub.domain.topico.TopicoRepository;
import br.com.ForumHub.domain.topico.ValidacaoRegra;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Tópicos", description = "Gerenciamento de tópicos do fórum")
@SecurityRequirement(name = "Bearer Authentication")
public class TopicoController {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private ValidacaoRegra validacaoRegra;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RespostaRepository respostaRepository;

    @PostMapping
    @Transactional
    @Operation(summary = "Cadastra um novo tópico", description = "Requer autenticação. Título e mensagem devem ser únicos.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tópico criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Tópico duplicado ou dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<TopicoDetalheDTO> cadastrar(@RequestBody @Valid TopicoFormDTO dados, UriComponentsBuilder uriBuilder) {

        validacaoRegra.validarTopicoDuplicado(dados.getTitulo(), dados.getMensagem());
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var usuarioLogado = (Usuario) authentication.getPrincipal();

        Usuario autor = usuarioRepository.findById(usuarioLogado.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        var topico = new Topico(
                null,                    // ID gerado automaticamente
                dados.getTitulo(),          // Do DTO
                dados.getMensagem(),        // Do DTO
                LocalDateTime.now(),        // Data atual automática
                "ATIVO",                    // Status automático
                autor,                      // Usuário autenticado
                dados.getCurso()           // Do DTO
        );
        topicoRepository.save(topico);

        var uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
        return ResponseEntity.created(uri).body(new TopicoDetalheDTO(topico));
    }

    @GetMapping
    @SecurityRequirements
    @Operation(summary = "Lista todos os tópicos", description = "Não requer autenticação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de tópicos retornada com sucesso")
    })
    public ResponseEntity<List<TopicoDetalheDTO>> listar() {
        List<Topico> topicos = topicoRepository.findAll();
        List<TopicoDetalheDTO> listaDTO = topicos.stream()
                .map(TopicoDetalheDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(listaDTO);
    }

    @GetMapping("/{id}")
    @SecurityRequirements
    @Operation(summary = "Busca tópico por ID", description = "Não requer autenticação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tópico encontrado"),
            @ApiResponse(responseCode = "404", description = "Tópico não encontrado")
    })
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
    @Operation(summary = "Atualiza um tópico", description = "Requer autenticação. Apenas o autor pode atualizar.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tópico atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Tópico duplicado ou dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Usuário não é o autor do tópico"),
            @ApiResponse(responseCode = "404", description = "Tópico não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<TopicoDetalheDTO> atualizar(@PathVariable Long id, @RequestBody @Valid TopicoFormDTO dados) {
        Optional<Topico> optionalTopico = topicoRepository.findById(id);

        if (optionalTopico.isPresent()) {
            var topico = optionalTopico.get();

            var usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!topico.getAutor().getId().equals(usuarioLogado.getId())) {
                return ResponseEntity.status(403).build();
            }

            validacaoRegra.validarTopicoDuplicadoUpdate(dados.getTitulo(), dados.getMensagem(), id);
            topico.atualizar(dados.getTitulo(), dados.getMensagem(), dados.getCurso());

            return ResponseEntity.ok(new TopicoDetalheDTO(topico));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta um tópico", description = "Requer autenticação")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tópico deletado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Usuário não é o autor do tópico"),
            @ApiResponse(responseCode = "404", description = "Tópico não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        Optional<Topico> topico = topicoRepository.findById(id);
        if (topico.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!topico.get().getAutor().getId().equals(usuarioLogado.getId())) {
            return ResponseEntity.status(403).build();
        }

        topicoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/respostas")
    @SecurityRequirements
    @Operation(summary = "Lista respostas de um tópico", description = "Não requer autenticação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de respostas retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tópico não encontrado")
    })
    public ResponseEntity<List<RespostaDetalheDTO>> listarRespostas(@PathVariable Long id) {
        Optional<Topico> topicoEncontrado = topicoRepository.findById(id);
        if (topicoEncontrado.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Resposta> respostas = respostaRepository.findByTopicoIdOrderByDataCriacaoAsc(id);
        List<RespostaDetalheDTO> listaDTO = respostas.stream()
                .map(RespostaDetalheDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaDTO);
    }

    @PostMapping("/{id}/respostas")
    @Transactional
    @Operation(summary = "Cadastra uma resposta em um tópico", description = "Requer autenticação")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Resposta criada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tópico não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<RespostaDetalheDTO> cadastrarResposta(
            @PathVariable Long id,
            @RequestBody @Valid RespostaFormDTO dados,
            UriComponentsBuilder uriBuilder) {

        Optional<Topico> topicoEncontrado = topicoRepository.findById(id);
        if (topicoEncontrado.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var usuarioLogado = (Usuario) authentication.getPrincipal();

        Usuario autor = usuarioRepository.findById(usuarioLogado.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        var resposta = new Resposta();
        resposta.setMensagem(dados.getMensagem());
        resposta.setDataCriacao(LocalDateTime.now());
        resposta.setTopico(topicoEncontrado.get());
        resposta.setAutor(autor);
        resposta.setSolucao(false);

        respostaRepository.save(resposta);

        var uri = uriBuilder.path("/topicos/{id}/respostas").buildAndExpand(id).toUri();
        return ResponseEntity.created(uri).body(new RespostaDetalheDTO(resposta));
    }
}
