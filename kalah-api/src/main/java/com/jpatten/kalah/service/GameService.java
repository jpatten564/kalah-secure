package com.jpatten.kalah.service;

import com.jpatten.kalah.exception.GameNotFoundException;
import com.jpatten.kalah.model.Game;
import com.jpatten.kalah.model.GameStatus;
import com.jpatten.kalah.model.dto.GameDto;
import com.jpatten.kalah.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class GameService {

    private final GameRepository gameRepository;

    private final StoneDistributorService stoneDistributorService;

    @Autowired
    public GameService(GameRepository gameRepository, StoneDistributorService stoneDistributorService) {
        this.gameRepository = gameRepository;
        this.stoneDistributorService = stoneDistributorService;
    }

    public GameDto createGame(Function<Game, String> uriProvider, String player1Username, String player2Username) {

        Game game = gameRepository.save(Game.createGame(player1Username, player2Username));
        String uri = uriProvider.apply(game);
        return GameDto.fromGame(game, uri);
    }

    public GameDto play(Long gameId, Integer pitIndex, Function<Game, String> uriProvider) {

        Game game = findGameById(gameId);

        stoneDistributorService.distributeStonesFromPit(game, pitIndex);

        if (game.isComplete()) {
            game.setGameStatus(GameStatus.FINISHED);
            stoneDistributorService.gatherRemainingStonesIntoHouse(game);
        }
        else {
            game.nextTurn();
        }

        String uri = uriProvider.apply(game);

        return GameDto.fromGame(gameRepository.save(game), uri);
    }

    private Game findGameById(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameNotFoundMessage(gameId)));
    }

    public List<GameDto> findAll(Function<Game, String> uriProvider) {
        return gameRepository.streamAll()
                .map(game -> GameDto.fromGame(game, uriProvider.apply(game)))
                .collect(toList());
    }

    public GameDto findById(Long gameId, Function<Game, String> uriProvider) {

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameNotFoundMessage(gameId)));

        return GameDto.fromGame(game, uriProvider.apply(game));
    }

    private String gameNotFoundMessage(Long gameId) {
        return String.format("Unable to find game with id %s", gameId);
    }

    public boolean isNextPlayer(Long gameId, String username) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameNotFoundMessage(gameId)))
                .getCurrentPlayer().getName().equals(username);
    }
}
