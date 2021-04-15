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
import com.averroes.hsstock.adapters.PositionAdapter;
import com.averroes.hsstock.database.DBHandler;
import com.averroes.hsstock.models.Depot;
import com.averroes.hsstock.models.Position;

import java.util.ArrayList;

public class PositionsActivity extends AppCompatActivity {

    ImageButton backBtn,addPositionBtn;
    EditText searchET;
    TextView counterTV,nodata;
    RecyclerView positionsRV;
    ImageView empty;

    private DBHandler dbHandler;
    private ArrayList<Position> positions;
    private PositionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positions);

        backBtn = findViewById(R.id.backBtn);
        searchET = findViewById(R.id.searchET);
        counterTV = findViewById(R.id.counterTV);
        positionsRV = findViewById(R.id.positionsRV);
        empty = findViewById(R.id.empty);
        nodata = findViewById(R.id.nodata);
        addPositionBtn = findViewById(R.id.addPositionBtn);

        dbHandler = new DBHandler(this);
        positions = new ArrayList<>();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

        addPositionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(PositionsActivity.this, AddPositionActivity.class), 1);
            }
        });
        
        getData();

        adapter = new PositionAdapter(PositionsActivity.this, this, positions);
        positionsRV.setAdapter(adapter);
        positionsRV.setLayoutManager(new LinearLayoutManager(PositionsActivity.this));
        adapter.notifyDataSetChanged();
        String countText = adapter.getItemCount() + " " + getString(R.string.positions);
        counterTV.setText(countText);
    }

    private void getData() {

        Cursor cursor = dbHandler.getAllPositions();

        if(cursor != null){

            if(cursor.getCount() == 0){
                empty.setVisibility(View.VISIBLE);
                nodata.setVisibility(View.VISIBLE);
            }
            else{
                empty.setVisibility(View.GONE);
                nodata.setVisibility(View.GONE);
                positions.clear();

                while(cursor.moveToNext()){

                    Position position = new Position();
                    position.set_name(cursor.getString(0));

                    Cursor cr = dbHandler.getPositionRefs(position.get_name());
                    while ((cr.moveToNext())){
                        String price = cr.getString(1) != null ? " : " + cr.getString(1) : "";
                        position.set_refs(position.get_refs().concat(cr.getString(0) + price + "\n"));
                        position.set_num_refs(position.get_num_refs() + 1);
                    }

                    positions.add(position);
                }
            }
        }
        else
            Toast.makeText(this, "Erreur de la base de données", Toast.LENGTH_LONG).show();
    }

    private void filter(String text) {
        ArrayList<Position> filteredList = new ArrayList<>();

        for (Position position : positions) {
            if (position.get_name().toLowerCase().trim().contains(text.toLowerCase())) {
                filteredList.add(position);
            }
        }

        adapter.filteredList(filteredList);
        String countText = adapter.getItemCount() + " " + getString(R.string.positions);
        counterTV.setText(countText);

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