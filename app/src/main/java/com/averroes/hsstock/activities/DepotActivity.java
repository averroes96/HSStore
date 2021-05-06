package com.averroes.hsstock.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.averroes.hsstock.R;
import com.averroes.hsstock.adapters.DepotAdapter;
import com.averroes.hsstock.database.DBHandler;
import com.averroes.hsstock.inc.Commons;
import com.averroes.hsstock.models.Depot;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class DepotActivity extends Commons {

    private ImageButton backBtn,addLocationBtn,positionsBtn,regionsBtn,barcodeBtn;
    private EditText searchET;
    private RecyclerView locationsRV;
    private ImageView empty;
    private TextView nodata,countTV,duplicateTV,/*filterTV,*/suggestionsTV;
    private ConstraintLayout mainLayout;
    private LinearLayout suggestionsLayout;

    private ArrayList<Depot> depots;
    private DBHandler dbHandler;
    private DepotAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depot);

        backBtn = findViewById(R.id.backBtn);
        addLocationBtn = findViewById(R.id.addLocationBtn);
        searchET = findViewById(R.id.searchET);
        locationsRV = findViewById(R.id.locationsRV);
        empty = findViewById(R.id.empty);
        nodata = findViewById(R.id.nodata);
        countTV =  findViewById(R.id.countTV);
        duplicateTV = findViewById(R.id.duplicateTV);
        //filterTV = findViewById(R.id.filterTV);
        positionsBtn = findViewById(R.id.positionsBtn);
        regionsBtn = findViewById(R.id.regionsBtn);
        mainLayout = findViewById(R.id.mainLayout);
        suggestionsTV = findViewById(R.id.suggestionsTV);
        suggestionsLayout = findViewById(R.id.suggestionsLayout);
        barcodeBtn = findViewById(R.id.barcodeBtn);

        dbHandler = new DBHandler(this);
        depots = new ArrayList<>();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        positionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(DepotActivity.this, PositionsActivity.class),
                        3);
            }
        });

        addLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(DepotActivity.this, AddDepotActivity.class),2);
            }
        });

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter(charSequence.toString());
                checkSimilarity(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        
        regionsBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegionsDialog();
            }
        }));
        
        /*filterTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFilterDialog();
            }
        });
        filterTV.setText(getResources().getStringArray(R.array.filter_types)[0]);*/
        
        barcodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();
            }
        });

        suggestionsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchET.setText(suggestionsTV.getText());
                suggestionsLayout.setVisibility(View.GONE);
                suggestionsTV.setText("");
            }
        });

        searchET.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                scanCode();
                return true;
            }
        });

        getData("");

        adapter = new DepotAdapter(DepotActivity.this, this, depots);
        locationsRV.setAdapter(adapter);
        locationsRV.setLayoutManager(new LinearLayoutManager(DepotActivity.this));
        adapter.notifyDataSetChanged();

        countTV.setText(adapter.getItemCount() + " Reference(s)");
        duplicateTV.setText(adapter.getDuplicatesCount() + " duplicate(s)");
    }

    private void scanCode() {

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureActivity.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning code...");
        integrator.initiateScan();
    }

    private void checkSimilarity(String string) {

        String best = "";
        int globalMinDist = 9999;

        if(adapter.getItemCount() == 0){
            for(Depot depot : depots){
                int minDist = editDistDP(string, depot.get_reference(), string.length(), depot.get_reference().length());
                if(minDist < globalMinDist) {
                    globalMinDist = minDist;
                    best = depot.get_reference();
                }
            }

            suggestionsLayout.setVisibility(View.VISIBLE);
            suggestionsTV.setText(best);
        }
        else{
            suggestionsLayout.setVisibility(View.GONE);
            suggestionsTV.setText("");
        }


    }

    private void showRegionsDialog() {

        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.filter_region_layout, null);
        dialog.setContentView(view);

        final TextView regionTV = view.findViewById(R.id.regionTV);
        Button confirmBtn = view.findViewById(R.id.confirmBtn);
        ImageButton backBtn = view.findViewById(R.id.backBtn);

        final SharedPreferences sharedPreferences = getSharedPreferences("region_settings", Context.MODE_PRIVATE);
        if(sharedPreferences.contains("selected_region")) {
            regionTV.setText(sharedPreferences.getString("selected_region", ""));
        }

        regionTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRegionDialog(regionTV);
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String chosenRegion = regionTV.getText().toString();
                filterRegion(chosenRegion);
                dialog.dismiss();
            }

        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void filterRegion(String chosenRegion) {

        SharedPreferences sharedPreferences = getSharedPreferences("region_settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("selected_region", chosenRegion);
        editor.apply();

        getData(chosenRegion);

        adapter = new DepotAdapter(DepotActivity.this, this, depots);
        locationsRV.setAdapter(adapter);
        locationsRV.setLayoutManager(new LinearLayoutManager(DepotActivity.this));
        adapter.notifyDataSetChanged();

        countTV.setText(adapter.getItemCount() + " Reference(s)");
        duplicateTV.setText(adapter.getDuplicatesCount() + " duplicate(s)");
    }

    private void openRegionDialog(final TextView regionTV) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.type_dialog_msg))
                .setItems(R.array.regions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String[] array = getResources().getStringArray(R.array.regions);
                        regionTV.setText(array[i]);
                    }
                })
                .create().show();
    }

    /*private void openFilterDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.filter_criteria))
                .setItems(R.array.filter_types, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String[] array = getResources().getStringArray(R.array.filter_types);
                        filterTV.setText(array[i]);
                        filter(searchET.getText().toString());
                    }
                })
                .create().show();
    }*/

    private void getData(String region) {

        Cursor cursor;
        SharedPreferences sharedPreferences = getSharedPreferences("region_settings", Context.MODE_PRIVATE);
        if(!region.equals(""))
            cursor = dbHandler.getAllDepots(region);
        else
            if(sharedPreferences.contains("selected_region"))
                cursor = dbHandler.getAllDepots(sharedPreferences.getString("selected_region", ""));
            else
                cursor = dbHandler.getAllDepots("CENTRE");

        if(cursor != null){

            depots.clear();

            if(cursor.getCount() == 0){
                empty.setVisibility(View.VISIBLE);
                nodata.setVisibility(View.VISIBLE);
            }
            else{
                empty.setVisibility(View.GONE);
                nodata.setVisibility(View.GONE);

                while(cursor.moveToNext()){
                    Depot depot = new Depot(
                            cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4)
                    );

                    depots.add(depot);
                }
            }
        }
        else
            showSnackBarMessage(mainLayout, "Erreur de la base de données");
    }

    private void filter(String text) {
        ArrayList<Depot> filteredList = new ArrayList<>();

        for (Depot depot : depots) {
            //if(filterTV.getText().equals(getResources().getStringArray(R.array.filter_types)[0])) {
            if (depot.get_reference().toLowerCase().trim().contains(text.toLowerCase())) {
                filteredList.add(depot);
            }
            //}
            /*if (depot.get_location().toLowerCase().trim().equals(text.toLowerCase())) {
                filteredList.add(depot);
            }*/
        }

        adapter.filteredList(filteredList);
        countTV.setText(adapter.getItemCount() + " Reference(s)");
        duplicateTV.setText(adapter.getDuplicatesCount() + " duplicate(s)");

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

        if(requestCode == 1 || requestCode == 2 || requestCode == 3){
            recreate();
        }
        else {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode, data);
            if(result != null){
                if(result.getContents() != null){
                    searchET.setText(result.getContents());
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}