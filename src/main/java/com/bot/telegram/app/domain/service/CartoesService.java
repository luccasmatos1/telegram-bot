package com.bot.telegram.app.domain.service;

import com.bot.telegram.app.core.dto.request.CartaoRequest;
import com.bot.telegram.app.domain.model.Cartoes;
import com.bot.telegram.app.domain.repository.CartoesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CartoesService {


    private final CartoesRepository repository;


    public void salvarCartao (CartaoRequest request){
        Cartoes cartoes = new Cartoes();
        cartoes.setId(new Random().nextLong());
        cartoes.setNome(request.nome());
        cartoes.setDiaFechamento(request.diaFechamento());
        cartoes.setDiaVencimento(request.diaVencimento());
        repository.save(cartoes);

    }

    public List<Cartoes> findAll () {
        return repository.findAll();
    }

    public Cartoes findById (Long id) {
        return repository.findById(id).orElse(null);
    }

    public Cartoes findByNome (String nome) {
        return repository.findByNome(nome).orElse(null);
    }


}
