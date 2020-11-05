package com.averroes.hsstock.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.averroes.hsstock.R;
import com.averroes.hsstock.database.DBHandler;
import com.averroes.hsstock.models.Depot;
import com.averroes.hsstock.models.Product;
import com.averroes.hsstock.models.Sell;

import java.util.Arrays;
import java.util.List;

public class AddPositionActivity extends AppCompatActivity {
    
    private ImageButton backBtn;
    private EditText referencesET,positionET;
    private Button addBtn;

    private String positionText;
    private List<String> references;

    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_position);
        
        backBtn = findViewById(R.id.backBtn);
        referencesET = findViewById(R.id.referencesET);
        positionET = findViewById(R.id.positionET);
        addBtn = findViewById(R.id.addBtn);

        dbHandler = new DBHandler(this);
        
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPosition();
            }
        });
  }

    private void addPosition() {

        positionText = positionET.getText().toString().trim();
        references = Arrays.asList(referencesET.getText().toString().trim().split(","));

        if(references.isEmpty()){
            Toast.makeText(this, "Entrez les references svp !", Toast.LENGTH_LONG).show();
            return;
        }

        for(String ref : references) {
            dbHandler.addPosition(
                    new Depot(
                            ref,
                            positionText
                    )
            );
        }

    }
}