package com.averroes.hsstock.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
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
import com.averroes.hsstock.models.Depot;
import com.averroes.hsstock.models.Product;
import com.averroes.hsstock.models.Sell;

import java.util.Arrays;
import java.util.List;

public class AddPositionActivity extends AppCompatActivity {
    
    private ImageButton backBtn;
    private EditText referencesET,positionET;
    private Button addBtn;

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
        
        backBtn.setOnClickListener(new View.OnClickListener() {
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

        SharedPreferences sharedPreferences = getSharedPreferences("region_settings", Context.MODE_PRIVATE);

        String positionText = positionET.getText().toString().trim();
        List<String> references = Arrays.asList(referencesET.getText().toString().trim().split("\n"));
        String regionText = sharedPreferences.contains("selected_region")? sharedPreferences.getString("selected_region", ""): "Centre";

        if(references.isEmpty()){
            Toast.makeText(this, "Entrez les references svp !", Toast.LENGTH_LONG).show();
            return;
        }

        for (String ref : references){
            String[] refAndPrice = ref.split(":");
            if (refAndPrice.length == 2) {
                String price = refAndPrice[1];
                if(!TextUtils.isDigitsOnly(price)){
                    Toast.makeText(this, "Les prix doivent être composés uniquement de chiffres!", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            else {
                Toast.makeText(this, "Erreur de syntaxe!", Toast.LENGTH_LONG).show();
                return;
            }
        }

        for(String ref : references) {
            if (!ref.trim().isEmpty()) {
                String[] refAndPrice = ref.split(":");

                if (refAndPrice.length == 1)
                    dbHandler.addDepot(
                            new Depot(
                                    ref,
                                    positionText,
                                    regionText
                            )
                    );
                if (refAndPrice.length == 2) {
                    String reference = refAndPrice[0];
                    String price = refAndPrice[1];
                    if(TextUtils.isDigitsOnly(price))
                        dbHandler.addDepot(
                                new Depot(
                                        reference,
                                        positionText,
                                        regionText,
                                        price
                                )
                        );
                }
            }
        }

        Toast.makeText(this, "Positions ajouté(s)", Toast.LENGTH_LONG).show();

    }
}