package com.jpatten.kalah.model;

import javax.persistence.*;

@Entity
public class Pit {

    private Long id;
    private Integer index;
    private Integer stoneCount = 0;
    private Boolean isHouse = false;
    private Player player;
    private Board board;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getStoneCount() {
        return stoneCount;
    }

    public void setStoneCount(Integer stoneCount) {
        this.stoneCount = stoneCount;
    }

    public void addStone() {
        stoneCount++;
    }

    public void addStones(int stoneCount) {
        this.stoneCount += stoneCount;
    }

    public void removeStone() {
        stoneCount--;
    }

    public void clearStones() {
        stoneCount = 0;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "playerId")
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "boardId")
    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Boolean isHouse() {
        return isHouse;
    }

    public void setHouse(Boolean house) {
        isHouse = house;
    }

    public boolean belongsToPlayer(Player player) {
        return this.player.getId().equals(player.getId());
    }

    public boolean hasIndex(int index) {
        return this.index == index;
    }

    @Transient
    public boolean isEmpty() {
        return stoneCount == 0;
    }

    public boolean canStonesBeRemoved() {
        return !isHouse && !isEmpty();
    }

    @Override
    public String toString() {
        return "Pit{" +
                "index=" + index +
                ", stoneCount=" + stoneCount +
                ", isHouse=" + isHouse +
                ", player=" + player +
                '}';
    }
}
