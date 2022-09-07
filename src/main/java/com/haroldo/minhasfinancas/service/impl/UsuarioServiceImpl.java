package com.haroldo.minhasfinancas.service.impl;

import com.haroldo.minhasfinancas.exception.ErroAutenticacaoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.haroldo.minhasfinancas.exception.RegraNegocioException;
import com.haroldo.minhasfinancas.model.entity.Usuario;
import com.haroldo.minhasfinancas.model.repository.UsuarioRepository;
import com.haroldo.minhasfinancas.service.UsuarioService;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {
	
	private final UsuarioRepository repository;
	
	public UsuarioServiceImpl(UsuarioRepository repository) {

		super();
		this.repository = repository;

	}

	@Override
	public Usuario autenticar(String email, String senha) {

		Optional<Usuario> usuario = repository.findByEmail(email);

		if (usuario.isEmpty())
			throw new ErroAutenticacaoException("Usuário não encontrado para o email informado.");


		if (!usuario.get().getSenha().equals(senha)) {
			throw new ErroAutenticacaoException("Senha inválida.");
		}

		return usuario.get();

	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {

		validarEmail(usuario.getEmail());
		return repository.save(usuario);

	}

	@Override
	public void validarEmail(String email) {
		
		//Query methods
		boolean existe = repository.existsByEmail(email);
		
		if (existe) {
			throw new RegraNegocioException("Já existe um usuário cadastrado com este email.");
		}

	}

	@Override
	public Optional<Usuario> obterPorId(Long id) {
		return repository.findById(id);
	}

}
