package com.averroes.hsstock.models;

public class Product {

    private int _id;
    private String _name;
    private String _color;
    private int _size;
    private int _sold;
    private String _image;

    public Product() {
        this._sold = 0;
        this._image = "";
    }

    public Product(String _name, String _color, int _size, String _image) {
        this._name = _name;
        this._color = _color;
        this._size = _size;
        this._sold = 0;
        this._image = _image;
    }

    public Product(String _name, String _color, int _size) {
        this._name = _name;
        this._color = _color;
        this._size = _size;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_color() {
        return _color;
    }

    public void set_color(String _color) {
        this._color = _color;
    }

    public int get_size() {
        return _size;
    }

    public void set_size(int _size) {
        this._size = _size;
    }

    public int get_sold() {
        return _sold;
    }

    public void set_sold(int _sold) {
        this._sold = _sold;
    }

    public String get_image() {
        return _image;
    }

    public void set_image(String _image) {
        this._image = _image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return _size == product._size &&
                _name.equals(product._name) &&
                _color.equals(product._color);
    }


}
