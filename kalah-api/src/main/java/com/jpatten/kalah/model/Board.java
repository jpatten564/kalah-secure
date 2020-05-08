package com.jpatten.kalah.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "board")
public class Board {

    public static final int PIT_COUNT = 14;

    public static Board newBoardWithPits(Game game) {
        Board board = new Board();
        board.setGame(game);

        Set<Pit> pits = new PitGenerator()
                .generatePits(game.getPlayerOne(), game.getPlayerTwo());

        pits.forEach(board::addPit);
        return board;
    }

    private Long id;
    private Set<Pit> pits = new HashSet<>();
    private Game game;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy="board", cascade={CascadeType.ALL})
    public Set<Pit> getPits() {
        return pits;
    }

    public void setPits(Set<Pit> pits) {
        this.pits = pits;
    }

    public void addPit(Pit pit) {
        pits.add(pit);
        pit.setBoard(this);
    }

    @Transient
    public Set<Pit> getPopulatedPits() {
        return pits.stream()
                .filter(pit -> !pit.isEmpty() && !pit.isHouse())
                .collect(Collectors.toSet());
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "game_id")
    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
