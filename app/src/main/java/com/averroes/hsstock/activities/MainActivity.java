package com.averroes.hsstock.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
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

import com.averroes.hsstock.adapters.CustomAdapter;
import com.averroes.hsstock.database.DBHandler;
import com.averroes.hsstock.inc.Commons;
import com.averroes.hsstock.models.Product;
import com.averroes.hsstock.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends Commons {

    private TextView nodata;
    private ImageView empty;
    private TextView sum,countRefTV;
    private ImageButton goToDepot,refsIB;
    private RecyclerView productList;
    private FloatingActionButton filterFAB;


    private DBHandler dbHandler;
    private ArrayList<Product> products;

    private CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton addProduct = findViewById(R.id.addProductBtn);
        ImageButton sellsBtn = findViewById(R.id.goToSellsBtn);
        EditText search = findViewById(R.id.searchET);
        productList = findViewById(R.id.productsRV);
        sum = findViewById(R.id.sumText);
        nodata = findViewById(R.id.nodata);
        empty = findViewById(R.id.empty);
        goToDepot = findViewById(R.id.goToDepot);
        refsIB = findViewById(R.id.refsIB);
        filterFAB = findViewById(R.id.filterFAB);
        countRefTV = findViewById(R.id.countRefTV);

        dbHandler = new DBHandler(this);
        products = new ArrayList<>();

        refsIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, ProductsActivity.class),1);
            }
        });

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, AddProductActivity.class),1);
            }
        });

        sellsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, SellsActivity.class), 2);
            }
        });

        goToDepot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DepotActivity.class));
            }
        });

        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filter(s.toString());
            }
        });

        filterFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog();
            }
        });

        //for(int i=1; i < 36; i++)
        //dbHandler.ignoreThis();
        
        storeData();
    }

    private void showFilterDialog() {

        final BottomSheetDialog dialog = new BottomSheetDialog(this);

        final View view = LayoutInflater.from(this).inflate(R.layout.filter_layout, null);
        dialog.setContentView(view);

        ImageButton backBtn;
        final TextView typeTV,sizeTV,colorTV;
        Button filterBtn;

        backBtn = view.findViewById(R.id.backBtn);
        typeTV = view.findViewById(R.id.typeTV);
        sizeTV = view.findViewById(R.id.sizeTV);
        colorTV = view.findViewById(R.id.colorTV);
        filterBtn = view.findViewById(R.id.filterBtn);

        SharedPreferences sharedPreferences = getSharedPreferences("filter_settings", Context.MODE_PRIVATE);

        if(sharedPreferences.contains("type_filter")) {
            typeTV.setText(sharedPreferences.getString("type_filter", ""));
        }
        if(sharedPreferences.contains("color_filter")) {
            colorTV.setText(sharedPreferences.getString("color_filter", ""));
        }
        if(sharedPreferences.contains("size_filter")) {
            sizeTV.setText(sharedPreferences.getString("size_filter", ""));
        }

        dialog.show();

        typeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeFiltering();
            }

            private void typeFiltering() {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle(getString(R.string.type_dialog_msg))
                        .setItems(R.array.types, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String[] array = getResources().getStringArray(R.array.types);
                                typeTV.setText(array[i]);
                                putPreferences("filter_settings", "type_filter", typeTV.getText().toString());
                            }
                        })
                        .create().show();
            }
        });

        sizeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sizeFiltering();
            }

            private void sizeFiltering() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle(getString(R.string.size_dialog_msg))
                            .setItems(R.array.sizes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String[] array = getResources().getStringArray(R.array.sizes);
                                    sizeTV.setText(array[i]);
                                    putPreferences("filter_settings", "size_filter", sizeTV.getText().toString());

                                }
                            })
                            .create().show();

            }
        });

        colorTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorFiltering();
            }

            private void colorFiltering() {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle(getString(R.string.color_dialog))
                        .setItems(dbHandler.getColors(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String[] array = dbHandler.getColors();
                                colorTV.setText(array[i]);
                                putPreferences("filter_settings", "color_filter", colorTV.getText().toString());
                            }
                        })
                        .create().show();

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                advancedFiltering();
            }

            private void advancedFiltering() {
                String query = "";
                String typeText = typeTV.getText().toString();
                String textColor = colorTV.getText().toString();
                if(!typeText.equals("") && !typeText.equals(getResources().getStringArray(R.array.types)[0]))
                    if(typeText.equals("Soirée"))
                        query = " WHERE type IN ('Soirée', 'Sabo-Soirée', 'Sandal-Soirée', 'Chaussure-Soirée') ";
                    else
                        query = " WHERE type = '" + typeText + "' ";

                if(!textColor.equals("")){
                    if(query.equals(""))
                        query = " WHERE color = '" + textColor +  "' ";
                    else
                        query += "AND color = '" + textColor + "' ";
                }
                if(!sizeTV.getText().toString().isEmpty()){
                    if(query.equals(""))
                        query = " WHERE size = " + Integer.parseInt(sizeTV.getText().toString()) +  " ";
                    else
                        query += "AND size = " + Integer.parseInt(sizeTV.getText().toString()) + " ";
                }

                if(query.equals(""))
                    query = " WHERE sold = ?";
                else
                    query += "AND sold = ?";

                getResults(query);

                customAdapter = new CustomAdapter(MainActivity.this, MainActivity.this, products);
                productList.setAdapter(customAdapter);
                productList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                customAdapter.notifyDataSetChanged();
                String sumText = customAdapter.getItemCount() + " " + getString(R.string.shoe_s);
                String refCountText = customAdapter.getRefCount() + " " + getString(R.string.reference_s);
                sum.setText(sumText);
                countRefTV.setText(refCountText);
                dialog.dismiss();
            }

            private void getResults(String query) {

                Cursor cursor = dbHandler.getResults(query);

                if(cursor != null){

                    if(cursor.getCount() == 0){
                        products.clear();
                        empty.setVisibility(View.VISIBLE);
                        nodata.setVisibility(View.VISIBLE);
                    }
                    else{
                        empty.setVisibility(View.GONE);
                        nodata.setVisibility(View.GONE);

                        products.clear();

                        while(cursor.moveToNext()){
                            Product product = new Product();
                            product.set_id(cursor.getInt(0));
                            product.set_name(cursor.getString(1));
                            product.set_color(cursor.getString(3));
                            product.set_size(cursor.getInt(2));
                            product.set_image(cursor.getString(4));
                            product.set_type(cursor.getString(5));
                            products.add(product);
                        }

                    }

                }
                else
                    Toast.makeText(view.getContext(), getString(R.string.db_error), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void filter(String text) {
        ArrayList<Product> filteredList = new ArrayList<>();

        for(Product str : products){
            if(str.get_name().toLowerCase().trim().contains(text.toLowerCase())){
                        filteredList.add(str);
            }
        }

        customAdapter.filteredList(filteredList);
        String sumText = customAdapter.getItemCount() + " " + getString(R.string.shoe_s);
        String refCountText = customAdapter.getRefCount() + " " + getString(R.string.reference_s);
        sum.setText(sumText);
        countRefTV.setText(refCountText);

        if(filteredList.size() == 0){
            empty.setVisibility(View.VISIBLE);
            nodata.setVisibility(View.VISIBLE);
        }
        else{
            empty.setVisibility(View.GONE);
            nodata.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        storeData();
        customAdapter.notifyDataSetChanged();
    }

    private void storeData() {

        String query = getFilterSettings();
        Cursor cursor = dbHandler.getResults(query);

        if(cursor != null){

            if(cursor.getCount() == 0){
                empty.setVisibility(View.VISIBLE);
                nodata.setVisibility(View.VISIBLE);
            }
            else{

                empty.setVisibility(View.GONE);
                nodata.setVisibility(View.GONE);

                products.clear();

                while(cursor.moveToNext()){
                    Product product = new Product();
                    product.set_id(cursor.getInt(0));
                    product.set_name(cursor.getString(1));
                    product.set_color(cursor.getString(3));
                    product.set_size(cursor.getInt(2));
                    product.set_image(cursor.getString(4));
                    product.set_type(cursor.getString(5));
                    products.add(product);
                }

            }

        }
        else
            Toast.makeText(this, "Erreur de la base de données", Toast.LENGTH_LONG).show();

        customAdapter = new CustomAdapter(MainActivity.this, this, products);
        productList.setAdapter(customAdapter);
        productList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        customAdapter.notifyDataSetChanged();
        String sumText = customAdapter.getItemCount() + " " + getString(R.string.shoe_s);
        String refCountText = customAdapter.getRefCount() + " " + getString(R.string.reference_s);
        sum.setText(sumText);
        countRefTV.setText(refCountText);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == 1 || requestCode == 2 || resultCode == 3){
            recreate();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeFilterPrefs();
    }

}