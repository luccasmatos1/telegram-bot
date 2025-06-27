package com.bot.telegram.app.core.utils;

import com.bot.telegram.app.domain.model.Cartoes;
import com.bot.telegram.app.domain.model.Categoria;
import com.bot.telegram.app.domain.model.enums.TipoMovimento;
import com.bot.telegram.app.domain.service.CartoesService;
import com.bot.telegram.app.domain.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ListasDeOpcoes {

    private final CartoesService cartoesService;
    private final CategoriaService categoriaService;


    public static List<InlineKeyboardButton> optionsReceitaOrDespesa() {
        InlineKeyboardButton btnReceita = new InlineKeyboardButton();
        btnReceita.setText("Receita");
        btnReceita.setCallbackData("btnReceita");

        InlineKeyboardButton btnDespesa = new InlineKeyboardButton();
        btnDespesa.setText("Despesa");
        btnDespesa.setCallbackData("btnDespesa");

        return new ArrayList<>(List.of(btnReceita, btnDespesa));
    }


    public static List<InlineKeyboardButton> optionsSimOuNao(String realiarOperacao, String negarOperacao) {
        InlineKeyboardButton btnSim = addButton(realiarOperacao, "btnSim");
        InlineKeyboardButton btnNao = addButton(negarOperacao, "btnNao");
        return new ArrayList<>(List.of(btnSim, btnNao));
    }

    public static List<InlineKeyboardButton> listaDeMenus() {
        InlineKeyboardButton lancamento = addButton("Novo Lançamento", "btnLancar");
        InlineKeyboardButton relatorio = addButton("Relatórios", "btnRelatorio");

        InlineKeyboardButton buttonCadastrar = addButton("Cadastro de Cartão/Categoria", "btnCadastrar");
        InlineKeyboardButton buttonLancarRapido = addButton("Lançar Rápido (Despesa)", "btnLancarRapido");

        return new ArrayList<>(List.of(buttonLancarRapido,lancamento, buttonCadastrar,relatorio));
    }


    public static List<InlineKeyboardButton> listaCadastro() {
        InlineKeyboardButton btn1 = addButton("Novo Cartão", "btnCartao");
        InlineKeyboardButton btn2 = addButton("Nova Categoria", "btnCategoria");

        return new ArrayList<>(List.of(btn1, btn2));
    }

    public static List<InlineKeyboardButton> listaCategoriaTipo() {
        InlineKeyboardButton btn1 = addButton("Fixa", "btnFixa");
        InlineKeyboardButton btn2 = addButton("Variavel", "btnVariavel");

        return new ArrayList<>(List.of(btn1, btn2));
    }


    private static InlineKeyboardButton addButton(String text, String callbackData) {

        InlineKeyboardButton btn = new InlineKeyboardButton();
        btn.setText(text);
        btn.setCallbackData(callbackData);
        return btn;
    }


    public List<InlineKeyboardButton> listarCartoes() {

        List<Cartoes> cartoes = cartoesService.findAll();


        List<InlineKeyboardButton> list = new ArrayList<>();


        cartoes.stream().map(Cartoes::getNome).filter(Objects::nonNull).forEach(nome -> {
            InlineKeyboardButton btn = new InlineKeyboardButton();
            btn.setText(nome);
            btn.setCallbackData(nome);
            list.add(btn);
        });


        return list;

    }


    public List<InlineKeyboardButton> listarCategoriasDespesa() {
        return categoriaService
                .findAll()
                .stream()
                .filter(categoria -> categoria.getTipo().equals(TipoMovimento.DESPESA))
                .map(Categoria::getDescricao)
                .filter(Objects::nonNull)
                .map(nome -> {
                    InlineKeyboardButton btn = new InlineKeyboardButton();
                    btn.setText(nome);
                    btn.setCallbackData(nome);
                    return btn;
                }).toList();
    }


    public List<InlineKeyboardButton> listarCategoriasReceitas() {
        return categoriaService
                .findAll()
                .stream()
                .filter(categoria -> categoria.getTipo().equals(TipoMovimento.RECEITA))
                .map(Categoria::getDescricao)
                .filter(Objects::nonNull)
                .map(nome -> {
                    InlineKeyboardButton btn = new InlineKeyboardButton();
                    btn.setText(nome);
                    btn.setCallbackData(nome);
                    return btn;
                }).toList();
    }
}
