package com.averroes.hsstock.activities;

import androidx.appcompat.app.AppCompatActivity;

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

public class AddSellActivity extends AppCompatActivity {

    private ImageButton back;
    private EditText reference, size, color, price;
    private Button add;

    private String referenceText,colorText, sizeNumber, priceNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sell);

        back = findViewById(R.id.backBtn);
        reference = findViewById(R.id.referencesET);
        size = findViewById(R.id.sizeET);
        color = findViewById(R.id.colorET);
        add = findViewById(R.id.addBtn);
        price = findViewById(R.id.priceET);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSell();
            }
        });
    }

    private void addSell() {

        referenceText = reference.getText().toString().trim();
        colorText = color.getText().toString().trim();
        sizeNumber = size.getText().toString().trim();
        priceNumber = price.getText().toString().trim();

        if(TextUtils.isEmpty(referenceText)){
            Toast.makeText(this, "Entrez la référence du chaussure s\'il vous plaît", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(colorText)){
            Toast.makeText(this, "Entrez le couleur du chaussure s\'il vous plaît", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(sizeNumber)){
            Toast.makeText(this, "Entrez le pointure du chaussure s\'il vous plaît", Toast.LENGTH_LONG).show();
            return;
        }
        if(!TextUtils.isDigitsOnly(sizeNumber) || Integer.parseInt(sizeNumber) <= 0){
            Toast.makeText(this, "Entrez un pointure valide s\'il vous plaît", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(priceNumber)){
            Toast.makeText(this, "Entrez le prix du chaussure s\'il vous plaît", Toast.LENGTH_LONG).show();
            return;
        }
        if(!TextUtils.isDigitsOnly(priceNumber) || Integer.parseInt(priceNumber) <= 0){
            Toast.makeText(this, "Entrez un prix valide s\'il vous plaît", Toast.LENGTH_LONG).show();
            return;
        }

        Product product = new Product();
        product.set_name(referenceText);
        product.set_color(colorText);
        product.set_size(Integer.parseInt(sizeNumber));

        Sell sell = new Sell();
        sell.setProduct(product);
        sell.set_price(Integer.parseInt(priceNumber));

        DBHandler dbHandler = new DBHandler(AddSellActivity.this);
        if(dbHandler.getProduct(sell.getProduct()) == null){
            Toast.makeText(this, "Les informations saisies n\'appartiennent à aucune chaussure\n", Toast.LENGTH_LONG).show();
            return;
        }
        dbHandler.addSell(sell);
        sell.getProduct().set_sold(1);
        dbHandler.updateProduct(dbHandler.getProduct(sell.getProduct()));
    }
}