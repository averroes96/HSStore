package com.averroes.hsstock.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.averroes.hsstock.R;
import com.averroes.hsstock.adapters.ModelAdapter;
import com.averroes.hsstock.database.DBHandler;
import com.averroes.hsstock.models.Model;

import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {

    private RecyclerView modelsRV;
    private ImageView emptyIV;
    private TextView nodataTV;

    private ModelAdapter adapter;
    private ArrayList<Model> models;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        modelsRV = findViewById(R.id.modelsRV);
        emptyIV = findViewById(R.id.emptyIV);
        nodataTV = findViewById(R.id.nodataTV);

        models = new ArrayList<>();
        dbHandler = new DBHandler(this);

        setModelsData();

        adapter = new ModelAdapter(ProductActivity.this, this, models);
        modelsRV.setAdapter(adapter);
        modelsRV.setLayoutManager(new LinearLayoutManager(ProductActivity.this));
        adapter.notifyDataSetChanged();
    }

    private void setModelsData() {

        Cursor cursor = dbHandler.getAllModels();

        if(cursor != null){

            if(cursor.getCount() == 0){
                emptyIV.setVisibility(View.VISIBLE);
                nodataTV.setVisibility(View.VISIBLE);
            }
            else{
                emptyIV.setVisibility(View.GONE);
                nodataTV.setVisibility(View.GONE);
                models.clear();

                while(cursor.moveToNext()){

                    Model model = new Model(cursor.getString(0), cursor.getString(1), cursor.getString(2));

                    String colors = dbHandler.getModelColors(model.get_name());
                    model.set_colors(colors);

                    models.add(model);
                }
            }
        }
        else
            Toast.makeText(this, R.string.db_error, Toast.LENGTH_LONG).show();
    }
}