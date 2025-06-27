package com.bot.telegram.app.domain.service;

import com.bot.telegram.app.core.dto.request.CategoriaRequest;
import com.bot.telegram.app.domain.model.Categoria;
import com.bot.telegram.app.domain.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CategoriaService {


    private final CategoriaRepository repository;


    public void removeAll() {
        repository.deleteAll();
    }

    public void salvar (CategoriaRequest request){
        Categoria categoria = new Categoria();
        categoria.setId(new Random().nextLong());
        categoria.setDescricao(request.nome());
        categoria.setFixa(request.fixa());
        categoria.setTipo(request.tipo());
        categoria.setDiaPagamento(request.diaVencimento());
        repository.save(categoria);
    }


    public List<Categoria> findAll () {
        return repository.findAll();
    }

    public Categoria findById (Long id) {
        return repository.findById(id).orElse(null);
    }
    public Categoria findByDescricao (String descricao) {
        return repository.findByDescricao(descricao).orElse(null);
    }
}
