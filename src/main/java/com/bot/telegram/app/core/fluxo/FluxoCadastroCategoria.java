package com.bot.telegram.app.core.fluxo;

import com.bot.telegram.app.core.enums.FluxoCadCategoria;
import com.bot.telegram.app.core.service.HandleService;
import com.bot.telegram.app.core.utils.ListasDeOpcoes;
import com.bot.telegram.app.domain.model.Categoria;
import com.bot.telegram.app.domain.model.enums.TipoMovimento;
import com.bot.telegram.app.domain.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FluxoCadastroCategoria {

    private final CategoriaService categoriaService;


    public void cadastroCategoriaService (HandleService handleService) {
        Long chatId = handleService.getChatId();
        String mensagemAtual = handleService.getMensagemAtual();
        Categoria categoria = handleService.getCategoriaMap().getOrDefault(chatId,new Categoria());
        FluxoCadCategoria etapaAtual = handleService.getCadCategoriaEtapas().getOrDefault(chatId, FluxoCadCategoria.SETAR_TIPO_CATEGORIA);


        switch (etapaAtual) {
            case SETAR_TIPO_CATEGORIA -> {
                categoria.setTipo(mensagemAtual.equalsIgnoreCase("btnReceita") ? TipoMovimento.RECEITA : TipoMovimento.DESPESA);
                handleService.getCategoriaMap().put(chatId,categoria);
                handleService.setarNovaEtapaCategoria(FluxoCadCategoria.SETAR_FIXA_OU_VARIAVEL);
                handleService.proximaPergunta("Renda fixa ou variavel? ", ListasDeOpcoes.listaCategoriaTipo());
            }

            case SETAR_FIXA_OU_VARIAVEL -> {
                categoria.setFixa(mensagemAtual.equalsIgnoreCase("btnFixa"));
                handleService.getCategoriaMap().put(chatId,categoria);
                handleService.setarNovaEtapaCategoria(FluxoCadCategoria.INFORMAR_NOME_CATEGORIA);
                handleService.proximaMensagem("Informe o nome da categoria:");
            }
            case INFORMAR_NOME_CATEGORIA -> {
                categoria.setDescricao(mensagemAtual);
                handleService.getCategoriaMap().put(chatId,categoria);
                handleService.setarNovaEtapaCategoria(FluxoCadCategoria.SETAR_DIA_VENCIMENTO);
                handleService.proximaMensagem("Qual o dia do vencimento?");
            }

            case SETAR_DIA_VENCIMENTO -> {
                categoria.setDiaPagamento(Integer.parseInt(mensagemAtual));
                handleService.getCategoriaMap().put(chatId,categoria);
                handleService.setarNovaEtapaCategoria(FluxoCadCategoria.FINALIZAR_CADASTRO);
                handleService.proximaPergunta("Salvar Lançamento? " , ListasDeOpcoes.optionsSimOuNao("Finalizar","Cancelar"));
            }

            case FINALIZAR_CADASTRO -> salvarCategoriaService(handleService);

        }

    }

    private void salvarCategoriaService(HandleService handleService) {
        if (handleService.getMensagemAtual().equalsIgnoreCase("btnSim")) {
            Categoria categoria = handleService.getCategoriaMap().get(handleService.getChatId());
            categoriaService.salvar(new com.bot.telegram.app.core.dto.request.CategoriaRequest(
                    categoria.getDescricao(),
                    categoria.getFixa(),
                    categoria.getTipo(),
                    categoria.getDiaPagamento()
            ));

            handleService.sendMessage("Categoria inserida com sucesso!");
        }


        if (handleService.getMensagemAtual().equalsIgnoreCase("btnNao")) {handleService.sendMessage("Operação Cancelada!");}

        handleService.getEtapas().remove(handleService.getChatId());
        handleService.getCadCategoriaEtapas().remove(handleService.getChatId());
        handleService.getCategoriaMap().remove(handleService.getChatId());
    }
}
