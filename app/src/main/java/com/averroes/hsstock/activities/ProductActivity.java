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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.averroes.hsstock.R;
import com.averroes.hsstock.adapters.ModelAdapter;
import com.averroes.hsstock.database.DBHandler;
import com.averroes.hsstock.inc.Commons;
import com.averroes.hsstock.models.Model;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class ProductActivity extends Commons {

    private RecyclerView modelsRV;
    private ImageButton backBtn,filterBtn;
    private EditText searchET;
    private ImageView emptyIV;
    private TextView nodataTV,counterTV;

    private ModelAdapter adapter;
    private ArrayList<Model> models;
    private DBHandler dbHandler;
    private String chosenType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        modelsRV = findViewById(R.id.modelsRV);
        emptyIV = findViewById(R.id.emptyIV);
        nodataTV = findViewById(R.id.nodataTV);
        backBtn = findViewById(R.id.backBtn);
        filterBtn = findViewById(R.id.filterBtn);
        searchET = findViewById(R.id.searchET);
        counterTV = findViewById(R.id.counterTV);

        models = new ArrayList<>();
        dbHandler = new DBHandler(this);

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
                filter(editable.toString(), chosenType);
            }
        });

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog();
            }

            private void showFilterDialog() {

                final BottomSheetDialog dialog = new BottomSheetDialog(ProductActivity.this);

                final View view = LayoutInflater.from(ProductActivity.this).inflate(R.layout.filter_model_layout, null);
                dialog.setContentView(view);

                final TextView typeTV = view.findViewById(R.id.typeTV);
                Button filterBtn = view.findViewById(R.id.filterBtn);

                typeTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openTypeDialog(typeTV);
                    }
                });

                filterBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chosenType = typeTV.getText().toString();
                        filter(searchET.getText().toString(), chosenType);
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

        setModelsData();

        adapter = new ModelAdapter(ProductActivity.this, this, models);
        modelsRV.setAdapter(adapter);
        modelsRV.setLayoutManager(new LinearLayoutManager(ProductActivity.this));
        adapter.notifyDataSetChanged();
        String countText = adapter.getItemCount() + " " + getString(R.string.reference_s);
        counterTV.setText(countText);
    }

    private void filter(String toString, String type) {

            ArrayList<Model> filteredList = new ArrayList<>();

            for (Model model : models) {
                if(!type.equals("") && !type.equals(getResources().getStringArray(R.array.types)[0])){
                    if(type.equals(getResources().getStringArray(R.array.types)[2])){
                        if(model.get_type().contains(type)){
                            if (model.get_name().toLowerCase().trim().contains(toString.toLowerCase())) {
                                filteredList.add(model);
                            }
                        }
                    }
                    else if(model.get_type().equals(type)){
                        if (model.get_name().toLowerCase().trim().contains(toString.toLowerCase())) {
                            filteredList.add(model);
                        }
                    }
                }
                else{
                    if (model.get_name().toLowerCase().trim().contains(toString.toLowerCase())) {
                        filteredList.add(model);
                    }
                }

            }

            adapter.filteredList(filteredList);
            String countText = adapter.getItemCount() + " " + getString(R.string.reference_s);
            counterTV.setText(countText);

            if(filteredList.size() == 0){
                emptyIV.setVisibility(View.VISIBLE);
                nodataTV.setVisibility(View.VISIBLE);
            }else{
                emptyIV.setVisibility(View.GONE);
                nodataTV.setVisibility(View.GONE);
            }

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
                    Model model = new Model(
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3)
                    );

                    String colors = dbHandler.getModelColorsCount(model.get_name());
                    model.set_colors(colors + " " + getString(R.string.colors));

                    models.add(model);
                }
            }
        }
        else
            Toast.makeText(this, R.string.db_error, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == 1){
            recreate();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}