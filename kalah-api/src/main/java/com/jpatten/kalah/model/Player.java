package com.jpatten.kalah.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Player {

    private Long id;
    private Set<Pit> assignedPits = new HashSet<>();
    private String name;
    private Game game;

    public Player() { }

    public Player(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy="player", cascade={CascadeType.ALL})
    public Set<Pit> getAssignedPits() {
        return assignedPits;
    }

    public void setAssignedPits(Set<Pit> assignedPits) {
        this.assignedPits = assignedPits;
    }

    public void addAssignedPit(Pit pit) {
        pit.setPlayer(this);
        assignedPits.add(pit);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="game_id")
    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public boolean allPitsEmpty() {
        return assignedPits.stream()
                .filter(pit -> !pit.isHouse())
                .allMatch(Pit::isEmpty);
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                '}';
    }
}
