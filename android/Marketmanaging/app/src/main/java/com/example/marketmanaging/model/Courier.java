package com.example.marketmanaging.model;

import java.io.Serializable;

public class Courier implements Serializable {

    private String name;

    public Courier(String name) {
        this.name = name;
    }

    public Courier() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

