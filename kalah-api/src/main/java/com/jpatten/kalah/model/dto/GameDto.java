package com.jpatten.kalah.model.dto;

import com.jpatten.kalah.model.Game;
import com.jpatten.kalah.model.GameStatus;

import java.util.HashMap;
import java.util.Map;

public class GameDto {

    private String id;
    private String uri;
    private GameStatus gameStatus;
    private PitStatus pitStatus;

    public static GameDto fromGame(Game game, String uri) {

        PitStatus pitStatus = PitStatus.fromGame(game);

        return new GameDto(game.getId(), game.getGameStatus(), uri, pitStatus);
    }

    public GameDto() { }

    public GameDto(Long id, GameStatus gameStatus, String uri, PitStatus pitStatus) {
        this.id = String.valueOf(id);
        this.uri = uri;
        this.gameStatus = gameStatus;
        this.pitStatus = pitStatus;
    }

    public String getId() {
        return id;
    }

    public String getUri() {
        return uri;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public Map<String,String> getStatus() {
        return pitStatus != null ? pitStatus.getPitIndexToPitCountMap() : new HashMap<>();
    }

}
