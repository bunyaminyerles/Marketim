package com.example.marketcilent.model;

public class Order {
    private Basket basket = new Basket();
    String state;

    public Order() {
    }

    public Order(Basket basket, String state) {
        this.basket = basket;
        this.state = state;
    }

    public Basket getBasket() {
        return basket;
    }

    public void setBasket(Basket basket) {
        this.basket = basket;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
