package com.averroes.hsstock.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
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
import com.averroes.hsstock.models.Position;

import java.util.Arrays;
import java.util.List;

public class UpdatePositionActivity extends Commons {

    private ImageButton backBtn,deleteBtn;
    private EditText referencesET,positionET;
    private Button editBtn;
    private ConstraintLayout mainLayout;

    private String positionText,initialPosition;
    private List<String> initialRefs,references;
    private DBHandler dbHandler;
    private Position selectedPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_position);

        backBtn = findViewById(R.id.backBtn);
        referencesET = findViewById(R.id.referencesET);
        positionET = findViewById(R.id.positionET);
        editBtn = findViewById(R.id.editBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        mainLayout = findViewById(R.id.mainLayout);

        dbHandler = new DBHandler(this);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(updatePosition())
                    finish();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog();
            }
        });

        getIntentData();
    }

    private void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.delete) + R.string.two_points_space + selectedPosition.toString());
        builder.setMessage(getString(R.string.delete_confirm_msg) + selectedPosition);
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deletePosition();
                finish();
            }
        });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.create().show();
    }

    private void deletePosition() {

        SharedPreferences sharedPreferences = getSharedPreferences("region_settings", Context.MODE_PRIVATE);
        String regionText = sharedPreferences.contains("selected_region")? sharedPreferences.getString("selected_region", ""): "CENTRE";

        for(String ref : initialRefs) {
            if(ref.trim().endsWith(":")) {
                dbHandler.removeDepot(
                        new Depot(
                                ref,
                                initialPosition,
                                regionText
                        )
                );
            }
            else{
                String realRef = ref.split(":")[0].trim();
                dbHandler.removeDepot(
                        new Depot(
                                realRef,
                                initialPosition,
                                regionText
                        )
                );
            }
        }

    }

    private void getIntentData() {
        if(getIntent().hasExtra("name") && getIntent().hasExtra("references")){
            initialRefs = Arrays.asList(getIntent().getStringExtra("references").split("\n"));
            initialPosition = getIntent().getStringExtra("name");
            referencesET.setText(getIntent().getStringExtra("references"));
            positionET.setText(getIntent().getStringExtra("name"));

            selectedPosition = new Position(
                    getIntent().getStringExtra("name"),
                    getIntent().getStringExtra("references")
            );
        }
        else{
            showSnackBarMessage(mainLayout, "No data!");
        }
    }

    private boolean updatePosition() {

        positionText = positionET.getText().toString().trim();
        references = Arrays.asList(referencesET.getText().toString().trim().split("\n"));
        SharedPreferences sharedPreferences = getSharedPreferences("region_settings", Context.MODE_PRIVATE);
        String regionText = sharedPreferences.contains("selected_region")? sharedPreferences.getString("selected_region", ""): "CENTRE";

        if(references.isEmpty()){
            showSnackBarMessage(mainLayout, R.string.enter_refs);
            return false;
        }

        for (String ref : references){
            String[] refAndPrice = ref.split(":");
            if (refAndPrice.length == 2) {
                String price = refAndPrice[1].trim();
                if(!TextUtils.isDigitsOnly(price)){
                    showSnackBarMessage(mainLayout, "Les prix doivent être composés uniquement de chiffres!");
                    return false;
                }
            }
            else if (refAndPrice.length > 2){
                showSnackBarMessage(mainLayout, "Erreur de syntaxe!");
                return false;
            }
        }

        deletePosition();

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
                    String reference = refAndPrice[0].trim();
                    String price = refAndPrice[1].trim();
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

        Toast.makeText(this, R.string.position_updated, Toast.LENGTH_LONG).show();

        return true;
    }
}