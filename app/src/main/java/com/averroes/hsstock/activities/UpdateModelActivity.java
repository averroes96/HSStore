package com.averroes.hsstock.activities;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;

import com.averroes.hsstock.R;
import com.averroes.hsstock.database.DBHandler;
import com.averroes.hsstock.inc.Commons;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class UpdateModelActivity extends Commons {

    private EditText referenceET, colorsAndSizesET;
    private TextView typeTV;

    private String referenceText,typeText,imageText;
    private String[] colorsText;
    private List<String> colorsAndSizes,sizes;

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
        toolBarLayout.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        referenceET = findViewById(R.id.referencesET);
        colorsAndSizesET = findViewById(R.id.colorsAndSizesET);
        typeTV = findViewById(R.id.typeTV);

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

        init();
    }

    private void init() {

        if(getIntent().hasExtra("name") && getIntent().hasExtra("type") && getIntent().hasExtra("image") && getIntent().hasExtra("colors")){

            referenceText = getIntent().getStringExtra("name");
            typeText = getIntent().getStringExtra("type");
            imageText = getIntent().getStringExtra("image");
            colorsText = getIntent().getStringArrayExtra("colors");

            referenceET.setText(referenceText);
            typeTV.setTextColor( getResources().getColor(R.color.colorBlack));
            typeTV.setText(typeText);

            for(String color : colorsText){
                if(!color.isEmpty()) {
                    String temp = color + ": " + dbHandler.getColorSizesByName(referenceText, color);
                    colorsAndSizesET.append(temp + "\n");
                }
            }

        }
    }

    private void save() {
    }
}