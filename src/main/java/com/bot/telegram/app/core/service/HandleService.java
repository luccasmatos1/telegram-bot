package com.bot.telegram.app.core.service;

import com.bot.telegram.app.core.TelegramBot;
import com.bot.telegram.app.core.enums.*;
import com.bot.telegram.app.core.utils.ListasDeOpcoes;
import com.bot.telegram.app.domain.model.Cartoes;
import com.bot.telegram.app.domain.model.Categoria;
import com.bot.telegram.app.domain.model.Despesas;
import com.bot.telegram.app.domain.service.CartoesService;
import com.bot.telegram.app.domain.service.CategoriaService;
import com.bot.telegram.app.domain.service.DespesasService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

@Service
@Getter
@RequiredArgsConstructor
public class HandleService {


    private final Map<Long, EtapasMenuPrincipal> etapas = new HashMap<>();
    private final Map<Long, FluxoCadCartao> subEtapaMap = new HashMap<>();
    private final Map<Long, Despesas> despesas = new HashMap<>();
    private final Map<Long, Cartoes> cartoesMap = new HashMap<>();
    private final Map<Long, Categoria> categoriaMap = new HashMap<>();
    private final Map<Long, FluxoCadCategoria> cadCategoriaEtapas = new HashMap<>();
    private final Map<Long, FluxoLancDespesas> lancDespesasMap = new HashMap<>();
    private final Map<Long, FluxoLancarReceitaEnum> fluxoLancarReceitaMap = new HashMap<>();

    @Setter
    private TelegramBot bot;
    @Setter
    private Long chatId;
    @Setter
    private String mensagemAtual;


    private final DespesasService service;
    private final CategoriaService categoriaService;
    private final CartoesService cartoesService;
    private final ListasDeOpcoes listasDeOpcoes;


    public void sendMessage(String msg) {
        bot.sendMessage(msg, this.chatId);
    }

    private void sendMessageComOpcoes(Long chatId, String msg, List<InlineKeyboardButton> opcoes) {
        bot.sendMessageComOpcoes(chatId, msg, opcoes);
    }

    public void mensagemInicial(String mensagem) {
        sendMessage(mensagem);
    }

    public void proximaMensagem(String mensagem) {
        sendMessage(mensagem);
    }

    public void proximaPergunta(String mensagem, List<InlineKeyboardButton> opcoes) {
        sendMessageComOpcoes(this.chatId, mensagem, opcoes);
    }


    public void iniciarBot() {
        sendMessageComOpcoes(this.chatId, "Seja bem vindo!", ListasDeOpcoes.listaDeMenus());
        setarProximaEtapa(EtapasMenuPrincipal.SELECIONAR_MENU);
    }


    public void handleInicio() {
        try {
            switch (this.mensagemAtual) {

                case "btnCadastrar" -> {
                    setarProximaEtapa(EtapasMenuPrincipal.CADASTRO);
                    proximaPergunta("Qual o tipo de cadastro?", ListasDeOpcoes.listaCadastro());
                }
                case "btnLancarRapido" -> {
                    setarProximaEtapa(EtapasMenuPrincipal.HANDLER_LANCAR_DESPESA);
                    setarNovaEtapaDespesas(FluxoLancDespesas.LANCAMENTO_RAPIDO);
                    proximaMensagem("Infome o valor,descricao e qtd de parcelas\nobs.: delimitado por ;");
                }
                case "btnLancar" -> {
                    setarProximaEtapa(EtapasMenuPrincipal.HANDLER_NOVO_LANCAMENTO);
                    proximaPergunta("Qual o tipo de lançamento?", ListasDeOpcoes.optionsReceitaOrDespesa());
                }
                case "btnRelatorio" -> {
                    setarProximaEtapa(EtapasMenuPrincipal.RELATORIO);
                    sendMessage("Verificar o que tem para pagar em qual mês/ano?");
                }
                default -> {
                    sendMessage("Opção Inválida!");
                }
            }

        } catch (Exception e) {
            sendMessage("Opção Inválida!");
        }
    }


    public void novoLancamento() {
        switch (this.mensagemAtual) {
            case "btnReceita" -> {
                setarProximaEtapa(EtapasMenuPrincipal.HANDLER_LANCAR_RECEITA);
                proximaPergunta("Qual categoria?", listasDeOpcoes.listarCategoriasReceitas());
            }
            case "btnDespesa" -> {
                setarProximaEtapa(EtapasMenuPrincipal.HANDLER_LANCAR_DESPESA);
                proximaPergunta("Qual categoria?", listasDeOpcoes.listarCategoriasDespesa());
            }
        }
    }


    public void novoCadastro(String msg) {

        switch (msg) {
            case "btnCategoria" -> {
                System.out.println("btnCategoria CLICADO");
                setarProximaEtapa(EtapasMenuPrincipal.NOVO_CADASTRO_CATEGORIA);

                proximaPergunta("Qual o tipo de Categoria", ListasDeOpcoes.optionsReceitaOrDespesa());
            }
            case "btnCartao" -> {
                System.out.println("btnCartao CLICADO");
                setarProximaEtapa(EtapasMenuPrincipal.NOVO_CADASTRO_CARTOES);
                mensagemInicial("Qual o nome do Cartão?");

            }
            default -> throw new RuntimeException("Opção Inválida!");
        }

    }


    public void relatorio(String msg, Long chatId) {
        if (msg.toLowerCase().startsWith("receita")) {
            sendMessage("Informe a referência: MM/AAAA");
//            etapas.put(chatId,"REL_RECEITAS");
        }

        if (msg.toLowerCase().startsWith("despesa")) {
            sendMessage("Informe a referência: MM/AAAA");
//            etapas.put(chatId,"REL_DESPESAS");
        }

        if (msg.toLowerCase().startsWith("receitas/despesas")) {
            sendMessage("Informe a referência: MM/AAAA");
//            etapas.put(chatId,"REL_GERAL");
        }


    }


    public void setarProximaEtapa(EtapasMenuPrincipal novaEtapa) {
        this.etapas.put(this.chatId, novaEtapa);
    }

    public void setarSubEtapa(FluxoCadCartao novaFluxoCadCartao) {
        this.subEtapaMap.put(this.chatId, novaFluxoCadCartao);
    }


    public void setarNovaEtapaCategoria(FluxoCadCategoria novaEtapa) {
        cadCategoriaEtapas.put(this.chatId, novaEtapa);
    }

    public void setarNovaEtapaReceitas(FluxoLancarReceitaEnum novaEtapa) {
        fluxoLancarReceitaMap.put(this.chatId, novaEtapa);
    }


    public void setarNovaEtapaDespesas(FluxoLancDespesas novaEtapa) {
        lancDespesasMap.put(this.chatId, novaEtapa);
    }


    public void limparFluxos(Long chatId) {
        despesas.remove(chatId);
        subEtapaMap.remove(chatId);
        cadCategoriaEtapas.remove(chatId);
        lancDespesasMap.remove(chatId);
        categoriaMap.remove(chatId);
        cartoesMap.remove(chatId);
        etapas.put(chatId, EtapasMenuPrincipal.INICIAR_BOT);


    }


}
