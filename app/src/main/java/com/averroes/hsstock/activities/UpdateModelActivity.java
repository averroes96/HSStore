package com.averroes.hsstock.activities;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.averroes.hsstock.R;
import com.averroes.hsstock.database.DBHandler;
import com.averroes.hsstock.inc.Commons;
import com.averroes.hsstock.interfaces.CameraMethods;
import com.averroes.hsstock.interfaces.StorageMethods;
import com.averroes.hsstock.models.Product;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UpdateModelActivity extends Commons implements CameraMethods, StorageMethods {

    private EditText referenceET, colorsAndSizesET;
    private TextView typeTV;
    private ImageView imageIV;
    private CoordinatorLayout mainLayout;

    private String reference,referenceText,typeText,imageText,colorText,sizesText;
    private String[] colorsText;
    private List<String> colorsAndSizes,sizes;
    private Button saveBtn;

    private Uri imageUri;

    private String[] cameraPerm;
    private String[] storagePerm;

    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_model);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle("");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        referenceET = findViewById(R.id.referencesET);
        colorsAndSizesET = findViewById(R.id.colorsAndSizesET);
        typeTV = findViewById(R.id.typeTV);
        imageIV = findViewById(R.id.imageIV);
        saveBtn = findViewById(R.id.saveBtn);
        mainLayout = findViewById(R.id.mainLayout);

        cameraPerm = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        storagePerm = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        dbHandler = new DBHandler(this);
        colorsAndSizes = new ArrayList<>();
        sizes = new ArrayList<>();

        typeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTypeDialog(typeTV);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog();
            }
        });

        init();
    }

    private void init() {

        if(getIntent().hasExtra("name") && getIntent().hasExtra("type") && getIntent().hasExtra("image") && getIntent().hasExtra("colors")){

            referenceText = getIntent().getStringExtra("name");
            typeText = getIntent().getStringExtra("type");
            imageText = getIntent().getStringExtra("image");
            colorsText = getIntent().getStringArrayExtra("colors");

            referenceET.setText(referenceText);
            typeTV.setTextColor(getResources().getColor(R.color.colorBlack));
            typeTV.setText(typeText);

            for(String color : colorsText){
                if(!color.trim().isEmpty()) {
                    String temp = color.trim() + ": " + dbHandler.getColorSizesByName(referenceText, color.trim());
                    colorsAndSizesET.append(temp + "\n");
                }
            }

            try{
                Picasso.get().load(imageText).placeholder(R.drawable.ic_image_grey_48).into(imageIV);
            }catch(Exception e){
                imageIV.setImageResource(R.drawable.ic_image_grey_48);
            }

        }
    }

    private void save() {

        reference = referenceET.getText().toString().trim();
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
                if(!TextUtils.isDigitsOnly(size) || !isValidSize(size)) {
                    showSnackBarMessage(mainLayout, R.string.invalid_size);
                    return;
                }
            }

        }

        dbHandler.deleteProductByNameAndType(referenceText, typeText);

        for (String str : colorsAndSizes) {

            // Init variables
            colorText = "";
            sizesText = "";

            colorText = str.split(":")[0];
            colorText = colorText.trim();
            sizesText = str.split(":")[1];
            sizesText = sizesText.trim();
            sizes = Arrays.asList(sizesText.split(","));

            for(String size : sizes) {
                dbHandler.addProduct(
                        new Product(
                                referenceText,
                                colorText.toUpperCase(),
                                Integer.parseInt(size),
                                imageUri == null ? imageText : imageUri.toString(),
                                typeTV.getText().toString()
                        ));
            }

        }

        Toast.makeText(UpdateModelActivity.this, R.string.model_updated, Toast.LENGTH_SHORT).show();

        finish();
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
                imageIV.setImageURI(imageUri);
            }
            else if(requestCode == IMAGE_PICK_GALLERY){

                imageUri = data.getData();
                imageIV.setImageURI(imageUri);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}