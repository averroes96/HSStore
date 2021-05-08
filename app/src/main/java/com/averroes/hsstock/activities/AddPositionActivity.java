package com.averroes.hsstock.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.averroes.hsstock.adapters.DepotAdapter;
import com.averroes.hsstock.adapters.LocationAdapter;
import com.averroes.hsstock.database.DBHandler;
import com.averroes.hsstock.inc.Commons;
import com.averroes.hsstock.models.Depot;
import com.averroes.hsstock.models.Product;
import com.averroes.hsstock.models.Sell;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddPositionActivity extends Commons {

    private ImageButton backBtn;
    private FloatingActionButton addNewLocation;
    private EditText positionET;
    private Button addBtn;
    private ConstraintLayout mainLayout;
    private RecyclerView locationsRV;

    private DBHandler dbHandler;
    private ArrayList<Depot> depots;
    private LocationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_position);
        
        backBtn = findViewById(R.id.backBtn);
        positionET = findViewById(R.id.positionET);
        addBtn = findViewById(R.id.addBtn);
        mainLayout = findViewById(R.id.mainLayout);
        addNewLocation = findViewById(R.id.addNewLocation);
        locationsRV = findViewById(R.id.locationsRV);

        dbHandler = new DBHandler(this);
        depots = new ArrayList<>();
        
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

        adapter = new LocationAdapter(AddPositionActivity.this, this, depots);
        locationsRV.setAdapter(adapter);
        locationsRV.setLayoutManager(new LinearLayoutManager(AddPositionActivity.this));
        adapter.notifyDataSetChanged();

  }

    private void addPosition() {

        SharedPreferences sharedPreferences = getSharedPreferences("region_settings", Context.MODE_PRIVATE);

        String positionText = positionET.getText().toString().trim();
        String regionText = sharedPreferences.contains("selected_region")? sharedPreferences.getString("selected_region", ""): "CENTRE";


        showSnackBarMessage(mainLayout, "Positions ajouté(s)");
        positionET.setText("");

    }
}