package com.haroldo.minhasfinancas.api.resource;

import com.haroldo.minhasfinancas.api.dto.UsuarioDTO;
import com.haroldo.minhasfinancas.api.dto.UsuarioLoginDTO;
import com.haroldo.minhasfinancas.exception.ErroAutenticacaoException;
import com.haroldo.minhasfinancas.exception.RegraNegocioException;
import com.haroldo.minhasfinancas.model.entity.Usuario;
import com.haroldo.minhasfinancas.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioResource {

    private UsuarioService service;

    private UsuarioResource(UsuarioService service) {
        this.service = service;
    }

    //POST
    @PostMapping
    public ResponseEntity salvar(@RequestBody UsuarioDTO dto) {

        Usuario usuario = Usuario.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(dto.getSenha())
                .build();

        try {

            Usuario usuarioSalvo = service.salvarUsuario(usuario);

            return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);

        } catch (RegraNegocioException e) {

            return ResponseEntity.badRequest().body(e.getMessage());

        }

    }

    @PostMapping("/autenticar")
    public ResponseEntity autenticar(@RequestBody UsuarioLoginDTO dto) {

        try {

            Usuario usuarioAutenticado = service.autenticar(dto.getEmail(), dto.getSenha());

            return ResponseEntity.ok(usuarioAutenticado);

        } catch (ErroAutenticacaoException e) {

            return ResponseEntity.badRequest().body(e.getMessage());

        }

    }
}
