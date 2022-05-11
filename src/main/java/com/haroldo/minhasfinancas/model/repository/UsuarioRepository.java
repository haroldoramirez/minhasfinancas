package com.haroldo.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.haroldo.minhasfinancas.model.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
		
	//Query methods
	boolean existsByEmail(String email);

}
