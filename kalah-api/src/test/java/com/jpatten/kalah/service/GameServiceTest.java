package com.jpatten.kalah.service;

import com.jpatten.kalah.exception.GameNotFoundException;
import com.jpatten.kalah.model.Game;
import com.jpatten.kalah.model.dto.GameDto;
import com.jpatten.kalah.repository.GameRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GameServiceTest {

    private static final String DUMMY_URI = "uri";
    private static final Function<Game, String> URI_PROVIDER = game -> DUMMY_URI;
    private static final long GAME_ID = 1L;

    @InjectMocks
    private GameService gameService;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private StoneDistributorService stoneDistributorService;

    @Before
    public void setup() {
        Game game = Game.createGame("player1", "player2");
        game.setId(1L);
        when(gameRepository.save(any(Game.class)))
                .thenReturn(game);
    }

    @Test
    public void create_returns_valid_dto() {
        GameDto gameDto = gameService.createGame(URI_PROVIDER, "player1", "player2");

        assertThat(gameDto.getId()).isEqualTo(String.valueOf(GAME_ID));
        assertThat(gameDto.getUri()).isEqualTo(DUMMY_URI);

        verifyGameSaved();
    }

    @Test(expected = GameNotFoundException.class)
    public void play_throws_if_game_not_found() {
        when(gameRepository.findById(any()))
                .thenReturn(Optional.empty());

        gameService.play(1234L, 1, URI_PROVIDER);
    }

    @Test
    public void stones_distributed() {
        Game game = mock(Game.class);
        when(gameRepository.findById(any()))
                .thenReturn(Optional.of(game));

        gameService.play(1234L, 1, URI_PROVIDER);

        verify(stoneDistributorService, times(1)).distributeStonesFromPit(any(), eq(1));
        verify(game, times(1)).nextTurn();

        verifyGameSaved();
    }

    @Test
    public void remaining_stones_gathered_on_game_complete() {
        Game game = mock(Game.class);
        when(game.isComplete()).thenReturn(true);
        when(gameRepository.findById(any()))
                .thenReturn(Optional.of(game));

        gameService.play(1L, 1, URI_PROVIDER);

        verify(stoneDistributorService, times(1)).gatherRemainingStonesIntoHouse(any());
        verifyGameSaved();
    }

    @Test
    public void next_turn_on_play_end() {
        Game game = mock(Game.class);
        when(gameRepository.findById(any()))
                .thenReturn(Optional.of(game));

        gameService.play(1L, 1, URI_PROVIDER);

        verify(game, times(1)).nextTurn();
        verifyGameSaved();
    }

    @Test
    public void finds_all_games() {
        when(gameRepository.streamAll())
                .thenReturn(Stream.of(Game.createGame("player1", "player2"),
                        Game.createGame("player1", "player2")));

        List<GameDto> games = gameService.findAll(URI_PROVIDER);

        verify(gameRepository, times(1)).streamAll();
        assertThat(games).hasSize(2);
    }

    @Test
    public void finds_game_by_id() {
        when(gameRepository.findById(anyLong()))
                .thenReturn(getGame());

        GameDto gameDto = gameService.findById(1L, URI_PROVIDER);

        verify(gameRepository, times(1)).findById(anyLong());
        assertThat(gameDto).isNotNull();
    }

    private Optional<Game> getGame() {
        return Optional.of(Game.createGame("player1", "player2"));
    }

    @Test(expected = GameNotFoundException.class)
    public void findById_throws_if_game_not_found() {
        when(gameRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        gameService.findById(1L, URI_PROVIDER);
    }

    @Test
    public void isNextPlayer_returns_false_given_incorrect_player() {
        when(gameRepository.findById(anyLong()))
                .thenReturn(getGame());

        assertThat(gameService.isNextPlayer(1L, "wrong_player"))
                .isFalse();
    }

    @Test
    public void isNextPlayer_returns_true_given_correct_player() {
        when(gameRepository.findById(anyLong()))
                .thenReturn(getGame());

        assertThat(gameService.isNextPlayer(1L, "player1"))
                .isTrue();
    }

    private void verifyGameSaved() {
        verify(gameRepository, times(1)).save(any());
    }
}
