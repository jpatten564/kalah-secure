package com.jpatten.kalah.service;

import com.jpatten.kalah.exception.IllegalMoveException;
import com.jpatten.kalah.model.Game;
import com.jpatten.kalah.model.Pit;
import com.jpatten.kalah.model.PlayResultAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StoneDistributorService {

    private final PitResolverService pitResolverService;

    @Autowired
    public StoneDistributorService(PitResolverService pitResolverService) {
        this.pitResolverService = pitResolverService;
    }

    void distributeStonesFromPit(Game game, Integer pitIndex) {

        Pit sourcePit = pitResolverService.getPitBelongingToCurrentPlayer(pitIndex, game);

        checkStonesCanBeDistributed(sourcePit);

        Pit targetPit = pitResolverService.nextStoneRecipientPit(sourcePit, game);

        while (!sourcePit.isEmpty()) {

            distributeStone(game, sourcePit, targetPit);

            if (sourcePit.isEmpty()) {
                performPlayResultAction(game, targetPit);
                return;
            }

            targetPit = pitResolverService.nextStoneRecipientPit(targetPit, game);
        }
    }

    private void performPlayResultAction(Game game, Pit targetPit) {
        PlayResultAction playResultAction = PlayResultAction.fromLastReceivingPit(game, targetPit);

        if (playResultAction == PlayResultAction.COLLECT) {
            Pit house = pitResolverService.getHouse(game.getCurrentPlayer(), game);
            targetPit.removeStone();
            house.addStone();
            gatherOpposingPitStonesIntoHouse(game, targetPit);
        }
        else if (playResultAction == PlayResultAction.ANOTHER_TURN) {
            // TODO: we are toggling the player here just so that when we togglePlayer again, the player remains the same... this needs to be revisited
            game.nextTurn();
        }
    }

    private void checkStonesCanBeDistributed(Pit sourcePit) {
        if (!sourcePit.canStonesBeRemoved()) {
            throw new IllegalMoveException("Cannot remove stones from pit");
        }
    }

    private void distributeStone(Game game, Pit sourcePit, Pit targetPit) {

        sourcePit.removeStone();
        targetPit.addStone();
    }

    private void gatherOpposingPitStonesIntoHouse(Game game, Pit sourcePit) {
        Pit house = pitResolverService.getHouse(game.getCurrentPlayer(), game);

        Pit opposingPit = pitResolverService.getOpposingPit(sourcePit, game);

        house.addStones(opposingPit.getStoneCount());
        opposingPit.clearStones();
    }

    public void gatherRemainingStonesIntoHouse(Game game) {
        game.getBoard().getPopulatedPits()
                .forEach(pit -> {
                    Pit house = pitResolverService.getHouse(pit.getPlayer(), game);
                    house.addStones(pit.getStoneCount());
                    pit.clearStones();
                });
    }

}
