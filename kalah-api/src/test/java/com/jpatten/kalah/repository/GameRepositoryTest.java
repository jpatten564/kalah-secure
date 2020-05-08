package com.jpatten.kalah.repository;

import com.jpatten.kalah.model.Board;
import com.jpatten.kalah.model.Game;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.jpatten.kalah.model.Board.PIT_COUNT;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class GameRepositoryTest {

    @Autowired
    private GameRepository gameRepository;

    @Test
    public void game_is_saved() {

        Game savedGame = gameRepository.save(Game.createGame("player1", "player2"));

        Optional<Game> byId = gameRepository.findById(savedGame.getId());
        assertThat(byId).isPresent();

        Game reloadedGame = byId.get();

        assertThat(reloadedGame.getPlayerOne()).isNotNull();
        assertThat(reloadedGame.getPlayerTwo()).isNotNull();

        Board board = reloadedGame.getBoard();
        assertThat(board).isNotNull();
        assertThat(board.getPits()).hasSize(PIT_COUNT);
    }

    @Test
    public void games_are_streamed() {
        Game savedGame = gameRepository.save(Game.createGame("player1", "player2"));

        List<Game> games = gameRepository.streamAll().collect(toList());

        assertThat(games).hasSize(1);

        assertThat(games.stream()
                .findFirst().map(Game::getId)
                .orElse(-1L))
                .isEqualTo(savedGame.getId());
    }

    @Test
    public void findById_finds_game() {
        Game savedGame = gameRepository.save(Game.createGame("player1", "player2"));

        Optional<Game> game = gameRepository.findById(savedGame.getId());

        assertThat(game).isPresent();
    }

}
