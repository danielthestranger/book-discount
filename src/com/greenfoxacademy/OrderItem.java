package com.greenfoxacademy;

public class OrderItem {
    private String id;
    private Double pricePerUnit;

    public OrderItem() {
    }

    public OrderItem(String id, Double pricePerUnit) {
        this();
        this.id = id;
        this.pricePerUnit = pricePerUnit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof OrderItem)) {
            return false;
        }
        OrderItem other = (OrderItem) o;
        return other.getId() == this.id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
