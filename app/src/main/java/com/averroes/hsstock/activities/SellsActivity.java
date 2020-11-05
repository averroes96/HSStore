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

import com.averroes.hsstock.database.DBHandler;
import com.averroes.hsstock.models.Product;
import com.averroes.hsstock.R;
import com.averroes.hsstock.models.Sell;
import com.averroes.hsstock.adapters.SellsAdapter;

import java.util.ArrayList;

public class SellsActivity extends AppCompatActivity {

    private EditText search;
    private TextView sum,count,nodata;
    private RecyclerView sellsList;
    private ImageView empty;

    private ArrayList<Sell> sells;
    private SellsAdapter customAdapter;

    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sells);

        ImageButton back, addSell;

        back = findViewById(R.id.backBtn);
        addSell = findViewById(R.id.addSellsBtn);
        sum = findViewById(R.id.sumText);
        count = findViewById(R.id.countText);
        sellsList = findViewById(R.id.sellsRV);
        search = findViewById(R.id.searchET);
        empty = findViewById(R.id.empty);
        nodata = findViewById(R.id.nodata);

        dbHandler = new DBHandler(this);
        sells = new ArrayList<>();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        addSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(SellsActivity.this, AddSellActivity.class),2);
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

        customAdapter = new SellsAdapter(SellsActivity.this, this, sells);
        sellsList.setAdapter(customAdapter);
        sellsList.setLayoutManager(new LinearLayoutManager(SellsActivity.this));
        customAdapter.notifyDataSetChanged();

        sum.setText(customAdapter.getSum() + " Da");
        count.setText(customAdapter.getItemCount() + " Chaussure(s)");

    }

    private void storeData() {

        Cursor cursor = dbHandler.getAllSells();

        if(cursor != null){

            if(cursor.getCount() == 0){
                empty.setVisibility(View.VISIBLE);
                nodata.setVisibility(View.VISIBLE);
            }
            else{

                empty.setVisibility(View.GONE);
                nodata.setVisibility(View.GONE);

                sells.clear();

                while(cursor.moveToNext()){

                    Product product = new Product();
                    product.set_id(cursor.getInt(3));
                    product.set_name(cursor.getString(4));
                    product.set_color(cursor.getString(6));
                    product.set_size(cursor.getInt(5));

                    Sell sell = new Sell();
                    sell.setProduct(product);
                    sell.set_id(cursor.getInt(0));
                    sell.set_price(cursor.getInt(1));
                    sell.set_date(cursor.getString(2));

                    sells.add(sell);

                }

            }

        }
        else
            Toast.makeText(this, "Erreur de la base de données", Toast.LENGTH_LONG).show();
    }

    private void filter(String text) {
        ArrayList<Sell> filteredList = new ArrayList<>();

        for (Sell sell : sells) {
            if (sell.getProduct().get_name().toLowerCase().trim().contains(text.toLowerCase())) {
                filteredList.add(sell);
            }
        }

        customAdapter.filteredList(filteredList);
        sum.setText(customAdapter.getSum() + " Da");
        count.setText(customAdapter.getItemCount() + " Chaussure(s)");

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