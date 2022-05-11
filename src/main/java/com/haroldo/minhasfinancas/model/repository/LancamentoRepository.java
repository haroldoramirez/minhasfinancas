package com.haroldo.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.haroldo.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{

}
