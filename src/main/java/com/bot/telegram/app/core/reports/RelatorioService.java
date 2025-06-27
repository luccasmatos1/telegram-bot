package com.bot.telegram.app.core.reports;

import com.bot.telegram.app.domain.model.Categoria;
import com.bot.telegram.app.domain.model.Despesas;
import com.bot.telegram.app.domain.model.Receitas;
import com.bot.telegram.app.domain.model.enums.TipoMovimento;
import com.bot.telegram.app.domain.service.CategoriaService;
import com.bot.telegram.app.domain.service.DespesasService;
import com.bot.telegram.app.domain.service.ReceitasService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RelatorioService {

    private final ReceitasService receitasService;
    private final DespesasService despesasService;
    private final CategoriaService categoriaService;


    public String relatorioGeral(String referencia) {
        StringBuilder builder = new StringBuilder();
        String[] data = referencia.split("/");
        String mes = data[0];
        String ano = data[1];
        LocalDate localDate = LocalDate.of(Integer.parseInt(ano), Integer.parseInt(mes), 1);
        builder.append("------------------------Relatório Geral------------------------\n\n");
        builder.append("Receitas").append("\n");
        builder.append(getReceitasFixasDetalhada());
        builder.append("\nDespesas\n");
        builder.append(getDespesasFixasDetalhada()).append("\n");
        builder.append(getDespesasDoMes(localDate));
        builder.append("\n\nSaldo do mês\n\n");
        builder.append(getResumo(localDate));

        return builder.toString();
    }


    private List<Receitas> receitasFixasDetalhada() {
        List<Categoria> categoriasFixasList = categoriaService.findAll().stream()
                                                              .filter(categoria -> categoria.getTipo().equals(TipoMovimento.RECEITA) && categoria.getFixa())
                                                              .toList();
        return categoriasFixasList.stream()
                                  .map(receitasService::findByCategoria)
                                  .flatMap(List::stream)
                                  .filter(Objects::nonNull)
                                  .collect(Collectors.toList());
    }


    private List<Despesas> despesasFixasDetalhada() {
        List<Categoria> despesasFixasList = categoriaService.findAll().stream()
                                                            .filter(categoria -> categoria.getTipo().equals(TipoMovimento.DESPESA) && categoria.getFixa())
                                                            .toList();

        return despesasFixasList.stream()
                                .map(despesasService::findByCategoria)
                                .flatMap(lista -> {
                                    lista.forEach(d -> System.out.println("Despesa encontrada: " + d.getDescricao()));
                                    return lista.stream();
                                })
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());
    }


    private String getReceitasFixasDetalhada() {
        return receitasFixasDetalhada()
                .stream()
                .map(receita -> "R$ " + receita.getValor() + " - " + receita.getDescricao() + "\n")
                .collect(Collectors.joining());
    }


    private String getDespesasFixasDetalhada() {
        return despesasFixasDetalhada()
                .stream()
                .map(despesas -> "R$ " + despesas.getValor() + " - " + despesas.getDescricao() + "\n")
                .collect(Collectors.joining());
    }


    private String getDespesasDoMes(LocalDate localDate) {
        return despesasService.getByReferencia(localDate.withDayOfMonth(1), localDate.withDayOfMonth(localDate.lengthOfMonth()))
                              .stream()
                              .map(despesas -> "R$ " + despesas.getValor() + " - " + despesas.getDescricao() + "\n").collect(Collectors.joining());
    }


    private String getResumo(LocalDate localDate) {
        List<Categoria> categoriasFixasList = categoriaService.findAll().stream()
                                                              .filter(categoria -> categoria.getTipo().equals(TipoMovimento.RECEITA) && categoria.getFixa())
                                                              .toList();

        List<Categoria> despesasFixasList = categoriaService.findAll().stream()
                                                            .filter(categoria -> categoria.getTipo().equals(TipoMovimento.DESPESA) && categoria.getFixa())
                                                            .toList();


        Double totalReceitasFixas = categoriasFixasList.stream()
                                                       .map(receitasService::findByCategoria)
                                                       .flatMap(List::stream)
                                                       .mapToDouble(Receitas::getValor).sum();


        Double totalDespesasFixas = despesasFixasList.stream()
                                                     .map(despesasService::findByCategoria)
                                                     .flatMap(List::stream)
                                                     .mapToDouble(Despesas::getValor).sum();


        Double despesasDoMes =
                despesasService.getByReferencia(localDate.withDayOfMonth(1), localDate.withDayOfMonth(localDate.lengthOfMonth()))
                               .stream()
                               .mapToDouble(Despesas::getValor).sum();


        return "Total Receitas: " + "R$ " + totalReceitasFixas + "\n" + "Total de Despesas: " + "R$ " + (despesasDoMes + totalDespesasFixas) + "\n" + "Saldo do mês: " + "R$ " + (totalReceitasFixas - totalDespesasFixas - despesasDoMes);

    }


}
