package com.jpatten.kalah.model;

import java.util.Set;

public class PitTest {

    public void correct_opposing_pit_returned() {
        Set<Pit> pits = new PitGenerator().generatePits(new Player("p1"), new Player("p2"));
    }

}
