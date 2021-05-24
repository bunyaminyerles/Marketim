package com.example.marketmanaging.model;

import java.util.UUID;

public class Category {

    private String name;
    private String url;

    public Category() {

    }
    public Category(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return url;
    }

    public void setUri(String url) {
        this.url = url;
    }

}
