package com.haroldo.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.haroldo.minhasfinancas.model.entity.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	//Query methods
	boolean existsByEmail(String email);

	Optional<Usuario> findByEmail(String email);

}
