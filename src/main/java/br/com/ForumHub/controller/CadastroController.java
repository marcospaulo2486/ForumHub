package br.com.ForumHub.controller;

import br.com.ForumHub.domain.Usuario.DadosCadastroUsuario;
import br.com.ForumHub.domain.Usuario.Usuario;
import br.com.ForumHub.domain.Usuario.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/register")
@Tag(name = "Cadastro", description = "Registro de novos usuários")
public class CadastroController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    @Transactional
    @Operation(summary = "Cadastra um novo usuário", description = "Requer login (e-mail) e senha. O login deve ser único.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Login já existe ou dados inválidos")
    })
    public ResponseEntity<?> cadastrar(@RequestBody @Valid DadosCadastroUsuario dados, UriComponentsBuilder uriBuilder) {
        var usuarioExistente = usuarioRepository.findByLogin(dados.getLogin());
        if (usuarioExistente != null) {
            return ResponseEntity.badRequest().body("Já existe um usuário com este login");
        }

        String senhaCriptografada = passwordEncoder.encode(dados.getSenha());
        var usuario = new Usuario(null, dados.getLogin(), senhaCriptografada, dados.getNomeExibicao());
        usuarioRepository.save(usuario);

        var uri = uriBuilder.path("/register").buildAndExpand(usuario.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }
}
