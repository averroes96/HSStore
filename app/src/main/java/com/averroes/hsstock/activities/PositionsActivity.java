package com.averroes.hsstock.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.averroes.hsstock.R;

public class PositionsActivity extends AppCompatActivity {

    ImageButton backBtn;
    EditText searchET;
    TextView counterTV;
    RecyclerView positionsRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positions);

        backBtn = findViewById(R.id.backBtn);
        searchET = findViewById(R.id.searchET);
        counterTV = findViewById(R.id.counterTV);
        positionsRV = findViewById(R.id.positionsRV);

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

            }
        });
    }
}