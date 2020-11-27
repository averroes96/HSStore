package com.averroes.hsstock.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.averroes.hsstock.adapters.CustomAdapter;
import com.averroes.hsstock.database.DBHandler;
import com.averroes.hsstock.models.Product;
import com.averroes.hsstock.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView nodata;
    private ImageView empty;
    private TextView sum;
    private ImageButton goToDepot;

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
        RecyclerView productList = findViewById(R.id.productsRV);
        sum = findViewById(R.id.sumText);
        nodata = findViewById(R.id.nodata);
        empty = findViewById(R.id.empty);
        goToDepot = findViewById(R.id.goToDepot);


        dbHandler = new DBHandler(this);
        products = new ArrayList<>();

        MediaPlayer player = new MediaPlayer();

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, AddProductActivity.class),2);
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
                startActivityForResult(new Intent(MainActivity.this, DepotActivity.class), 3);
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

        storeData();

        customAdapter = new CustomAdapter(MainActivity.this, this, products);
        productList.setAdapter(customAdapter);
        productList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        customAdapter.notifyDataSetChanged();

        sum.setText(customAdapter.getItemCount() + " Chaussure(s)");
    }

    private void filter(String text) {
        ArrayList<Product> filteredList = new ArrayList<>();

        for(Product str : products){
            if(str.get_name().toLowerCase().trim().contains(text.toLowerCase())){
                filteredList.add(str);
            }
        }

        customAdapter.filteredList(filteredList);
        sum.setText(customAdapter.getItemCount() + " Chaussure(s)");

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

        Cursor cursor = dbHandler.readAllData();

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
                    products.add(product);
                }

            }

        }
        else
            Toast.makeText(this, "Erreur de la base de données", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == 1 || requestCode == 2){
            recreate();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}