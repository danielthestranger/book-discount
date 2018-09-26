package com.greenfoxacademy;

public class Product {
    private String id;
    private Double unitPrice;

    public Product() {
    }

    public Product(String id, Double unitPrice) {
        this();
        this.id = id;
        this.unitPrice = unitPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        Product other = (Product) o;
        return other.getId() == this.id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
