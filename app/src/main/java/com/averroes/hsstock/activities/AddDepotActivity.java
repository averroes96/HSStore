package com.averroes.hsstock.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.averroes.hsstock.R;
import com.averroes.hsstock.database.DBHandler;
import com.averroes.hsstock.models.Depot;

import java.util.Arrays;
import java.util.List;

public class AddDepotActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private EditText referenceET,positionET;
    private Button addBtn;

    private String positionText;
    private String referenceText;

    private DBHandler dbHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_depot);

        backBtn = findViewById(R.id.backBtn);
        referenceET = findViewById(R.id.referenceET);
        positionET = findViewById(R.id.positionET);
        addBtn = findViewById(R.id.addBtn);

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
        positionText = positionET.getText().toString().trim();
        referenceText = referenceET.getText().toString().trim();

        if(referenceText.isEmpty()){
            Toast.makeText(this, getString(R.string.enter_ref), Toast.LENGTH_LONG).show();
            return;
        }

        if(positionText.isEmpty()){
            Toast.makeText(this, getString(R.string.enter_position), Toast.LENGTH_LONG).show();
            return;
        }

        if(referenceText.contains(",")){
            Toast.makeText(this, getString(R.string.invalid_ref), Toast.LENGTH_LONG).show();
            return;
        }

        dbHandler.addDepot(
            new Depot(
                    referenceText,
                    positionText
            )
        );


        Toast.makeText(this, "Positions ajouté(s)", Toast.LENGTH_LONG).show();
    }
}