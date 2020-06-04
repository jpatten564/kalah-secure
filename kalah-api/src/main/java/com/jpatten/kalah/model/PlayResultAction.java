package com.jpatten.kalah.model;

/**
 * An action to be performed following the end of a "play", when the last stone has been placed by the player
 */
public enum PlayResultAction {

    NONE, COLLECT, ANOTHER_TURN;

    public static PlayResultAction fromLastReceivingPit(Game game, Pit lastStoneTargetPit) {

        if (!lastStoneTargetPit.isHouse() && lastStoneTargetPit.belongsToPlayer(game.getCurrentPlayer()) &&
                lastStoneTargetPit.getStoneCount() == 1) {

            return PlayResultAction.COLLECT;
        }

        if (lastStoneTargetPit.isHouse() && lastStoneTargetPit.belongsToPlayer(game.getCurrentPlayer())) {
            return PlayResultAction.ANOTHER_TURN;
        }

        return NONE;
    }
}
