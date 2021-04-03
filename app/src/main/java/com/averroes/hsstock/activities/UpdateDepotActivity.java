package com.averroes.hsstock.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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

public class UpdateDepotActivity extends AppCompatActivity {

    private ImageButton backBtn,deleteBtn;
    private EditText referenceET,positionET;
    private Button updateBtn;

    private String referenceText,positionText;
    private Depot selectedDepot;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_depot);

        backBtn = findViewById(R.id.backBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        referenceET = findViewById(R.id.referenceET);
        positionET = findViewById(R.id.positionET);
        updateBtn = findViewById(R.id.updateBtn);

        dbHandler = new DBHandler(this);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean res = updateDepot();
                if(res)
                    finish();
            }
        });

        getIntentData();
    }

    private void getIntentData() {
        if(getIntent().hasExtra("id") && getIntent().hasExtra("reference") && getIntent().hasExtra("position")){

            selectedDepot = new Depot(
                    Integer.parseInt(getIntent().getStringExtra("id")),
                    getIntent().getStringExtra("reference"),
                    getIntent().getStringExtra("position"),
                    ""
            );

            referenceET.setText(selectedDepot.get_reference());
            positionET.setText(String.valueOf(selectedDepot.get_location()));

        }
        else{
            Toast.makeText(this, "No data !", Toast.LENGTH_LONG).show();
        }
    }

    private boolean updateDepot() {
        referenceText = referenceET.getText().toString().trim();
        positionText = positionET.getText().toString().trim();

        if(TextUtils.isEmpty(referenceText)){
            Toast.makeText(this, "Entrez la référence du chaussure s'il vous plaît", Toast.LENGTH_LONG).show();
            return false;
        }
        if(TextUtils.isEmpty(positionText)){
            Toast.makeText(this, "Entrez la position du chaussure s'il vous plaît", Toast.LENGTH_LONG).show();
            return false;
        }

        Depot depot = new Depot(
                Integer.parseInt(getIntent().getStringExtra("id")),
                referenceText,
                positionText,
                getIntent().getStringExtra("region")
        );

        return dbHandler.updateDepot(depot);
    }

    private void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Supprimer: " + selectedDepot.toString());
        builder.setMessage("Etes-vous sûr que vous voulez supprimer " + selectedDepot.get_reference() + " de position " + selectedDepot.get_location());
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteDepot();
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

    private void deleteDepot() {
        dbHandler.deleteDepot(selectedDepot);
    }
}