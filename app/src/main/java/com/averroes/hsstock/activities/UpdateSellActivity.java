package com.averroes.hsstock.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.averroes.hsstock.database.DBHandler;
import com.averroes.hsstock.models.Product;
import com.averroes.hsstock.R;
import com.averroes.hsstock.models.Sell;

public class UpdateSellActivity extends AppCompatActivity {

    private ImageButton back,delete;
    private EditText reference, size, color, price;
    private Button update;

    private String referenceText,colorText, sizeNumber,priceNumber;
    private Sell selectedSell;
    private Product selectedProduct;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_sell);

        back = findViewById(R.id.backBtn);
        reference = findViewById(R.id.referencesET);
        size = findViewById(R.id.sizeET);
        color = findViewById(R.id.colorET);
        update = findViewById(R.id.updateBtn);
        price = findViewById(R.id.priceET);
        delete = findViewById(R.id.deleteBtn);

        dbHandler = new DBHandler(this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean res = updateSell();
                if(res)
                    finish();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog();
            }
        });

        getIntentData();
    }

    public void getIntentData(){

        if(getIntent().hasExtra("ref") && getIntent().hasExtra("size") && getIntent().hasExtra("color") && getIntent().hasExtra("sell_id") && getIntent().hasExtra("prod_id") && getIntent().hasExtra("price")){

            selectedSell = new Sell();
            selectedProduct = new Product();

            selectedProduct.set_id(Integer.parseInt(getIntent().getStringExtra("prod_id")));
            selectedProduct.set_color(getIntent().getStringExtra("color"));
            selectedProduct.set_size(Integer.parseInt(getIntent().getStringExtra("size")));
            selectedProduct.set_name(getIntent().getStringExtra("ref"));

            selectedSell.setProduct(selectedProduct);
            selectedSell.set_price(Integer.parseInt(getIntent().getStringExtra("price")));
            selectedSell.set_id(Integer.parseInt(getIntent().getStringExtra("sell_id")));

            reference.setText(selectedSell.getProduct().get_name());
            size.setText(String.valueOf(selectedSell.getProduct().get_size()));
            color.setText(selectedSell.getProduct().get_color());
            price.setText(String.valueOf(selectedSell.get_price()));

        }
        else{
            Toast.makeText(this, "No data !", Toast.LENGTH_LONG).show();
        }

    }


    private void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Supprimer vend num: " + getIntent().getStringExtra("sell_id"));
        builder.setMessage("Etes-vous sûr que vous voulez supprimer vend num: " + getIntent().getStringExtra("sell_id") + "?");
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteSell();
                finish();
            }
        });
        builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.create().show();
    }

    private void deleteSell() {
        dbHandler.updateSold(selectedProduct.get_id(),0);
        dbHandler.deleteSell(selectedSell);
    }

    private boolean updateSell() {

        referenceText = reference.getText().toString().trim();
        colorText = color.getText().toString().trim();
        sizeNumber = size.getText().toString().trim();
        priceNumber = price.getText().toString().trim();

        if(TextUtils.isEmpty(referenceText)){
            Toast.makeText(this, "Entrez la référence du chaussure s\'il vous plaît", Toast.LENGTH_LONG).show();
            return false;
        }
        if(TextUtils.isEmpty(colorText)){
            Toast.makeText(this, "Entrez le couleur du chaussure s\'il vous plaît", Toast.LENGTH_LONG).show();
            return false;
        }
        if(TextUtils.isEmpty(sizeNumber)){
            Toast.makeText(this, "Entrez le pointure du chaussure s\'il vous plaît", Toast.LENGTH_LONG).show();
            return false;
        }
        if(!TextUtils.isDigitsOnly(sizeNumber) || Integer.parseInt(sizeNumber) <= 0){
            Toast.makeText(this, "Entrez un pointure valide s\'il vous plaît", Toast.LENGTH_LONG).show();
            return false;
        }
        if(TextUtils.isEmpty(priceNumber)){
            Toast.makeText(this, "Entrez le prix du chaussure s\'il vous plaît", Toast.LENGTH_LONG).show();
            return false;
        }
        if(!TextUtils.isDigitsOnly(priceNumber) || Integer.parseInt(priceNumber) <= 0){
            Toast.makeText(this, "Entrez un prix valide s\'il vous plaît", Toast.LENGTH_LONG).show();
            return false;
        }

        Product product = new Product(
                referenceText,
                colorText,
                Integer.parseInt(sizeNumber),
                ""
        );


        if (!product.equals(selectedSell.getProduct())) {

            if(dbHandler.getProduct(product) == null){
                Toast.makeText(this, "Les informations saisies n\'appartiennent à aucune chaussure\n", Toast.LENGTH_LONG).show();
                return false;
            }

            dbHandler.updateSold(selectedProduct.get_id(), 0);
            selectedProduct = dbHandler.getProduct(product);
            dbHandler.updateSold(selectedProduct.get_id(), 1);
            selectedSell.setProduct(selectedProduct);
        }
        selectedSell.set_price(Integer.parseInt(priceNumber));
        dbHandler.updateSell(selectedSell);
        return true;
    }
}