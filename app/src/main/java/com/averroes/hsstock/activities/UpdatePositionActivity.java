package com.averroes.hsstock.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.averroes.hsstock.R;
import com.averroes.hsstock.database.DBHandler;
import com.averroes.hsstock.models.Depot;
import com.averroes.hsstock.models.Position;

import java.util.Arrays;
import java.util.List;

public class UpdatePositionActivity extends AppCompatActivity {

    private ImageButton backBtn,deleteBtn;
    private EditText referencesET,positionET;
    private Button editBtn;

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
                updatePosition();
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

        for(String ref : initialRefs) {
            dbHandler.removeDepot(
                    new Depot(
                            ref,
                            initialPosition
                    )
            );
        }

    }

    private void getIntentData() {
        if(getIntent().hasExtra("name") && getIntent().hasExtra("references")){
            initialRefs = Arrays.asList(getIntent().getStringExtra("references").split(","));
            initialPosition = getIntent().getStringExtra("name");
            referencesET.setText(getIntent().getStringExtra("references"));
            positionET.setText(getIntent().getStringExtra("name"));

            selectedPosition = new Position(
                    getIntent().getStringExtra("name"),
                    getIntent().getStringExtra("references")
            );
        }
        else{
            Toast.makeText(this, "No data !", Toast.LENGTH_LONG).show();
        }
    }

    private void updatePosition() {

        positionText = positionET.getText().toString().trim();
        references = Arrays.asList(referencesET.getText().toString().trim().split(","));

        if(references.isEmpty()){
            Toast.makeText(this, R.string.enter_refs, Toast.LENGTH_LONG).show();
            return;
        }

        deletePosition();

        for(String ref : references) {
            dbHandler.addDepot(
                    new Depot(
                            ref,
                            positionText
                    )
            );
        }

        Toast.makeText(this, R.string.position_updated, Toast.LENGTH_LONG).show();
    }
}