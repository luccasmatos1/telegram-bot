package com.bot.telegram.app.relatorios;

import com.bot.telegram.app.domain.model.Despesas;

import java.util.List;
import java.util.Objects;

public class RelatoriosBot {





    public static String getDespesas (List<Despesas> despesas) {

        StringBuilder sb = new StringBuilder();
        sb.append("----------DESPESAS---------").append("\n");

        despesas.stream()
                .filter(r-> Objects.nonNull(r.getCartoes()))
                .sorted((d1,d2) -> d1.getCartoes().getNome().compareTo(d2.getCartoes().getNome()))
                .forEach(
                        despesa ->
                                sb.append(despesa.getCartoes().getNome())
                                        .append(" - ")
                                        .append(despesa.getDescricao())
                                        .append(" - ")
                                        .append(despesa.getValor())
                                        .append("\n"));

        despesas.stream()
                .filter(dep -> Objects.isNull(dep.getCartoes()))
                .forEach(dep1 -> {
                            sb.append(dep1.getDescricao())
                            .append(" - ")
                            .append(dep1.getValor())
                            .append("\n");
                });

        return sb.toString();
    }

}
