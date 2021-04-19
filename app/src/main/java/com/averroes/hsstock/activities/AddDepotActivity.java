package com.averroes.hsstock.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.averroes.hsstock.R;
import com.averroes.hsstock.database.DBHandler;
import com.averroes.hsstock.inc.Commons;
import com.averroes.hsstock.models.Depot;

import java.util.Arrays;
import java.util.List;

public class AddDepotActivity extends Commons {

    private ImageButton backBtn;
    private EditText referenceET,positionET,priceET;
    private Button addBtn;
    private ConstraintLayout mainLayout;

    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_depot);

        backBtn = findViewById(R.id.backBtn);
        referenceET = findViewById(R.id.referenceET);
        positionET = findViewById(R.id.positionET);
        priceET = findViewById(R.id.priceET);
        addBtn = findViewById(R.id.addBtn);
        mainLayout = findViewById(R.id.mainLayout);

        dbHandler = new DBHandler(this);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDepot();
            }
        });
    }

    private void addDepot() {

        SharedPreferences sharedPreferences = getSharedPreferences("region_settings", Context.MODE_PRIVATE);

        String positionText = positionET.getText().toString().trim();
        String referenceText = referenceET.getText().toString().trim();
        String priceText = priceET.getText().toString().trim();
        String regionText = sharedPreferences.contains("selected_region")? sharedPreferences.getString("selected_region", ""): "CENTRE";

        if(referenceText.isEmpty()){
            showSnackBarMessage(mainLayout, getString(R.string.enter_ref));
            return;
        }

        if(positionText.isEmpty()){
            showSnackBarMessage(mainLayout, R.string.enter_ref);
            return;
        }

        if(referenceText.contains(",")){
            showSnackBarMessage(mainLayout, R.string.invalid_ref);
            return;
        }

        if(!TextUtils.isEmpty(priceText) && !TextUtils.isDigitsOnly(priceText)){
            showSnackBarMessage(mainLayout, "Entrez un prix du chaussure valide s'il vous plaît");
            return;
        }

        dbHandler.addDepot(
            new Depot(
                    referenceText,
                    positionText,
                    regionText,
                    priceText
            )
        );


        showSnackBarMessage(mainLayout, "Positions ajouté(s)");
        referenceET.setText("");
        positionET.setText("");
    }
}