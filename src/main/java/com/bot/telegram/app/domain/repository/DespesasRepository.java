package com.bot.telegram.app.domain.repository;

import com.bot.telegram.app.domain.model.Categoria;
import com.bot.telegram.app.domain.model.Despesas;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DespesasRepository extends MongoRepository<Despesas, Long> {

    @Query("{ 'dataPagamento': { $gte: ?0, $lt: ?1 } }")
    List<Despesas> findByDataPagamentoBetween(LocalDate inicio, LocalDate fim);

    List<Despesas> findByCategoria(Categoria categoria);
}
