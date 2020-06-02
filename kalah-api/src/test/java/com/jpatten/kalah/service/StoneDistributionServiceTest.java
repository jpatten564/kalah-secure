package com.jpatten.kalah.service;

import com.jpatten.kalah.exception.IllegalMoveException;
import com.jpatten.kalah.model.Game;
import com.jpatten.kalah.model.GameStatus;
import com.jpatten.kalah.model.Pit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.util.Pair;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.util.Pair.of;

@RunWith(MockitoJUnitRunner.class)
public class StoneDistributionServiceTest {

    private static final int[] COMPLETE_GAME_SEQUENCE = {
            1, 2, 8, 1, 9, 3, 10, 4, 11, 3, 8, 4, 10,
            2, 11, 3, 9, 4, 12, 4, 11, 3, 13, 12, 2,
            11, 3, 10, 4, 9, 5, 13, 4, 11, 3, 8, 2, 10,
            6, 13, 12, 5, 13, 11, 3, 12, 4, 10, 5, 11};
    @Spy
    private PitResolverService pitResolverService;

    @InjectMocks
    private StoneDistributorService stoneDistributionService;

    @Test
    public void stones_distributed_correctly() {

        Game game = getGame();
        move(game, 1);

        assertCorrectPitStoneCounts(game, asMap(
                of(1,0), of(2,7), of(3, 7), of(4, 7), of(5, 7), of(6, 7), of(7, 1),
                of(8,6), of(9,6), of(10,6), of(11,6), of(12,6), of(13,6), of(14,0)
        ));
    }

    @Test
    public void last_stone_placed_in_house_yields_another_turn() {
        Game game = getGame();

        assertThat(game.getGameStatus()).isEqualTo(GameStatus.PLAYER_ONE_TURN);

        move(game, 1);

        // TODO: GameStatus is deliberately toggled in StoneDistributionService to ensure
        //  that when it is toggled back by the GameService, the value is correct.
        //  This needs addressing as it's pretty confusing.
        assertThat(game.getGameStatus()).isEqualTo(GameStatus.PLAYER_ONE_TURN);
    }

    @Test
    public void last_stone_in_an_empty_pit_gathers_opposing_stones() {
        Game game = getGame();
        move(game, 1,2,8,1);

        assertCorrectPitStoneCounts(game, asMap(
                of(1,0), of(2,0), of(3, 8), of(4, 8), of(5, 8), of(6, 8), of(7, 10),
                of(8,0), of(9,8), of(10,7), of(11,7), of(12,0), of(13,7), of(14,1)
        ));
    }

    @Test(expected = IllegalMoveException.class)
    public void cannot_distribute_stones_from_empty_pit() {
        Game game = getGame();
        game.getBoard().getPits().forEach(Pit::clearStones);
        move(game,1);
    }

    @Test
    public void game_runs_to_completion() {
        Game game = getGame();

        move(game, COMPLETE_GAME_SEQUENCE);

        assertThat(game.isComplete()).isTrue();
    }

    private void move(Game game, int... pitIndex) {
        Arrays.stream(pitIndex).boxed()
                .forEach(i -> {
                    stoneDistributionService.distributeStonesFromPit(game, i);
                    game.setGameStatus(game.getGameStatus().togglePlayer());
                });
    }

    private void assertCorrectPitStoneCounts(Game game, Map<Integer, Integer> expected) {
        getPitsAsMap(game).forEach((key, value) ->
                assertThat(expected.get(key)).isEqualTo(value));
    }

    public Map<Integer, Integer> asMap(Pair<Integer,Integer>... keyValuePair) {
        return Arrays.stream(keyValuePair)
                .collect(toMap(Pair::getFirst, Pair::getSecond));
    }

    private Map<Integer, Integer> getPitsAsMap(Game game) {
        return game.getBoard().getPits().stream()
                .collect(toMap(Pit::getIndex, Pit::getStoneCount));
    }

    private Game getGame() {
        Game game = Game.createGame("player1", "player2");
        game.getPlayerOne().setId(1L);
        game.getPlayerTwo().setId(2L);
        return game;
    }
}
