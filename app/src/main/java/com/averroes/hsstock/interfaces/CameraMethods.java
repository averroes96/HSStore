package com.averroes.hsstock.interfaces;

public interface CameraMethods {

    static final int CAMERA_REQUEST = 200;

    static final int IMAGE_PICK_GALLERY = 400;
    static final int IMAGE_PICK_CAMERA = 500;

    abstract void pickFromGallery();

    abstract void requestCameraPermission();

    abstract void pickFromCamera();

    abstract void showImagePickDialog();

    abstract boolean checkCameraPermission();
}
