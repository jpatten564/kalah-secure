package com.jpatten.kalah.controller;

import com.jpatten.kalah.exception.GameNotFoundException;
import com.jpatten.kalah.exception.IllegalMoveException;
import com.jpatten.kalah.model.Game;
import com.jpatten.kalah.model.dto.GameDto;
import com.jpatten.kalah.security.IsNextPlayer;
import com.jpatten.kalah.service.GameService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.function.Function;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(value = "/games", produces = "application/json")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @ApiOperation(value = "Create a new game", response = Game.class,
            produces = "application/json", consumes="application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Game Successfully Created")
    })
    @PostMapping
    public ResponseEntity<GameDto> createGame(Principal principal, HttpServletRequest request) {
        // TODO: the game creator currently plays both sides
        final GameDto gameDto = this.gameService.createGame(getUriProvider(request), principal.getName(), principal.getName());
        return ResponseEntity.status(CREATED).body(gameDto);
    }


    @ApiOperation(value = "Play / make a move", response = Game.class,
            produces = "application/json", consumes="application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Move Successfully Completed"),
            @ApiResponse(code = 403, message = "Illegal Move"),
            @ApiResponse(code = 404, message = "Game Not Found")
    })
    @IsNextPlayer
    @PostMapping(path="/{gameId}/pits/{pitIndex}")
    public ResponseEntity<GameDto> play(@PathVariable Long gameId, @PathVariable Integer pitIndex, HttpServletRequest request) {

        try {
            final GameDto gameDto = gameService.play(gameId, pitIndex, getUriProvider(request));
            return ResponseEntity.ok().body(gameDto);
        }
        catch (IllegalMoveException ime) {
            throw new ResponseStatusException(FORBIDDEN, "Illegal Move", ime);
        }
        catch (GameNotFoundException gnfe) {
            throw new ResponseStatusException(NOT_FOUND, "Game Not Found", gnfe);
        }
    }

    @ApiOperation(value = "Get all games", response = Game.class,
            produces = "application/json", consumes="application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully listed games")
    })
    @GetMapping
    public ResponseEntity<List<GameDto>> getAll(HttpServletRequest request) {
        List<GameDto> games = gameService.findAll(getUriProvider(request));
        return ResponseEntity.ok().body(games);
    }

    @ApiOperation(value = "Get a game", response = Game.class,
            produces = "application/json", consumes="application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Game found"),
            @ApiResponse(code = 404, message = "Game Not Found")
    })
    @GetMapping(path = "/{gameId}")
    public ResponseEntity<GameDto> get(@PathVariable Long gameId, HttpServletRequest request) {
        try {
            GameDto gameDto = gameService.findById(gameId, getUriProvider(request));
            return ResponseEntity.ok().body(gameDto);
        }
        catch (GameNotFoundException gnfe) {
            throw new ResponseStatusException(NOT_FOUND, "Game Not Found", gnfe);
        }
    }

    private Function<Game, String> getUriProvider(HttpServletRequest request) {
        return game -> String.format("%s://%s:%s/games/%s",
                request.getScheme(),
                request.getServerName(),
                request.getServerPort(),
                game.getId());
    }
}
