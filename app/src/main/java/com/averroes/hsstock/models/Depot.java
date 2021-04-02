package com.averroes.hsstock.models;

import androidx.annotation.NonNull;

public class Depot {

    private int _id;
    private String _reference;
    private String _location;
    private String _region;

    public Depot() {
    }

    public Depot(int id, String reference, String location, String region) {
        this._id = id;
        this._reference = reference;
        this._location = location;
        this._region = region;
    }

    public Depot(String _reference, String _location) {
        this._reference = _reference;
        this._location = _location;
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

    @NonNull
    @Override
    public String toString() {
        return _reference + " => " + _location;
    }
}
