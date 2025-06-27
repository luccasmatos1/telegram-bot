package com.bot.telegram.app.domain.repository;

import com.bot.telegram.app.domain.model.Cartoes;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CartoesRepository extends MongoRepository<Cartoes,Long> {

    Optional<Cartoes> findByNome(String nome);
}
