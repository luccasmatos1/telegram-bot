package com.bot.telegram.app.core.fluxo;

import com.bot.telegram.app.core.enums.FluxoLancDespesas;
import com.bot.telegram.app.core.service.HandleService;
import com.bot.telegram.app.core.utils.ListasDeOpcoes;
import com.bot.telegram.app.domain.model.Cartoes;
import com.bot.telegram.app.domain.model.Categoria;
import com.bot.telegram.app.domain.model.Despesas;
import com.bot.telegram.app.domain.service.CartoesService;
import com.bot.telegram.app.domain.service.CategoriaService;
import com.bot.telegram.app.domain.service.DespesasService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class FluxoLancarNovaDespesa {

    private final DespesasService despesasService;
    private final CartoesService cartoesService;
    private final CategoriaService categoriaService;
    private final ListasDeOpcoes listasDeOpcoes;


    private boolean informarCartao = true;

    public void cadastroDespesaService(HandleService service) {
        Long chatId = service.getChatId();
        String mensagemAtual = service.getMensagemAtual();

        Despesas despesas = service.getDespesas().getOrDefault(chatId, new Despesas());
        FluxoLancDespesas etapaAtual = service.getLancDespesasMap().getOrDefault(chatId, FluxoLancDespesas.INFORMAR_CATEGORIA);

        switch (etapaAtual) {

            case INFORMAR_CATEGORIA -> {
                try {
                    Categoria categoria = categoriaService.findByDescricao(mensagemAtual);
                    if (categoria == null) {
                        throw new IllegalArgumentException("Categoria não encontrada!");
                    }
                    despesas.setCategoria(categoria);
                    service.getDespesas().put(chatId, despesas);
                    service.setarNovaEtapaDespesas(FluxoLancDespesas.DESEJA_INFORMAR_CARTAO);
                    service.proximaPergunta("Deseja informar o cartão?", ListasDeOpcoes.optionsSimOuNao("Sim", "Não"));

                } catch (IllegalArgumentException e) {
                    service.proximaPergunta("Informe a Categoria", listasDeOpcoes.listarCategoriasDespesa());
                }

            }

            case DESEJA_INFORMAR_CARTAO -> {
                if (mensagemAtual.equalsIgnoreCase("btnNao")) {
                    service.setarNovaEtapaDespesas(FluxoLancDespesas.INFORMAR_DESCRICAO);
                    service.proximaMensagem("O que foi gasto?\n");
                    informarCartao = false;

                } else {
                    service.proximaPergunta("Qual o cartão?", listasDeOpcoes.listarCartoes());
                    service.setarNovaEtapaDespesas(FluxoLancDespesas.INFORMAR_CATAO);
                }
            }

            case INFORMAR_CATAO -> {
                Cartoes cartao = cartoesService.findByNome(mensagemAtual);
                despesas.setCartoes(cartao);
                service.getDespesas().put(chatId, despesas);
                service.setarNovaEtapaDespesas(FluxoLancDespesas.INFORMAR_DESCRICAO);
                service.proximaMensagem("O que foi gasto?\n");
            }

            case INFORMAR_DESCRICAO -> {
                despesas.setDescricao(mensagemAtual);
                service.getDespesas().put(chatId, despesas);
                service.setarNovaEtapaDespesas(FluxoLancDespesas.INFORMAR_VALOR);
                service.proximaMensagem("Informe o valor da despesa: R$");
            }

            case INFORMAR_VALOR -> {
                try {
                    Double valor = Double.valueOf(mensagemAtual.replace(",", "."));
                    despesas.setValor(valor);
                    service.getDespesas().put(chatId, despesas);
                    if (informarCartao) {
                        service.setarNovaEtapaDespesas(FluxoLancDespesas.INFORMAR_PARCELA);
                        service.proximaMensagem("Informe a quantidade de parcelas: ");
                    } else {
                        service.setarNovaEtapaDespesas(FluxoLancDespesas.DESEJA_INFORMAR_DATA);
                        service.proximaPergunta("Deseja Informar Data?", ListasDeOpcoes.optionsSimOuNao("Informar", "Lançar com a data de Hoje"));
                    }
                } catch (IllegalArgumentException e) {
                    service.sendMessage("Por favor, informe um valor válido!");
                }
            }

            case INFORMAR_PARCELA -> {
                try {

                    Integer parcela = Integer.parseInt(mensagemAtual);
                    despesas.setParcela(parcela);
                    service.getDespesas().put(chatId, despesas);
                    service.setarNovaEtapaDespesas(FluxoLancDespesas.DESEJA_INFORMAR_DATA);
                    service.proximaPergunta("Deseja Informar a data?", ListasDeOpcoes.optionsSimOuNao("Informar Data", "Lançar com a data de hoje"));
                } catch (IllegalArgumentException e) {
                    service.sendMessage("Por favor, informe um valor válido!");
                }
            }

            case DESEJA_INFORMAR_DATA -> {

                if (mensagemAtual.equalsIgnoreCase("btnNao")) {
                    despesas.setDataCompra(LocalDate.now());
                    service.proximaPergunta("Deseja Concluir a operação? Sim ou Não", ListasDeOpcoes.optionsSimOuNao("Finalizar", "Cancelar"));
                    service.setarNovaEtapaDespesas(FluxoLancDespesas.FINALIZAR_LANCAMENTO);

                } else {
                    service.proximaMensagem("Informe a data no formato DD/MM/AAAA ou DD/MM");
                    service.setarNovaEtapaDespesas(FluxoLancDespesas.INFORMAR_DATA);
                }


            }

            case INFORMAR_DATA -> {
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


    public void cadastroRapido(HandleService service) {
        Long chatId = service.getChatId();
        String mensagemAtual = service.getMensagemAtual();

        Despesas despesas = service.getDespesas().getOrDefault(chatId, new Despesas());
        FluxoLancDespesas etapaAtual = service.getLancDespesasMap().get(chatId);

        if (etapaAtual.equals(FluxoLancDespesas.LANCAMENTO_RAPIDO)) {
             service.getLancDespesasMap().put(service.getChatId(), FluxoLancDespesas.INFORMAR_VALOR_DESCRICAO_PARCELA);
        }

        FluxoLancDespesas novaEtapa = service.getLancDespesasMap().get(chatId);
        switch (novaEtapa) {
            case INFORMAR_VALOR_DESCRICAO_PARCELA -> {
                try {
                    Double valor = getValor(mensagemAtual);
                    String descricao = getDescricao(mensagemAtual);
                    Integer parcelas = getParcelas(mensagemAtual);
                    despesas.setDescricao(descricao);
                    despesas.setValor(valor);
                    despesas.setParcela(parcelas);
                    service.getDespesas().put(chatId, despesas);
                    service.setarNovaEtapaDespesas(FluxoLancDespesas.INFORMAR_CATAO);
                    service.proximaPergunta("Qual o cartão?", listasDeOpcoes.listarCartoes());

                } catch (IllegalArgumentException e) {
                    service.sendMessage("Por favor, Informe valores válidos!");
                }
            }

            case INFORMAR_CATAO -> {
                Cartoes cartao = cartoesService.findByNome(mensagemAtual);
                if (cartao == null) {
                    throw new IllegalArgumentException("Cartão não encontrado!");
                }
                despesas.setCartoes(cartao);
                despesas.setDataCompra(LocalDate.now());
                service.getDespesas().put(chatId, despesas);
                service.proximaPergunta("Deseja Concluir a operação", ListasDeOpcoes.optionsSimOuNao("Finalizar", "Cancelar"));
                service.setarNovaEtapaDespesas(FluxoLancDespesas.FINALIZAR_LANCAMENTO);
            }

            case FINALIZAR_LANCAMENTO -> finalizacao(service);
        }


    }


    private void finalizacao(HandleService service) {
        if (service.getMensagemAtual().equalsIgnoreCase("btnNao")) {
            service.sendMessage("Operação Cancelada!");
            limparFluxo(service);
        } else {
            Despesas despesas = service.getDespesas().get(service.getChatId());
            despesasService.salvar(despesas);
            service.sendMessage("Lançamento Inserido com Sucesso!");
            limparFluxo(service);
        }
    }


    private void finalizarComDataInformada(HandleService service) {
        System.out.println("ENTREI AQUI msg atual: " + service.getMensagemAtual());
        String mensagemAtual = service.getMensagemAtual();
        Despesas despesas = service.getDespesas().get(service.getChatId());

        String[] data = mensagemAtual.split("/");
        String dia = data[0], mes = data[1];
        String ano = (data.length < 3) ? String.valueOf(LocalDate.now().getYear()) : data[2];
        LocalDate dataCompra = LocalDate.parse(dia + "/" + mes + "/" + ano, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        System.out.println("DATA COMPRA: " + dataCompra);
        despesas.setDataCompra(dataCompra);
        service.getDespesas().put(service.getChatId(), despesas);
        service.setarNovaEtapaDespesas(FluxoLancDespesas.FINALIZAR_LANCAMENTO);


    }


    public void finalizarComData(String msg, Long chatId, Despesas despesa) {


    }


    private void limparFluxo(HandleService service) {
        service.getDespesas().remove(service.getChatId());
        service.getLancDespesasMap().remove(service.getChatId());
        service.getEtapas().remove(service.getChatId());
        service.getLancDespesasMap().remove(service.getChatId());
    }


    private Double getValor(String mensagem) {
        String[] vlrArray = mensagem.split(";");
        String stringConverter = vlrArray[0].replace(",", ".").trim();
        return Double.parseDouble(stringConverter);
    }

    private String getDescricao(String mensagem) {
        try {
            String[] getDescricao = mensagem.split(";");
            return getDescricao[1].trim();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Descrição Inválida!");
        }
    }


    private Integer getParcelas(String mensagem) {
        String[] vlrArray = mensagem.split(";");

        if (vlrArray.length < 3) {
            return 1;
        }
        return Integer.parseInt(vlrArray[2].trim());
    }


}
