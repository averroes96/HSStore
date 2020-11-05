package com.averroes.hsstock.models;

public class Sell {

    private int _id;
    private String _date;
    private int _price;
    private Product product;

    public Sell() {
    }

    public Sell(Product product, String _date, int _price) {
        this.product = product;
        this._date = _date;
        this._price = _price;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_date() {
        return _date;
    }

    public void set_date(String _date) {
        this._date = _date;
    }

    public int get_price() {
        return _price;
    }

    public void set_price(int _price) {
        this._price = _price;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
