package com.averroes.hsstock.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.averroes.hsstock.inc.Commons;
import com.averroes.hsstock.interfaces.CameraMethods;
import com.averroes.hsstock.database.DBHandler;
import com.averroes.hsstock.models.Product;
import com.averroes.hsstock.R;
import com.averroes.hsstock.interfaces.StorageMethods;

public class UpdateProductActivity extends Commons implements CameraMethods, StorageMethods {

    private ImageButton back,delete;
    private ImageView image;
    private EditText reference, size, color;
    private TextView typeTV;
    private ConstraintLayout mainLayout;
    private Button update;

    private Uri imageUri;

    private String[] cameraPerm;
    private String[] storagePerm;

    private String referenceText,colorText, sizeNumber;

    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);

        back = findViewById(R.id.backBtn);
        reference = findViewById(R.id.referencesET);
        size = findViewById(R.id.sizeET);
        color = findViewById(R.id.colorET);
        update = findViewById(R.id.updateBtn);
        delete = findViewById(R.id.deleteBtn);
        image = findViewById(R.id.productImageIV);
        typeTV = findViewById(R.id.typeTV);
        mainLayout = findViewById(R.id.mainLayout);

        cameraPerm = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        storagePerm = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        dbHandler = new DBHandler(this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean res = updateProduct();
                if(res)
                    finish();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog();
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

        getIntentData();
    }

    private void openTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.type_dialog_msg))
                .setItems(R.array.types, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String[] array = getResources().getStringArray(R.array.types);
                        typeTV.setText(array[i]);
                        typeTV.setTextColor(getResources().getColor(R.color.colorBlack));
                    }
                })
                .create().show();
    }

    private void confirmDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Supprimer " + getIntent().getStringExtra("ref"));
        builder.setMessage("Etes-vous sûr que vous voulez supprimer " + getIntent().getStringExtra("ref") + "?");
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteProduct();
                finish();
            }
        });
        builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.create().show();
    }

    private void deleteProduct() {
        dbHandler.deleteProductByID(getIntent().getStringExtra("id"));
    }

    public void getIntentData(){

        if(getIntent().hasExtra("ref") && getIntent().hasExtra("size") && getIntent().hasExtra("color") && getIntent().hasExtra("id") && getIntent().hasExtra("image")){

            reference.setText(getIntent().getStringExtra("ref"));
            size.setText(getIntent().getStringExtra("size"));
            color.setText(getIntent().getStringExtra("color"));
            typeTV.setText(getIntent().getStringExtra("type"));
            if(!getIntent().getStringExtra("image").equals("")) {
                imageUri = Uri.parse(getIntent().getStringExtra("image"));
                image.setImageURI(imageUri);
            }
        }
        else{
            Toast.makeText(this, "No data !", Toast.LENGTH_LONG).show();
        }

    }

    private boolean updateProduct() {

        referenceText = reference.getText().toString().trim();
        colorText = color.getText().toString().trim();
        sizeNumber = size.getText().toString().trim();

        if(TextUtils.isEmpty(referenceText)){
            showSnackBarMessage(mainLayout, R.string.enter_ref);
            return false;
        }
        if(TextUtils.isEmpty(colorText)){
            showSnackBarMessage(mainLayout, R.string.enter_color);
            return false;
        }
        if(TextUtils.isEmpty(sizeNumber)){
            showSnackBarMessage(mainLayout, R.string.enter_size);
            return false;
        }
        if(!TextUtils.isDigitsOnly(sizeNumber) || isValidSize(sizeNumber)){
            showSnackBarMessage(mainLayout, R.string.enter_valid_size);
            return false;
        }

        Product product = new Product(
                referenceText,
                colorText,
                Integer.parseInt(sizeNumber),
                imageUri == null? "" : imageUri.toString(),
                typeTV.getText().toString()
        );
        product.set_id(Integer.parseInt(getIntent().getStringExtra("id")));
        dbHandler.updateProduct(product);

        return true;
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
        builder.setTitle("Pick image")
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
                        showSnackBarMessage(mainLayout, R.string.camera_storage_perms_needed);
                    }
                }
            } break;
            case STORAGE_REQUEST : {
                if(grantResults.length > 0){
                    boolean locationResult = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(locationResult){
                        pickFromGallery();
                    }else{
                        showSnackBarMessage(mainLayout, R.string.storage_perm_needed);
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
}