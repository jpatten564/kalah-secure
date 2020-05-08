package com.jpatten.kalah.model.dto;

import com.jpatten.kalah.model.Game;
import com.jpatten.kalah.model.Pit;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class PitStatus {

    public static final Collector<Pit, ?, LinkedHashMap<String, String>> PIT_COLLECTOR = Collectors.toMap(
            pit -> String.valueOf(pit.getIndex()),
            pit -> String.valueOf(pit.getStoneCount()),
            (p1, p2) -> p1,
            LinkedHashMap::new
    );

    public static PitStatus fromGame(Game game) {
        PitStatus pitStatus = new PitStatus();

        pitStatus.pitIndexToPitCountMap = game.getBoard().getPits().stream()
                .sorted(Comparator.comparing(Pit::getIndex))
                .collect(PIT_COLLECTOR);

        return pitStatus;
    }

    private Map<String, String> pitIndexToPitCountMap;

    public Map<String, String> getPitIndexToPitCountMap() {
        return pitIndexToPitCountMap;
    }

    public void setPitIndexToPitCountMap(Map<String, String> pitIndexToPitCountMap) {
        this.pitIndexToPitCountMap = pitIndexToPitCountMap;
    }
}
