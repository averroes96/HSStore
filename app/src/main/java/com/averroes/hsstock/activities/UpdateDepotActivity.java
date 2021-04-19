package com.averroes.hsstock.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import com.averroes.hsstock.inc.Commons;
import com.averroes.hsstock.models.Depot;

public class UpdateDepotActivity extends Commons {

    private ImageButton backBtn,deleteBtn;
    private EditText referenceET,positionET,priceET;
    private Button updateBtn;
    private ConstraintLayout mainLayout;


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
        priceET = findViewById(R.id.priceET);
        updateBtn = findViewById(R.id.updateBtn);
        mainLayout = findViewById(R.id.mainLayout);

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
                if(res) {
                    finish();
                }
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
                    getIntent().getStringExtra("region"),
                    getIntent().getStringExtra("price")
            );

            referenceET.setText(selectedDepot.get_reference());
            positionET.setText(String.valueOf(selectedDepot.get_location()));
            priceET.setText(String.valueOf(selectedDepot.get_price()));

        }
        else{
            showSnackBarMessage(mainLayout, "No data !");
        }
    }

    private boolean updateDepot() {
        String referenceText = referenceET.getText().toString().trim();
        String positionText = positionET.getText().toString().trim();
        String priceText = priceET.getText().toString().trim();

        if(TextUtils.isEmpty(referenceText)){
            showSnackBarMessage(mainLayout, "Entrez la référence du chaussure s'il vous plaît");
            return false;
        }
        if(TextUtils.isEmpty(positionText)){
            showSnackBarMessage(mainLayout, "Entrez la position du chaussure s'il vous plaît");
            return false;
        }
        if(!TextUtils.isEmpty(priceText) && !TextUtils.isDigitsOnly(priceText)){
            showSnackBarMessage(mainLayout, "Entrez le prix du chaussure s'il vous plaît");
            return false;
        }

        Depot depot = new Depot(
                Integer.parseInt(getIntent().getStringExtra("id")),
                referenceText,
                positionText,
                getIntent().getStringExtra("region"),
                priceText
        );

        Toast.makeText(this, R.string.depot_updated, Toast.LENGTH_LONG).show();

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