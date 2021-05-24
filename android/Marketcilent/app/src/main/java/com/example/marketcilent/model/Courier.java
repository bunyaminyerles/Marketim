package com.example.marketcilent.model;

import java.io.Serializable;


public class Courier implements Serializable {

    private String name;
    private String email;

    public Courier(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Courier() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
