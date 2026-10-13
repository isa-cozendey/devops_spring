package br.edu.ifc.bikes.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifc.bikes.dto.AtualizarSenhaRequestDTO;
import br.edu.ifc.bikes.dto.UsuarioRequestDTO;
import br.edu.ifc.bikes.dto.UsuarioResponseDTO;
import br.edu.ifc.bikes.service.UsuarioService;
import br.edu.ifc.bikes.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Usuarios", description = "Contém todas as operações relativas aos recursos para cadastro, edição e leitura de um usuário.")
@RestController
@RequestMapping("api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(summary = "Criar um novo usuário", description = "Recurso para criar um novo usuário",
        responses = {
                @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class))),
                @ApiResponse(responseCode = "409", description = "Usuário e-mail já cadastrado no sistema",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                @ApiResponse(responseCode = "422", description = "Recurso não processado por dados de entrada inválidos",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        })
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> create(@Valid @RequestBody UsuarioRequestDTO usuario) {
        UsuarioResponseDTO usuarioCriado  = usuarioService.create(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCriado);
    }

    @Operation(summary = "Recuperar um usuário pelo id", description = "Recuperar um usuário pelo id",
        responses = {
                @ApiResponse(responseCode = "200", description = "Recurso recuperado com sucesso",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class))),
                @ApiResponse(responseCode = "404", description = "Recurso não encontrado",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> getById(@PathVariable Long id) {
        UsuarioResponseDTO usuario = usuarioService.getById(id);

        if(usuario != null){
            return ResponseEntity.ok(usuario);
        }else {
            return ResponseEntity.notFound().build();
        }
    }


    @Operation(summary = "Atualizar senha", description = "Atualizar senha",
        responses = {
                @ApiResponse(responseCode = "204", description = "Senha atualizada com sucesso",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
                @ApiResponse(responseCode = "400", description = "Senha não confere",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                @ApiResponse(responseCode = "404", description = "Recurso não encontrado",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        })
    //o PATCH deve ser utilizado, no lugar no PUT, quando a atualização for de poucos campos.
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updatePassword(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarSenhaRequestDTO request) {
        usuarioService.updatePassword(id, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar todos os usuários", description = "Listar todos os usuários",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista com todos os usuários cadastrados",
                        content = @Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = UsuarioResponseDTO.class))))
            })
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> getAll(){
        List<UsuarioResponseDTO> usuarios = usuarioService.getAll();
        return ResponseEntity.ok(usuarios);
    }
}