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

    public static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage)
            throws IOException {
        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(img, selectedImage);
        return img;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
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
        builder.setTitle("choisissez une image")
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
}