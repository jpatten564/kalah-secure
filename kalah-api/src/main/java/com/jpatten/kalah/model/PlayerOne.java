package com.jpatten.kalah.model;

import javax.persistence.Entity;

@Entity
public class PlayerOne extends Player {

    public PlayerOne() {
    }

    public PlayerOne(String name) {
        super(name);
    }
}
