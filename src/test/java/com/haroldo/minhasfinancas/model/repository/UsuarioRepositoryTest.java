package com.haroldo.minhasfinancas.model.repository;

import com.haroldo.minhasfinancas.model.entity.Usuario;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UsuarioRepositoryTest {
	
	//Teste de Integracao
	
	@Autowired
	UsuarioRepository repository;

	@Autowired
	TestEntityManager entityManager;

	public static Usuario criarUsuario() {
		return Usuario.builder()
				.nome("usuario")
				.email("usuario@email.com")
				.senha("senha")
				.build();
	}
	
	@Test
	public void deveVerificarAExistenciaDeUmEmail() {
		
		//Cenario
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		//Acao - Execucao
		boolean result = repository.existsByEmail("usuario@email.com");
		
		//Verificacao
		Assertions.assertThat(result).isTrue();
		
	}
	
	@Test
	public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComEmail() {

		//Acao - Execucao
		boolean result = repository.existsByEmail("usuario@email.com");
		
		//Verificacao
		Assertions.assertThat(result).isFalse();

	}

	@Test
	public void devePersistirUmUsuarioNaBaseDeDados() {

		//Cenario
		Usuario usuario = criarUsuario();

		//Acao
		Usuario usuarioSalvo = repository.save(usuario);

		//Verificacao
		Assertions.assertThat(usuarioSalvo.getId()).isNotNull();

	}

	@Test
	public void deveBuscarUmUsuarioPorEmail() {

		//Cenario
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);

		//Verificacao
		Optional<Usuario> result = repository.findByEmail("usuario@email.com");

		Assertions.assertThat(result.isPresent()).isTrue();

	}

	@Test
	public void deveRetornarVazioAoBuscarUsuarioPorEmailQuandoNaoExisteNaBase() {

		//Verificacao
		Optional<Usuario> result = repository.findByEmail("usuario@email.com");

		Assertions.assertThat(result.isPresent()).isFalse();

	}

}
