package com.averroes.hsstock.activities;

import android.database.Cursor;
import android.os.Bundle;

import com.averroes.hsstock.adapters.ModelAdapter;
import com.averroes.hsstock.database.DBHandler;
import com.averroes.hsstock.models.Model;
import com.averroes.hsstock.models.Position;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.averroes.hsstock.R;

import java.util.ArrayList;

public class ModelsActivity extends AppCompatActivity {

    private RecyclerView modelsRV;
    private ImageView emptyIV;
    private TextView nodataTV;

    private ModelAdapter adapter;
    private ArrayList<Model> models;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());
        toolBarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorWhite));
        toolBarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorWhite));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        modelsRV = findViewById(R.id.modelsRV);
        emptyIV = findViewById(R.id.emptyIV);
        nodataTV = findViewById(R.id.nodataTV);

        models = new ArrayList<>();
        dbHandler = new DBHandler(this);

        setModelsData();

        adapter = new ModelAdapter(ModelsActivity.this, this, models);
        modelsRV.setAdapter(adapter);
        modelsRV.setLayoutManager(new LinearLayoutManager(ModelsActivity.this));
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
                    model.set_colors(colors.replaceAll(",", " "));

                    models.add(model);
                }
            }
        }
        else
            Toast.makeText(this, R.string.db_error, Toast.LENGTH_LONG).show();
    }
}