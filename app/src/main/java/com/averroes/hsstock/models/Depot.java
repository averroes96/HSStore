package com.averroes.hsstock.models;

import androidx.annotation.NonNull;

public class Depot {

    private int _id;
    private String _reference;
    private String _location;
    private String _region;
    private String _price;

    public Depot() {
        _id = 0;
        _reference = "";
        _location = "";
        _region = "";
        _price = "";
    }

    public Depot(int id, String reference, String location, String region) {
        this._id = id;
        this._reference = reference;
        this._location = location;
        this._region = region;
    }

    public Depot(String _reference, String _location, String _region) {
        this._reference = _reference;
        this._location = _location;
        this._region = _region;
    }

    public Depot(String _reference, String _location, String _region, String _price) {
        this._reference = _reference;
        this._location = _location;
        this._region = _region;
        this._price = _price;
    }
    public Depot(int _id, String _reference, String _location, String _region, String _price) {
        this._id = _id;
        this._reference = _reference;
        this._location = _location;
        this._region = _region;
        this._price = _price;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_reference() {
        return _reference;
    }

    public void set_reference(String _reference) {
        this._reference = _reference;
    }

    public String get_location() {
        return _location;
    }

    public void set_location(String _location) {
        this._location = _location;
    }

    public String get_region() {
        return _region;
    }

    public void set_region(String _region) {
        this._region = _region;
    }

    public String get_price() {
        return _price;
    }

    public void set_price(String _price) {
        this._price = _price;
    }

    @NonNull
    @Override
    public String toString() {
        return _reference + " => " + _location;
    }
}
