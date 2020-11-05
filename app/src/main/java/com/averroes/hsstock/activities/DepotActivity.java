package com.averroes.hsstock.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.averroes.hsstock.R;
import com.averroes.hsstock.adapters.DepotAdapter;
import com.averroes.hsstock.database.DBHandler;
import com.averroes.hsstock.models.Depot;

import java.util.ArrayList;

public class DepotActivity extends AppCompatActivity {

    private ImageButton backBtn,addLocationBtn;
    private EditText searchET;
    private RecyclerView locationsRV;
    private ImageView empty;
    private TextView nodata;

    private ArrayList<Depot> depots;
    private DBHandler dbHandler;
    private DepotAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depot);

        backBtn = findViewById(R.id.backBtn);
        addLocationBtn = findViewById(R.id.addLocationBtn);
        searchET = findViewById(R.id.searchET);
        locationsRV = findViewById(R.id.locationsRV);
        empty = findViewById(R.id.empty);
        nodata = findViewById(R.id.nodata);

        dbHandler = new DBHandler(this);
        depots = new ArrayList<>();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        addLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(DepotActivity.this, AddPositionActivity.class),2);
            }
        });

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        getData();

        adapter = new DepotAdapter(DepotActivity.this, this, depots);
        locationsRV.setAdapter(adapter);
        locationsRV.setLayoutManager(new LinearLayoutManager(DepotActivity.this));
        adapter.notifyDataSetChanged();
    }

    private void getData() {

        Cursor cursor = dbHandler.getAllDepots();

        if(cursor != null){

            if(cursor.getCount() == 0){
                empty.setVisibility(View.VISIBLE);
                nodata.setVisibility(View.VISIBLE);
            }
            else{

                empty.setVisibility(View.GONE);
                nodata.setVisibility(View.GONE);

                depots.clear();

                while(cursor.moveToNext()){

                    Depot depot = new Depot(
                            cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getString(2)
                    );

                    depots.add(depot);

                }

            }

        }
        else
            Toast.makeText(this, "Erreur de la base de données", Toast.LENGTH_LONG).show();
    }

    private void filter(String text) {
        ArrayList<Depot> filteredList = new ArrayList<>();

        for (Depot depot : depots) {
            if (depot.get_reference().toLowerCase().trim().contains(text.toLowerCase())) {
                filteredList.add(depot);
            }
        }

        adapter.filteredList(filteredList);

        if(filteredList.size() == 0){
            empty.setVisibility(View.VISIBLE);
            nodata.setVisibility(View.VISIBLE);
        }else{
            empty.setVisibility(View.GONE);
            nodata.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == 1 || requestCode == 2){
            recreate();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}