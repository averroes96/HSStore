package com.averroes.hsstock.interfaces;

public interface StorageMethods {

    static final int STORAGE_REQUEST = 300;

    boolean checkStoragePermission();

    void requestStoragePermission();

}
