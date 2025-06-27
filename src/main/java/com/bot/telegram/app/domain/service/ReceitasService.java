package com.bot.telegram.app.domain.service;

import com.bot.telegram.app.domain.model.Categoria;
import com.bot.telegram.app.domain.model.Despesas;
import com.bot.telegram.app.domain.model.Receitas;
import com.bot.telegram.app.domain.repository.ReceitasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ReceitasService {


    private final ReceitasRepository repository;

    public void salvar (Receitas receitas) {
        receitas.setId(new Random().nextLong());
        repository.save(receitas);
    }



    public List<Receitas> findByCategoria (Categoria categoria) {
        return repository.findByCategoria(categoria);
    }
}
