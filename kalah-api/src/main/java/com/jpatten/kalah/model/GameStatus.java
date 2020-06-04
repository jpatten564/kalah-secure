package com.jpatten.kalah.model;

public enum GameStatus {

    PLAYER_ONE_TURN,
    PLAYER_TWO_TURN,
    FINISHED;

    public GameStatus togglePlayer() {
        return this == PLAYER_ONE_TURN ? PLAYER_TWO_TURN : PLAYER_ONE_TURN;
    }

}
