package com.averroes.hsstock.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.averroes.hsstock.models.Product;
import com.averroes.hsstock.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView nodata;
    private ImageView empty;
    private TextView sum,typeTV;
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
        typeTV = findViewById(R.id.typeTV);
        refsIB = findViewById(R.id.refsIB);
        filterFAB = findViewById(R.id.filterFAB);

        typeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTypeDialog();
            }
        });
        typeTV.setText(getResources().getStringArray(R.array.types)[0]);

        dbHandler = new DBHandler(this);
        products = new ArrayList<>();

        refsIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, RefsActivity.class), 3);
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

        //dbHandler.ignoreThis();
        
        storeData();

        customAdapter = new CustomAdapter(MainActivity.this, this, products);
        productList.setAdapter(customAdapter);
        productList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        customAdapter.notifyDataSetChanged();

        sum.setText(customAdapter.getItemCount() + " Chaussure(s)");
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
                sum.setText(customAdapter.getItemCount() + " " + getString(R.string.shoe_s));
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


    private void openTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.type_dialog_msg))
                .setItems(R.array.types, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String[] array = getResources().getStringArray(R.array.types);
                        typeTV.setText(array[i]);
                        if(i == 0) {
                            storeData();
                            customAdapter = new CustomAdapter(MainActivity.this, MainActivity.this, products);
                            productList.setAdapter(customAdapter);
                            productList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                            customAdapter.notifyDataSetChanged();
                            String sumText = customAdapter.getItemCount() + " " + getString(R.string.shoe_s);
                            sum.setText(sumText);
                        }
                        else
                            filterByType(array[i]);
                    }
                })
                .create().show();
    }

    private void filterByType(String s) {
        ArrayList<Product> filteredList = new ArrayList<>();

        for(Product str : products){
            if(!str.get_type().equals("")) {
                if (str.get_type().toLowerCase().trim().equals(s.toLowerCase())) {
                    filteredList.add(str);
                }
            }
        }

        customAdapter.filteredList(filteredList);
        String sumText = customAdapter.getItemCount() + " " + getString(R.string.shoe_s);
        sum.setText(sumText);

        if(filteredList.size() == 0){
            empty.setVisibility(View.VISIBLE);
            nodata.setVisibility(View.VISIBLE);
        }
        else{
            empty.setVisibility(View.GONE);
            nodata.setVisibility(View.GONE);
        }
    }

    private void filter(String text) {
        ArrayList<Product> filteredList = new ArrayList<>();

        for(Product str : products){
            if(str.get_name().toLowerCase().trim().contains(text.toLowerCase())){
                if(!typeTV.getText().toString().equals(getResources().getStringArray(R.array.types)[0])) {
                    if (str.get_type().equals(typeTV.getText().toString()))
                        filteredList.add(str);
                }
                else{
                    filteredList.add(str);
                }
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
                    product.set_type(cursor.getString(5));
                    products.add(product);
                }

            }

        }
        else
            Toast.makeText(this, "Erreur de la base de données", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == 1 || requestCode == 2 || resultCode == 3){
            recreate();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}