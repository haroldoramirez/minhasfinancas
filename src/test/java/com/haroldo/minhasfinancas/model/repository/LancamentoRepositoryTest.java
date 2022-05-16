package com.haroldo.minhasfinancas.model.repository;

import com.haroldo.minhasfinancas.model.entity.Lancamento;
import com.haroldo.minhasfinancas.model.enums.StatusLancamento;
import com.haroldo.minhasfinancas.model.enums.TipoLancamento;
import static org.assertj.core.api.Assertions.*; //Adicionado static
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class LancamentoRepositoryTest {

    @Autowired
    LancamentoRepository repository;

    @Autowired
    TestEntityManager entityManager;

    public static Lancamento criarLancamento() {
        return Lancamento.builder()
                .ano(2019)
                .mes(1)
                .descricao("Lancamento qualquer")
                .valor(BigDecimal.valueOf(10))
                .tipo(TipoLancamento.RECEITA)
                .status(StatusLancamento.PENDENTE)
                .dataCadastro(LocalDate.now()).build();
    }

    private Lancamento criarEPersistirUmLancamento() {

        //Cenario
        Lancamento lancamento = criarLancamento();

        //Acao
        //Persistir na base
        entityManager.persist(lancamento);

        return lancamento;

    }

    @Test
    public void deveSalvarUmLancamento() {

        //Cenario
        Lancamento lancamento = criarEPersistirUmLancamento();

        //Verificacao static org.assertj.core.api.Assertions
        assertThat(lancamento.getId()).isNotNull();
    }

    @Test
    public void deveDeletarUmLancamento() {

        //Cenario
        Lancamento lancamento = criarEPersistirUmLancamento();

        //Acao
        //Buscar o lancamento que foi persistido
        lancamento = entityManager.find(Lancamento.class, lancamento.getId());

        //Deleta
        repository.delete(lancamento);

        //Faz a busca
        Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());

        //Verificacao se o arquivo foi deletado - static org.assertj.core.api.Assertions
        assertThat(lancamentoInexistente).isNull();

    }

    @Test
    public void deveAtualizarUmLancamento() {

        //Cenario
        Lancamento lancamento = criarEPersistirUmLancamento();

        lancamento.setAno(2022);
        lancamento.setDescricao("Teste atualizar");
        lancamento.setStatus(StatusLancamento.CANCELADO);

        //Acao salva e atualiza
        repository.save(lancamento);

        Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());

        //Verificacao - static org.assertj.core.api.Assertions
        assertThat(lancamentoAtualizado.getAno()).isEqualTo(2022);
        assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("Teste atualizar");
        assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);

    }

    @Test
    public void deveBuscarLancamentoPorId() {

        //Cenario
        Lancamento lancamento = criarEPersistirUmLancamento();

        //Acao
        Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());

        //Verificacao - static org.assertj.core.api.Assertions
        assertThat(lancamentoEncontrado.isPresent()).isTrue();

    }


}
