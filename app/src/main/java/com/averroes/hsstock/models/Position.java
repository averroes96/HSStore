package com.averroes.hsstock.models;

public class Position {

    private String _name;
    private String _refs;
    private int _num_refs;

    public Position(String _name, String _refs, int _num_refs) {
        this._name = _name;
        this._refs = _refs;
        this._num_refs = _num_refs;
    }

    public Position() {
        this._name = "";
        this._refs = "";
        this._num_refs = 0;
    }

    public Position(String _name, String _refs) {
        this._name = _name;
        this._refs = _refs;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_refs() {
        return _refs;
    }

    public void set_refs(String _refs) {
        this._refs = _refs;
    }

    public int get_num_refs() {
        return _num_refs;
    }

    public void set_num_refs(int _num_refs) {
        this._num_refs = _num_refs;
    }

    @Override
    public String toString() {
        return "Position{ " +
                " name = '" + _name + '\'' +
                " } ";
    }


}
