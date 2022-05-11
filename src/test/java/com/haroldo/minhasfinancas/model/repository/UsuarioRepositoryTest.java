package com.haroldo.minhasfinancas.model.repository;

import com.haroldo.minhasfinancas.model.entity.Usuario;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class UsuarioRepositoryTest {
	
	//Teste de Integracao
	
	@Autowired
	UsuarioRepository repository;
	
	@Test
	public void deveVerificarAExistenciaDeUmEmail() {
		
		//Cenario
		Usuario usuario = Usuario.builder().nome("usuario").email("usuario@email.com").build();
		repository.save(usuario);
		
		//Acao - Execucao
		boolean result = repository.existsByEmail("usuario@email.com");
		
		//Verificacao
		Assertions.assertThat(result).isTrue();
		
	}
	
	@Test
	public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComEmail() {
		
		//Cenario
		repository.deleteAll();
		
		//Acao - Execucao
		boolean result = repository.existsByEmail("usuario@email.com");
		
		//Verificacao
		
		Assertions.assertThat(result).isFalse();
	}

}
