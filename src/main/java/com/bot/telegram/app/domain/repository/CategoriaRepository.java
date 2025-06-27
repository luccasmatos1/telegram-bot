package com.bot.telegram.app.domain.repository;

import com.bot.telegram.app.domain.model.Categoria;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CategoriaRepository extends MongoRepository<Categoria,Long> {

    Optional<Categoria> findByDescricao(String nome);
}
