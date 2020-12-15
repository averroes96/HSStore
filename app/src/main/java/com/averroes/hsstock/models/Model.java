package com.averroes.hsstock.models;

public class Model {

    private String _name,_colors,_count,_type,_image;

    public Model() {
        this._name = "";
        this._colors = "";
        this._count = "";
        this._type = "";
        this._image = "";
    }

    public Model(String _name) {
        this._name = _name;
        this._colors = "";
        this._count = "";
        this._type = "";
        this._image = "";
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_colors() {
        return _colors;
    }

    public void set_colors(String _colors) {
        this._colors = _colors;
    }

    public String get_count() {
        return _count;
    }

    public void set_count(String _count) {
        this._count = _count;
    }

    public String get_type() {
        return _type;
    }

    public void set_type(String _type) {
        this._type = _type;
    }

    public String get_image() {
        return _image;
    }

    public void set_image(String _image) {
        this._image = _image;
    }
}
