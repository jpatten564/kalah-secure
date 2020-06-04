package com.jpatten.kalah.model;

import java.util.Set;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toSet;

class PitGenerator {

    static final int INITIAL_STONE_COUNT = 6;

    Set<Pit> generatePits(Player player1, Player player2) {
        return IntStream.range(1, Board.PIT_COUNT+1).boxed()
                .map(i -> createPit(i, player1, player2))
                .collect(toSet());
    }

    private Pit createPit(Integer index, Player player1, Player player2) {
        Pit pit = new Pit();
        if (isHouse(index)) {
            pit.setHouse(true);
        }
        else {
            pit.setStoneCount(INITIAL_STONE_COUNT);
        }
        Player pitOwner = index <= 7 ? player1 : player2;
        pitOwner.addAssignedPit(pit);

        pit.setIndex(index);
        return pit;
    }

    private boolean isHouse(Integer position) {
        return position % 7 == 0;
    }

}
