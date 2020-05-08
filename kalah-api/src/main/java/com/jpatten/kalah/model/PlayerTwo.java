package com.jpatten.kalah.model;

import javax.persistence.Entity;

@Entity
public class PlayerTwo extends Player {

    public PlayerTwo() {
    }

    public PlayerTwo(String name) {
        super(name);
    }

}
