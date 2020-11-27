package com.averroes.hsstock.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
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

    private ImageButton backBtn,addLocationBtn,positionsBtn;
    private EditText searchET;
    private RecyclerView locationsRV;
    private ImageView empty;
    private TextView nodata,countTV,duplicateTV,filterTV;

    private ArrayList<Depot> depots;
    private DBHandler dbHandler;
    private DepotAdapter adapter;
    private String currentSearchType = "ref";

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
        countTV =  findViewById(R.id.countTV);
        duplicateTV = findViewById(R.id.duplicateTV);
        filterTV = findViewById(R.id.filterTV);
        positionsBtn = findViewById(R.id.positionsBtn);

        dbHandler = new DBHandler(this);
        depots = new ArrayList<>();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        positionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(DepotActivity.this, PositionsActivity.class),
                        3);
            }
        });

        addLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(DepotActivity.this, AddDepotActivity.class),2);
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
        
        filterTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFilterDialog();
            }
        });
        filterTV.setText(getResources().getStringArray(R.array.filter_types)[0]);

        getData();

        adapter = new DepotAdapter(DepotActivity.this, this, depots);
        locationsRV.setAdapter(adapter);
        locationsRV.setLayoutManager(new LinearLayoutManager(DepotActivity.this));
        adapter.notifyDataSetChanged();

        countTV.setText(adapter.getItemCount() + " Reference(s)");
        duplicateTV.setText(adapter.getDuplicatesCount() + " duplicate(s)");
    }

    private void openFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.filter_criteria))
                .setItems(R.array.filter_types, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String[] array = getResources().getStringArray(R.array.filter_types);
                        filterTV.setText(array[i]);
                    }
                })
                .create().show();
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
            if(filterTV.getText().equals(getResources().getStringArray(R.array.filter_types)[0])) {
                if (depot.get_reference().toLowerCase().trim().contains(text.toLowerCase())) {
                    filteredList.add(depot);
                }
            }
            else{
                if (depot.get_location().toLowerCase().trim().contains(text.toLowerCase())) {
                    filteredList.add(depot);
                }
            }
        }

        adapter.filteredList(filteredList);
        countTV.setText(adapter.getItemCount() + " Reference(s)");
        duplicateTV.setText(adapter.getDuplicatesCount() + " duplicate(s)");

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

        if(requestCode == 1 || requestCode == 2 || requestCode == 3){
            recreate();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}