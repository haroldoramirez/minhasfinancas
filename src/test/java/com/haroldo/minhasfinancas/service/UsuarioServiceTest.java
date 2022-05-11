package com.haroldo.minhasfinancas.service;

import com.haroldo.minhasfinancas.exception.ErroAutenticacaoException;
import com.haroldo.minhasfinancas.exception.RegraNegocioException;
import com.haroldo.minhasfinancas.model.entity.Usuario;
import com.haroldo.minhasfinancas.model.repository.UsuarioRepository;
import com.haroldo.minhasfinancas.service.impl.UsuarioServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class UsuarioServiceTest {

    //Testes unitarios com mock e spy
    @SpyBean
    UsuarioServiceImpl service;

    @MockBean
    UsuarioRepository repository;

    @Test(expected = Test.None.class)
    public void deveSalvarUsuario() {

        //Cenario para chamar metodos reais
        Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nome("nome")
                .email("email@email.com")
                .senha("senha").build();

        Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

        //Acao
        Usuario usuarioSalvo = service.salvarUsuario(new Usuario());

        //Verificacao
        Assertions.assertThat(usuarioSalvo).isNotNull();
        Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1L);
        Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
        Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
        Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
    }

    @Test(expected = RegraNegocioException.class)
    public void naoDeveSalvarUsuarioComEmailJaCadastrado() {

        //Cenario
        String email = "email@email.com";

        Usuario usuario = Usuario.builder()
                .email(email)
                .build();

        Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);

        //Acao
        service.salvarUsuario(usuario);

        //Verificacao
        Mockito.verify(repository, Mockito.never()).save(usuario);

    }

    @Test(expected = Test.None.class)
    public void deveAutenticarUmUsuarioComSucesso() {

        //Cenario
        String email = "email@email.com";
        String senha = "senha";

        Usuario usuario = Usuario.builder().email(email).senha(senha).id(1L).build();
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

        //Acao
        Usuario result = service.autenticar(email, senha);

        //Verificacao
        Assertions.assertThat(result).isNotNull();

    }

    @Test
    public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComEmailInformado() {

        //Cenario
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        //Acao
        Throwable exception = Assertions.catchThrowable(() -> service.autenticar("email@email.com", "senha"));

        //Verificacao
        Assertions.assertThat(exception)
                .isInstanceOf(ErroAutenticacaoException.class).hasMessage("Usuário não encontrado para o email informado.");
    }

    @Test
    public void deveLancarErroQuandoSenhaNaoBater() {

        //Cenario
        String email = "email@email.com";
        String senha = "senha";

        Usuario usuario = Usuario.builder().email(email).senha(senha).id(1L).build();
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

        //Acao
        Throwable exception = Assertions.catchThrowable(() -> service.autenticar("email@email.com", "123"));

        //Verificacao
        Assertions.assertThat(exception).isInstanceOf(ErroAutenticacaoException.class).hasMessage("Senha inválida.");

    }

    @Test(expected = Test.None.class)
    public void deveValidarEmail() {

        //Cenario
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

        //Acao
        service.validarEmail("email@email.com");

    }

    @Test(expected = RegraNegocioException.class)
    public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {

        //Cenario
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

        //Acao
        service.validarEmail("usuario@email.com");

    }
}
