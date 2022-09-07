package com.haroldo.minhasfinancas.api.resource;

import com.haroldo.minhasfinancas.api.dto.UsuarioDTO;
import com.haroldo.minhasfinancas.api.dto.UsuarioLoginDTO;
import com.haroldo.minhasfinancas.exception.ErroAutenticacaoException;
import com.haroldo.minhasfinancas.exception.RegraNegocioException;
import com.haroldo.minhasfinancas.model.entity.Usuario;
import com.haroldo.minhasfinancas.service.LancamentoService;
import com.haroldo.minhasfinancas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioResource {

    private final UsuarioService service;
    private final LancamentoService lancamentoService;

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

    @GetMapping("{id}/saldo")
    public ResponseEntity obterSaldo(@PathVariable("id") Long id) {

        Optional<Usuario> usuario = service.obterPorId(id);

        if (usuario.isEmpty())
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
        return ResponseEntity.ok(saldo);

    }
}
