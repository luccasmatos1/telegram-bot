package com.bot.telegram.app.core.fluxo;

import com.bot.telegram.app.core.enums.FluxoLancDespesas;
import com.bot.telegram.app.core.enums.FluxoLancarReceitaEnum;
import com.bot.telegram.app.core.service.HandleService;
import com.bot.telegram.app.core.utils.ListasDeOpcoes;
import com.bot.telegram.app.domain.model.Categoria;
import com.bot.telegram.app.domain.model.Despesas;
import com.bot.telegram.app.domain.model.Receitas;
import com.bot.telegram.app.domain.service.CategoriaService;
import com.bot.telegram.app.domain.service.ReceitasService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FluxoLancarReceita {


    private final Map<Long, Receitas> receitasMap = new HashMap<>();

    private final CategoriaService categoriaService;
    private final ReceitasService receitasService;

    public void lancarReceita(HandleService service) {
        Long chatId = service.getChatId();
        String mensagemAtual = service.getMensagemAtual();
        Receitas receita = receitasMap.getOrDefault(chatId, new Receitas());

        FluxoLancarReceitaEnum etapa = service.getFluxoLancarReceitaMap().getOrDefault(chatId, FluxoLancarReceitaEnum.INFORMAR_CATEGORIA);

        switch (etapa) {
            case INFORMAR_CATEGORIA -> {
                Categoria categoria = categoriaService.findByDescricao(mensagemAtual);
                if (categoria == null) {
                    service.sendMessage("Categoria não encontrada! Informe outra!");
                    return;
                }
                receita.setCategoria(categoria);
                receitasMap.put(chatId, receita);
                service.setarNovaEtapaReceitas(FluxoLancarReceitaEnum.INFOMAR_VALOR);
                service.proximaMensagem("Informe o valor da receita: ");
            }
            case INFOMAR_VALOR -> {
                try {
                    Double valor = Double.parseDouble(mensagemAtual.replace(",", "."));
                    receita.setValor(valor);
                    receitasMap.put(chatId, receita);
                    service.setarNovaEtapaReceitas(FluxoLancarReceitaEnum.INFORMAR_DESCRICAO);
                    service.proximaMensagem("Informe a descrição da receita: ");

                } catch (NumberFormatException e) {
                    service.sendMessage("Por favor, informe um valor válido!");
                }
            }

            case INFORMAR_DESCRICAO -> {
                receita.setDescricao(mensagemAtual);
                receitasMap.put(chatId, receita);
                service.setarNovaEtapaReceitas(FluxoLancarReceitaEnum.DESEJA_INFORMAR_DATA_RECEBIMENTO);
                service.proximaPergunta("Deseja informar a data de recebimento? ", ListasDeOpcoes.optionsSimOuNao("Informar Data", "Recebi Hoje"));
            }

            case DESEJA_INFORMAR_DATA_RECEBIMENTO -> {
                if (mensagemAtual.equalsIgnoreCase("btnNao")) {
                    receita.setDataLancamento(LocalDate.now());
                    service.proximaPergunta("Deseja Concluir a operação? Sim ou Não", ListasDeOpcoes.optionsSimOuNao("Finalizar", "Cancelar"));
                    service.setarNovaEtapaReceitas(FluxoLancarReceitaEnum.FINALIZAR_LANCAMENTO);

                } else {
                    service.proximaMensagem("Informe a data no formato DD/MM/AAAA ou DD/MM");
                    service.setarNovaEtapaDespesas(FluxoLancDespesas.INFORMAR_DATA);
                }
            }


            case INFORMAR_DATA_RECEBIMENTO -> {
                try {
                    finalizarComDataInformada(service);
                    service.proximaPergunta("Deseja Concluir a operação", ListasDeOpcoes.optionsSimOuNao("Finalizar", "Cancelar"));

                } catch (Exception e) {
                    service.sendMessage("Por favor, informe uma data válida!");
                }

            }
            case FINALIZAR_LANCAMENTO -> finalizacao(service);
        }

    }






    private void finalizarComDataInformada(HandleService service) {
        String mensagemAtual = service.getMensagemAtual();
        Despesas despesas = service.getDespesas().get(service.getChatId());

        String[] data = mensagemAtual.split("/");
        String dia = data[0], mes = data[1];
        String ano = (data.length < 3) ? String.valueOf(LocalDate.now().getYear()) : data[2];
        LocalDate dataCompra = LocalDate.parse(dia + "/" + mes + "/" + ano, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        System.out.println("DATA COMPRA: " + dataCompra);
        despesas.setDataCompra(dataCompra);
        service.getDespesas().put(service.getChatId(), despesas);
        service.setarNovaEtapaReceitas(FluxoLancarReceitaEnum.FINALIZAR_LANCAMENTO);


    }




    private void finalizacao(HandleService service) {
        if (service.getMensagemAtual().equalsIgnoreCase("btnNao")) {
            service.sendMessage("Operação Cancelada!");
            service.limparFluxos(service.getChatId());
        } else {
            Receitas receitas = receitasMap.get(service.getChatId());
            receitasService.salvar(receitas);
            service.sendMessage("Lançamento Inserido com Sucesso!");
            service.limparFluxos(service.getChatId());
        }
    }

}


