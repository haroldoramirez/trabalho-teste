package com.haroldo.minhasfinancas.api.resource;

import com.haroldo.minhasfinancas.api.dto.AtualizaStatusDTO;
import com.haroldo.minhasfinancas.api.dto.LancamentoDTO;
import com.haroldo.minhasfinancas.exception.RegraNegocioException;
import com.haroldo.minhasfinancas.model.entity.Lancamento;
import com.haroldo.minhasfinancas.model.entity.Usuario;
import com.haroldo.minhasfinancas.model.enums.StatusLancamento;
import com.haroldo.minhasfinancas.model.enums.TipoLancamento;
import com.haroldo.minhasfinancas.service.LancamentoService;
import com.haroldo.minhasfinancas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoResource {

    private final LancamentoService service;
    private final UsuarioService usuarioService;

    //POST
    @PostMapping
    public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {

        try {

            Lancamento entidade = converter(dto);
            entidade = service.salvar(entidade);

            return new ResponseEntity(entidade, HttpStatus.CREATED);

        } catch (RegraNegocioException e) {

            return ResponseEntity.badRequest().body(e.getMessage());

        }

    }

    //PUT precisa de ID
    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {

        return service.opterPorId(id).map(entidade -> {

            try {

                Lancamento lancamento = converter(dto);
                lancamento.setId(entidade.getId());
                service.atualizar(lancamento);
                return ResponseEntity.ok(lancamento);

            } catch (RegraNegocioException e) {

                return ResponseEntity.badRequest().body(e.getMessage());

            }

        }).orElseGet( () -> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));

    }

    @PutMapping("{id}/atualiza-status")
    public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO dto) {

        return service.opterPorId(id).map(entidade -> {

            try {

                StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());

                if (statusSelecionado == null) {
                    return ResponseEntity.badRequest().body("Não foi possível atualizar o status do lançamento, envie um status válido.");
                }

                entidade.setStatus(statusSelecionado);
                service.atualizar(entidade);
                return ResponseEntity.ok(entidade);
            } catch (RegraNegocioException e) {

                return ResponseEntity.badRequest().body(e.getMessage());

            }

        }).orElseGet( () -> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));

    }

    //DELETE
    @DeleteMapping("{id}")
    public ResponseEntity deletar(@PathVariable("id") Long id) {

        return service.opterPorId(id).map(entidade -> {

            service.deletar(entidade);
            return new ResponseEntity(HttpStatus.NO_CONTENT);

        }).orElseGet( () -> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));

    }

    //GET
    @GetMapping
    public ResponseEntity buscar(
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam("usuario") Long idUsuario) {

        Lancamento lancamentoFiltro = new Lancamento();
        lancamentoFiltro.setDescricao(descricao);
        lancamentoFiltro.setMes(mes);
        lancamentoFiltro.setAno(ano);

        Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);

        if (usuario.isEmpty()) {
            return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuário não encontrato para o Id informado.");
        } else {
            lancamentoFiltro.setUsuario(usuario.get());
        }

        List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);

        return ResponseEntity.ok(lancamentos);
    }

    private Lancamento converter(LancamentoDTO dto) {
        Lancamento lancamento = new Lancamento();

        lancamento.setId(dto.getId());
        lancamento.setDescricao(dto.getDescricao());
        lancamento.setAno(dto.getAno());
        lancamento.setMes(dto.getMes());
        lancamento.setValor(dto.getValor());

        if (dto.getTipo() != null)
            lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));

        if (dto.getStatus() != null)
            lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));

        //Busca do usuario
        Usuario usuario = usuarioService.obterPorId(dto.getUsuario()).orElseThrow( () -> new RegraNegocioException("Usuário não encontrato para o Id informado."));
        lancamento.setUsuario(usuario);

        return lancamento;
    }

}
