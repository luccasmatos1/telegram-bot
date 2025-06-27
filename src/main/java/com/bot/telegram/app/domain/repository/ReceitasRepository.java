package com.bot.telegram.app.domain.repository;

import com.bot.telegram.app.domain.model.Categoria;
import com.bot.telegram.app.domain.model.Receitas;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReceitasRepository extends MongoRepository<Receitas,Long> {


    List<Receitas> findByCategoria(Categoria categoria);
}
