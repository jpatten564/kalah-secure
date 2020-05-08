package com.jpatten.kalah.repository;

import com.jpatten.kalah.model.Game;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface GameRepository extends CrudRepository<Game, Long> {

    @Query("select g from Game g")
    Stream<Game> streamAll();

}
