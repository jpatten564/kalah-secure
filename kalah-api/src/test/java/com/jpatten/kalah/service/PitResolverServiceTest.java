package com.jpatten.kalah.service;

import com.jpatten.kalah.exception.IllegalMoveException;
import com.jpatten.kalah.model.Game;
import com.jpatten.kalah.model.Pit;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PitResolverServiceTest {

    @Test
    public void finds_pit_belonging_to_player() {
        Game game = getGame();

        Pit pit = new PitResolverService().getPitBelongingToCurrentPlayer(1, game);

        assertThat(pit).isNotNull();
    }

    @Test(expected = IllegalMoveException.class)
    public void throws_if_pit_does_not_belong_to_player() {
        Game game = getGame();

        new PitResolverService().getPitBelongingToCurrentPlayer(9, game);
    }

    @Test
    public void finds_players_house() {
        Game game = getGame();

        Pit player1House = new PitResolverService().getHouse(game.getPlayerOne(), game);

        assertThat(player1House.isHouse()).isTrue();
        assertThat(player1House.getIndex()).isEqualTo(7);

        Pit player2House = new PitResolverService().getHouse(game.getPlayerTwo(), game);

        assertThat(player2House.isHouse()).isTrue();
        assertThat(player2House.getIndex()).isEqualTo(14);
    }

    @Test(expected = IllegalStateException.class)
    public void throws_if_house_not_found() {
        Game game = getGame();
        game.getBoard().getPits().clear();
        new PitResolverService().getHouse(game.getPlayerOne(), game);
    }

    @Test
    public void next_stone_resolved_correctly() {
        Game game = getGame();
        Pit pit = getPit(game, 1);

        Pit next = new PitResolverService().nextStoneRecipientPit(pit, game);
        assertThat(next.hasIndex(2)).isTrue();
    }

    @Test
    public void next_stone_skips_opponents_house() {
        Game game = getGame();
        Pit pit = getPit(game, 13);

        Pit next = new PitResolverService().nextStoneRecipientPit(pit, game);
        assertThat(next.hasIndex(1)).isTrue();
    }

    @Test
    public void returns_correct_opposing_pit() {
        Game game = getGame();
        Pit pit = getPit(game, 1);

        Pit opposing = new PitResolverService().getOpposingPit(pit, game);
        assertThat(opposing.hasIndex(13)).isTrue();
    }

    // TODO: this code duplicates code within PitResolverService. Test setup should
    // probably be simpler than this...
    private Pit getPit(Game game, int index) {
        return game.getBoard().getPits().stream()
                .filter(pit -> pit.hasIndex(index)).findFirst()
                .orElseThrow(() -> new RuntimeException("Unable to setup test - pit lookup failed"));
    }

    private Game getGame() {
        Game game = Game.createGame("player1", "player2");
        game.getPlayerOne().setId(1L);
        game.getPlayerTwo().setId(2L);
        return game;
    }

}
