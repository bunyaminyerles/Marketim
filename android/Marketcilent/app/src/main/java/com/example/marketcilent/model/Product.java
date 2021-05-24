package com.example.marketcilent.model;

public class Product {


    private String categoryUuid;
    private String productName;
    private String price;
    private String unitDetail;
    private String uri;


    public Product() {
    }

    public Product(String categoryUuid, String productName, String price, String unitDetail, String uri) {
        this.categoryUuid = categoryUuid;
        this.productName = productName;
        this.price = price;
        this.unitDetail = unitDetail;
        this.uri = uri;
    }

    public String getCategoryUuid() {
        return categoryUuid;
    }

    public void setCategoryUuid(String categoryUuid) {
        this.categoryUuid = categoryUuid;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUnitDetail() {
        return unitDetail;
    }

    public void setUnitDetail(String unitDetail) {
        this.unitDetail = unitDetail;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
