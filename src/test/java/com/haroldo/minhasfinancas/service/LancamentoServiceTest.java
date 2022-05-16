package com.haroldo.minhasfinancas.service;

import com.haroldo.minhasfinancas.exception.RegraNegocioException;
import com.haroldo.minhasfinancas.model.entity.Lancamento;
import com.haroldo.minhasfinancas.model.entity.Usuario;
import com.haroldo.minhasfinancas.model.enums.StatusLancamento;
import com.haroldo.minhasfinancas.model.repository.LancamentoRepository;
import com.haroldo.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.haroldo.minhasfinancas.service.impl.LancamentoServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

    @SpyBean //Para chamar os metodos reais
    LancamentoServiceImpl service;

    @MockBean //Simular o comportamento do repository
    LancamentoRepository repository;

    @Test
    public void deveSalvarUmLancamento() {

        //Cenario 1 - verifica se contem erros na validacao
        Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
        //Mockar Nao fazer nada quando o service chamar o metodo validar la na impl para nao lancar erros
        Mockito.doNothing().when(service).validar(lancamentoASalvar);

        //Cenario 2
        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
        //Pois foi salvo na base de dados
        lancamentoSalvo.setId(1L);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
        Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

        //Execucao
        Lancamento lancamento = service.salvar(lancamentoASalvar);

        //Verificacao
        Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
        Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);

    }

    @Test
    public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {

        //Cenario 1 - verifica se contem erros na validacao
        Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
        Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);

        //Execucacao e verificacao
        Assertions.catchThrowableOfType(() -> service.salvar(lancamentoASalvar), RegraNegocioException.class );
        Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);

    }

    @Test
    public void deveAtualizarUmLancamento() {

        //Cenario 1 - verifica se contem erros na validacao
        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();

        //Pois foi salvo na base de dados
        lancamentoSalvo.setId(1L);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

        //Mockar nao fazer nada quando o service chamar o metodo validar la na impl para nao lancar erros
        Mockito.doNothing().when(service).validar(lancamentoSalvo);

        Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

        //Execucao
        service.atualizar(lancamentoSalvo);

        //Verificacao
        Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);

    }

    @Test
    public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {

        //Cenario 1 - verifica se contem erros na validacao
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

        //Execucacao e verificacao
        Assertions.catchThrowableOfType(() -> service.atualizar(lancamento), NullPointerException.class);
        Mockito.verify(repository, Mockito.never()).save(lancamento);

    }

    @Test
    public void deveDeletarUmLancamento() {

        //Cenario 1 - verifica se contem erros na validacao
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1L);

        //Execucacao
        service.deletar(lancamento);

        //Verificacao
        Mockito.verify(repository).delete(lancamento);
    }

    @Test
    public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {

        //Cenario 1 - verifica se contem erros na validacao
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

        //Execucacao
        Assertions.catchThrowableOfType(() -> service.deletar(lancamento), NullPointerException.class);

        //Verificacao que nunca chamou o metodo delete apos o erro
        Mockito.verify(repository, Mockito.never()).delete(lancamento);

    }

    @Test
    public void deveFiltrarLancamentos() {

        //Cenario 1 Salvar um lancamento para ser usado na busca
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1L);

        List<Lancamento> lista = Arrays.asList(lancamento);
        Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);

        //Execucao
        List<Lancamento> resultado = service.buscar(lancamento);

        //Verificacao
        Assertions.assertThat(resultado)
                .isNotEmpty()
                .hasSize(1)
                .contains(lancamento);

    }

    @Test
    public void deveAtualizarOsStatusDeUmLancamento() {

        //Cenario 1 - verifica se contem erros na validacao
        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();

        //Pois foi salvo na base de dados
        lancamentoSalvo.setId(1L);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

        StatusLancamento novoStatus = StatusLancamento.EFETIVADO;

        //doNothing() so pode ser usado para metodos void
        //foi mudado para doReturn
        Mockito.doReturn(lancamentoSalvo).when(service).atualizar(lancamentoSalvo);

        //Execucao
        service.atualizarStatus(lancamentoSalvo, novoStatus);

        //Verificacoes
        Assertions.assertThat(lancamentoSalvo.getStatus()).isEqualTo(novoStatus);
        Mockito.verify(service).atualizar(lancamentoSalvo);
    }

    @Test
    public void deveObterUmLancamentoPorID() {

        //Cenario
        Long id = 1L;
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

        //Pois foi salvo na base de dados
        lancamento.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));

        //Execucao
        Optional<Lancamento> resultado = service.opterPorId(id);

        //Verificacao
        Assertions.assertThat(resultado.isPresent()).isTrue();

    }

    @Test
    public void deveRetornarVazioQuandoOLancamentoNaoExiste() {

        //Cenario
        Long id = 1L;
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

        //Pois foi salvo na base de dados
        lancamento.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        //Execucao
        Optional<Lancamento> resultado = service.opterPorId(id);

        //Verificacao
        Assertions.assertThat(resultado.isPresent()).isFalse();

    }

    @Test
    public void deveLancarErrosAoValidarUmLancamento() {
        Lancamento lancamento = new Lancamento();

        // DESCRICAO

        // Verifica se descricao esta nula
        Throwable erro = Assertions.catchThrowable( () -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");

        // Adicionar um valor vazio para a descricao
        lancamento.setDescricao("");

        // Verifica se descricao vazia
        erro = Assertions.catchThrowable( () -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");

        // Seta uma descricao correta para continuar a verificacao
        lancamento.setDescricao("Salário ou Pagamentos");

        // MES

        // Verifica se tem um mes invalido
        erro = Assertions.catchThrowable( () -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");

        //Seta um valor incorreto para o mes
        lancamento.setMes(0);

        // Verifica se tem um mes invalido
        erro = Assertions.catchThrowable( () -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");

        //Seta um valor incorreto para o mes
        lancamento.setMes(13);

        // Verifica se tem um mes invalido
        erro = Assertions.catchThrowable( () -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");

        // Seta um mes correto para continuar com a validacao
        lancamento.setMes(5);

        // ANO

        // Verifica se tem um ano nulo
        erro = Assertions.catchThrowable( () -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");

        lancamento.setAno(202);

        // Verifica se tem um ano com menor caracter
        erro = Assertions.catchThrowable( () -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");

        // Seta o ano correto para continuar com a validacao
        lancamento.setAno(2022);

        // USUARIO

        // Verifica se tem um usuario nulo
        erro = Assertions.catchThrowable( () -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");

        // Seta um usuario sem ID
        lancamento.setUsuario(new Usuario());

        // Verifica se o usuario sem ID
        erro = Assertions.catchThrowable( () -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");

        // Seta o ID correto para continuar com a validacao
        lancamento.getUsuario().setId(1L);

        // Verifica se o usuario com ID
        erro = Assertions.catchThrowable( () -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");

        // Seta um nome de usuario para continuar com a validacao
        lancamento.getUsuario().setNome("Fulano 1");

        // VALOR

        // Verifica se o valor e nulo
        erro = Assertions.catchThrowable( () -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido.");

        lancamento.setValor(BigDecimal.ZERO);

        // Verifica se o valor e zero
        erro = Assertions.catchThrowable( () -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido.");

        //Seta um valor correto para continuar com a validacao
        lancamento.setValor(BigDecimal.valueOf(15000));

        // TIPO

        // Verifica se o tipo e nulo
        erro = Assertions.catchThrowable( () -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Tipo de lançamento.");

    }

}
