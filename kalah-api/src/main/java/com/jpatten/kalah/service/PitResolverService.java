package com.jpatten.kalah.service;

import com.google.common.collect.ImmutableMap;
import com.jpatten.kalah.exception.IllegalMoveException;
import com.jpatten.kalah.model.Game;
import com.jpatten.kalah.model.Pit;
import com.jpatten.kalah.model.Player;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PitResolverService {

    private static final Map<Integer, Integer> OPPOSING_PIT_MAP = new ImmutableMap.Builder<Integer, Integer>()
            .put(1, 13).put(2, 12).put(3, 11).put(4, 10).put(5, 9).put(6, 8)
            .put(13, 1).put(12, 2).put(11, 3).put(10, 4).put(9,5).put(8,6)
            .build();

    Pit getPitBelongingToCurrentPlayer(int pitIndex, Game game) {

        Pit pit = getPit(pitIndex, game);
        if (!pit.belongsToPlayer(game.getCurrentPlayer())) {
            throw new IllegalMoveException(String.format("Unable to find pit with index %s for player %s",
                    pitIndex, game.getCurrentPlayer()));
        }
        return pit;
    }

    private Pit getPit(int pitIndex, Game game) {

        return game.getBoard().getPits().stream()
                .filter(p -> p.hasIndex(pitIndex))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Unable to find pit with index %s", pitIndex)));
    }

    Pit getHouse(Player player, Game game) {
        return game.getBoard().getPits().stream()
                .filter(Pit::isHouse)
                .filter(pit -> pit.belongsToPlayer(player))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Unable to find house for player"));
    }

    Pit nextStoneRecipientPit(Pit currentPit, Game game) {
        int nextIndex = currentPit.getIndex() + 1;
        if (nextIndex > currentPit.getBoard().getPits().size()) {
            nextIndex = 1;
        }
        Pit pit = getPit(nextIndex, game);
        if (pit.isHouse() && !pit.belongsToPlayer(game.getCurrentPlayer())) {
            return nextStoneRecipientPit(pit, game);
        }
        return pit;
    }

    Pit getOpposingPit(Pit pit, Game game) {
        return getPit(OPPOSING_PIT_MAP.get(pit.getIndex()), game);
    }
}
