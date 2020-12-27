package com.averroes.hsstock.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.averroes.hsstock.adapters.CustomAdapter;
import com.averroes.hsstock.inc.Commons;
import com.averroes.hsstock.interfaces.CameraMethods;
import com.averroes.hsstock.database.DBHandler;
import com.averroes.hsstock.models.Product;
import com.averroes.hsstock.R;
import com.averroes.hsstock.interfaces.StorageMethods;
import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddProductActivity extends Commons implements CameraMethods, StorageMethods {

    private ImageButton back;
    private ConstraintLayout mainLayout;
    private CircularImageView image;
    private EditText reference, colorsAndSizesET;
    private TextView typeTV;
    private Button add;

    private String referenceText,colorText,sizesText;
    private List<String> colorsAndSizes,sizes;

    private Uri imageUri;

    private String[] cameraPerm;
    private String[] storagePerm;

    private DBHandler dbHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        back = findViewById(R.id.backBtn);
        reference = findViewById(R.id.referencesET);
        colorsAndSizesET = findViewById(R.id.colorsAndSizesET);
        add = findViewById(R.id.addBtn);
        image = findViewById(R.id.productImageTV);
        typeTV = findViewById(R.id.typeTV);
        mainLayout = findViewById(R.id.mainLayout);

        cameraPerm = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        storagePerm = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        dbHandler = new DBHandler(this);
        colorsAndSizes = new ArrayList<>();
        sizes = new ArrayList<>();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProduct();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog();
            }
        });

        typeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTypeDialog();
            }
        });

    }

    private void reset() {
        reference.setText("");
        colorsAndSizesET.setText("");
        image.setImageResource(R.drawable.ic_image_grey_48);
    }

    private void openTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.type_dialog_msg))
                .setItems(R.array.types, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String[] array = getResources().getStringArray(R.array.types);
                        typeTV.setText(array[i]);
                    }
                })
                .create().show();
    }

    private void addProduct() {

        referenceText = reference.getText().toString().trim();
        colorsAndSizes = Arrays.asList(colorsAndSizesET.getText().toString().trim().split("\n"));

        if(colorsAndSizes.size() == 0){
                Toast.makeText(this, getString(R.string.enter_colors_sizes), Toast.LENGTH_LONG).show();
                return;
        }

        for (String str : colorsAndSizes) {

            // Init variables
            colorText = "";
            sizesText = "";

            try {
                colorText = str.split(":")[0];
                colorText = colorText.trim();
                sizesText = str.split(":")[1];
                sizesText = sizesText.trim();
                sizes = Arrays.asList(sizesText.split(","));
            } catch (ArrayIndexOutOfBoundsException e){
                showSnackBarMessage(mainLayout, R.string.syntax_error);
                return;
            }
            if(TextUtils.isEmpty(referenceText)){
                showSnackBarMessage(mainLayout, R.string.enter_ref);
                return;
            }
            // Check sizes and colors input
            if(colorText.isEmpty()){
                showSnackBarMessage(mainLayout, R.string.syntax_error);
                return;
            }
            if(sizesText.isEmpty()){
                showSnackBarMessage(mainLayout, R.string.syntax_error);
                return;
            }
            if(sizes.isEmpty()){
                showSnackBarMessage(mainLayout, R.string.enter_sizes);
                return;
            }

            for(String size : sizes){
                if(!TextUtils.isDigitsOnly(size)) {
                    showSnackBarMessage(mainLayout, R.string.invalid_size);
                    return;
                }
            }

            for(String size : sizes) {
                dbHandler.addProduct(
                        new Product(
                                referenceText,
                                colorText.toUpperCase(),
                                Integer.parseInt(size),
                                imageUri == null ? "" : imageUri.toString(),
                                typeTV.getText().toString()
                        ));
            }

        }

            reset();

            showSnackBarMessage(mainLayout, R.string.product_s_added);

    }

    @Override
    public void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY);
    }

    @Override
    public void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPerm, CAMERA_REQUEST);
    }

    @Override
    public void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "temp_image title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "temp_image description");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA);

    }

    @Override
    public void showImagePickDialog() {
        String[] options = { "Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_source)
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i == 0){
                            if(checkCameraPermission()){
                                pickFromCamera();
                            }else{
                                requestCameraPermission();
                            }
                        }else{
                            if(checkStoragePermission()){
                                pickFromGallery();
                            }
                            else{
                                requestStoragePermission();
                            }
                        }
                    }
                })
                .show();
    }

    @Override
    public boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePerm, STORAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case CAMERA_REQUEST : {
                if(grantResults.length > 0){
                    boolean cameraResult = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageResult = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraResult && storageResult){
                        pickFromCamera();
                    }else{
                        Toast.makeText(this, "Camera and storage permission are needed", Toast.LENGTH_LONG).show();
                    }
                }
            } break;
            case STORAGE_REQUEST : {
                if(grantResults.length > 0){
                    boolean locationResult = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(locationResult){
                        pickFromGallery();
                    }else{
                        Toast.makeText(this, "Storage permission is needed", Toast.LENGTH_LONG).show();
                    }
                }
            } break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_CAMERA){
                image.setImageURI(imageUri);
            }
            else if(requestCode == IMAGE_PICK_GALLERY){

                imageUri = data.getData();
                image.setImageURI(imageUri);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean checkViews(){

        if(!reference.getText().toString().trim().isEmpty())
            return false;
        return colorsAndSizesET.getText().toString().trim().isEmpty();
    }

    @Override
    public void onBackPressed() {

        if(checkViews()){
            super.onBackPressed();
        }
        else{
            backDialog();
        }
    }

    private void backDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure));
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(AddProductActivity.this, MainActivity.class));
                finish();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.create().show();
    }
}