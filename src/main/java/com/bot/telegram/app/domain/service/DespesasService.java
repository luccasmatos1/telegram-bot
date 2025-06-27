package com.bot.telegram.app.domain.service;

import com.bot.telegram.app.core.dto.response.DespesaResponse;
import com.bot.telegram.app.domain.model.Categoria;
import com.bot.telegram.app.domain.model.Despesas;
import com.bot.telegram.app.domain.repository.DespesasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class DespesasService {

    private final DespesasRepository repository;


    public DespesaResponse salvar(Despesas despesas) {
        Integer parcela = 1;
        despesas.setId(new Random().nextLong());

        LocalDate fechamento = despesas.getDataCompra();
        String nomeCartao = null;

        if (Objects.nonNull(despesas.getCartoes())) {
             nomeCartao = despesas.getCartoes().getNome();
             fechamento = LocalDate.of(despesas.getDataCompra().getYear(),despesas.getDataCompra().getMonth(), despesas.getCartoes().getDiaFechamento());
        }

        if (Objects.nonNull(despesas.getParcela())) {
            parcela = despesas.getParcela();
        }

        if (parcela > 1) {
            salvarParcelas(despesas);
            return new DespesaResponse(
                    nomeCartao,
                    fechamento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    setFirstDataPagamento(despesas,1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    despesas.getParcela());

        }


        despesas.setParcela(parcela);
        despesas.setDataPagamento(setFirstDataPagamento(despesas,1));
        repository.save(despesas);

        return new DespesaResponse(
                nomeCartao,
                fechamento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                setFirstDataPagamento(despesas,1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                despesas.getParcela());

    }

    private void salvarParcelas (Despesas despesas) {

        Integer parcelas = despesas.getParcela();
        Double vlrParcela = despesas.getValor() / parcelas;
        String descricaoInicial = despesas.getDescricao();


        for (int i = 0; i < parcelas; i++) {
            despesas.setId(new Random().nextLong());
            despesas.setDescricao(descricaoInicial + " - Parcela " + (i+1));
            despesas.setValor(vlrParcela);
            despesas.setDataPagamento(setFirstDataPagamento(despesas,i + 1));
            despesas.setParcela(i+1);
            despesas.setCartoes(despesas.getCartoes());
            repository.save(despesas);
        }
    }


    private LocalDate setFirstDataPagamento (Despesas despesas,Integer plusMonth) {
        if (Objects.isNull(despesas.getCartoes())){
            return LocalDate.of(despesas.getDataCompra().getYear(),despesas.getDataCompra().getMonth(), 10).plusMonths(plusMonth);
        }

        Integer diaCompra = despesas.getDataCompra().getDayOfMonth();

        LocalDate diaVencimento = LocalDate.of(despesas.getDataCompra().getYear(),despesas.getDataCompra().getMonth(), despesas.getCartoes().getDiaVencimento());

        if (diaCompra <= despesas.getCartoes().getDiaFechamento()) {
            return diaVencimento.plusMonths(plusMonth-1);
        }

        return diaVencimento.plusMonths(plusMonth);
    }






    public List<Despesas> getByReferencia (LocalDate dtInicio, LocalDate dtFim) {
        return repository.findByDataPagamentoBetween(dtInicio,dtFim);
    }

    public List<Despesas> findByCategoria (Categoria categoria) {
        return repository.findByCategoria(categoria);
    }



}
