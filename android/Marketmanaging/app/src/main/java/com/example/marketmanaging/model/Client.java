package com.example.marketmanaging.model;

import java.io.Serializable;

public class Client implements Serializable {

    private String name,email,adress;

    public Client(String name, String email, String adress) {
        this.name = name;
        this.email = email;
        this.adress = adress;
    }

    public Client() {
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

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }
}
