package com.bot.telegram.app.core.fluxo;

import com.bot.telegram.app.core.service.HandleService;
import com.bot.telegram.app.core.enums.FluxoCadCartao;
import com.bot.telegram.app.core.dto.request.CartaoRequest;
import com.bot.telegram.app.core.utils.ListasDeOpcoes;
import com.bot.telegram.app.domain.model.Cartoes;
import com.bot.telegram.app.domain.service.CartoesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class FluxoCadastroCartao {


    private final CartoesService cartoesService;

    public void cadastroCartaoService (HandleService service) {
        Long chatId = service.getChatId();
        Map<Long,Cartoes> cartoesMap = service.getCartoesMap();
        String mensagemAtual = service.getMensagemAtual();

        FluxoCadCartao fluxoCadCartao = service.getSubEtapaMap().getOrDefault(chatId, FluxoCadCartao.CAD_CARTAO_INFORMAR_NOME);
        Cartoes cartao = cartoesMap.getOrDefault(chatId,new Cartoes());

        try {
            switch (fluxoCadCartao){
                case CAD_CARTAO_INFORMAR_NOME -> {
                    cartao.setNome(mensagemAtual);
                    cartoesMap.put(chatId,cartao);
                    System.out.println("Entrei no case informar nome e o nome é: " + mensagemAtual);
                    service.setarSubEtapa(FluxoCadCartao.CAD_CARTAO_INFORMAR_DIA_FECHAMENTO);
                    service.proximaMensagem("Informar Fechamento");
                }
                case CAD_CARTAO_INFORMAR_DIA_FECHAMENTO -> {


                    cartao.setDiaFechamento(Integer.parseInt(mensagemAtual));
                    cartoesMap.put(chatId,cartao);
                    service.setarSubEtapa(FluxoCadCartao.CAD_CARTAO_INFORMAR_DIA_PRIMEIRO_VENCIMENTO);
                    service.proximaMensagem("Informar Vencimento");


                }
                case CAD_CARTAO_INFORMAR_DIA_PRIMEIRO_VENCIMENTO -> {
                    cartao.setDiaVencimento(Integer.parseInt(mensagemAtual));
                    service.setarSubEtapa(FluxoCadCartao.FINALIZAR_CADASTRO_CARTAO);
                    service.proximaPergunta("Confirmar Lançamento?", ListasDeOpcoes.optionsSimOuNao("Finalizar","Cancelar"));
                }

                case FINALIZAR_CADASTRO_CARTAO -> finalizarCadastroCartaoService(service);

            }
        } catch (NumberFormatException e) {
            service.sendMessage("Por favor, informe um valor Válido!");
        }


    }



    private void finalizarCadastroCartaoService(HandleService service) {
        String opcao = service.getMensagemAtual();

        try {
            if (!opcao.equalsIgnoreCase("btnSim") && !opcao.equalsIgnoreCase("btnNao")) {throw new IllegalArgumentException("Opção Inválida!");}



            if (opcao.equalsIgnoreCase("btnSim")){
                if (service.getCartoesMap().containsKey(service.getChatId())){
                    Cartoes cartoes = service.getCartoesMap().get(service.getChatId());
                    CartaoRequest request = new CartaoRequest(
                            cartoes.getNome(),
                            cartoes.getDiaVencimento(),
                            cartoes.getDiaFechamento()
                    );

                    cartoesService.salvarCartao(request);
                    service.sendMessage("Cartão Inserido com Sucesso!");
                }
            }

            if (opcao.equalsIgnoreCase("btnNao")) {service.sendMessage("Operação Cancelada!");}




            service.getCartoesMap().remove(service.getChatId());
            service.getSubEtapaMap().remove(service.getChatId());
            service.getEtapas().remove(service.getChatId());
        } catch (Exception e) {
            service.proximaPergunta("Opção inválida!, Selecione Sim ou Não para finalizar o cadastro", ListasDeOpcoes.optionsSimOuNao("Finalizar","Cancelar"));
        }




    }

}
