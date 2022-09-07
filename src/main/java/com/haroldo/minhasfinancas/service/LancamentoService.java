package com.haroldo.minhasfinancas.service;

import com.haroldo.minhasfinancas.model.entity.Lancamento;
import com.haroldo.minhasfinancas.model.enums.StatusLancamento;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface LancamentoService {

    Lancamento salvar(Lancamento lancamento);

    Lancamento atualizar(Lancamento lancamento);

    void deletar(Lancamento lancamento);

    List<Lancamento> buscar(Lancamento lancamento);

    void atualizarStatus(Lancamento lancamento, StatusLancamento status);

    void validar(Lancamento lancamento);

    Optional<Lancamento> opterPorId(Long id);

    BigDecimal obterSaldoPorUsuario(Long id);

}
