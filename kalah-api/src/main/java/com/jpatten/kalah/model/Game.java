package com.jpatten.kalah.model;

import javax.persistence.*;

import static com.jpatten.kalah.model.GameStatus.*;
import static com.jpatten.kalah.model.GameStatus.PLAYER_ONE_TURN;

@Entity
public class Game {

    private Long id;
    private Board board;

    private PlayerOne playerOne;
    private PlayerTwo playerTwo;

    private GameStatus gameStatus = PLAYER_ONE_TURN;

    private Game() { }

    public static Game createGame(String player1Username, String player2Username) {
        Game game = new Game();

        game.setPlayerOne(new PlayerOne(player1Username));
        game.getPlayerOne().setGame(game);

        game.setPlayerTwo(new PlayerTwo(player2Username));
        game.getPlayerTwo().setGame(game);

        game.setBoard(Board.newBoardWithPits(game));
        game.getBoard().setGame(game);
        return game;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToOne(mappedBy="game", cascade=CascadeType.ALL)
    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    @OneToOne(mappedBy="game", cascade=CascadeType.ALL)
    public PlayerOne getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(PlayerOne playerOne) {
        this.playerOne = playerOne;
    }

    @OneToOne(mappedBy="game", cascade=CascadeType.ALL)
    public PlayerTwo getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(PlayerTwo playerTwo) {
        this.playerTwo = playerTwo;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    @Transient
    public Player getCurrentPlayer() {
        return gameStatus == PLAYER_ONE_TURN ? playerOne : playerTwo;
    }

    public void nextTurn() {
        setGameStatus(gameStatus.togglePlayer());
    }

    @Transient
    public boolean isComplete() {
        return playerOne.allPitsEmpty() || playerTwo.allPitsEmpty();
    }
}
