package com.jpatten.kalah.model;

import org.junit.Test;

import java.util.Set;
import java.util.stream.Stream;

import static com.jpatten.kalah.model.PitGenerator.INITIAL_STONE_COUNT;
import static org.assertj.core.api.Assertions.assertThat;

public class PitGeneratorTest {

    @Test
    public void pits_have_with_correct_stone_count() {

        Stream<Pit> pitsExcludingHouses = generatePits()
                .stream().filter(pit -> !pit.isHouse());

        assertThat(pitsExcludingHouses)
                .extracting(Pit::getStoneCount)
                .containsOnly(INITIAL_STONE_COUNT);
    }

    @Test
    public void houses_have_no_stones() {
        Stream<Pit> houses = generatePits()
                .stream().filter(Pit::isHouse);

        assertThat(houses)
                .extracting(Pit::getStoneCount)
                .containsOnly(0);
    }

    @Test
    public void houses_have_correct_indexes() {
        assertThat(generatePits()
                .stream().filter(Pit::isHouse)
                .map(Pit::getIndex))
                .containsOnly(7, 14);
    }

    @Test
    public void players_assigned_pits_correctly() {
        Player player1 = new Player("player1");
        Player player2 = new Player("player2");

        new PitGenerator().generatePits(player1, player2);

        assertThat(player1.getAssignedPits())
                .extracting(Pit::getIndex)
                .containsExactlyInAnyOrder(1,2,3,4,5,6,7);

        assertThat(player2.getAssignedPits())
                .extracting(Pit::getIndex)
                .containsExactlyInAnyOrder(8,9,10,11,12,13,14);
    }

    private Set<Pit> generatePits() {
        return new PitGenerator()
                .generatePits(new Player("p1"), new Player("p2"));
    }
}
