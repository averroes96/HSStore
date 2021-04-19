package com.averroes.hsstock.inc;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.averroes.hsstock.R;
import com.google.android.material.snackbar.Snackbar;

public class Commons extends AppCompatActivity {

    public void showSnackBarMessage(View view, int resId){
        Snackbar snackbar = Snackbar.make(view, resId, Snackbar.LENGTH_LONG);
        snackbar.setAction(
                getString(R.string.close), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
        snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
        TextView snackTextView = (TextView) snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        snackTextView.setMaxLines(3);
        snackbar.show();
    }

    public void showSnackBarMessage(View view, String res){
        Snackbar snackbar = Snackbar.make(view, res, Snackbar.LENGTH_LONG);
        snackbar.setAction(
                getString(R.string.close), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
        snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
        TextView snackTextView = (TextView) snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        snackTextView.setMaxLines(3);
        snackbar.show();
    }

    public void putPreferences(String spName, String key, String value){

        SharedPreferences sharedPreferences = getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();

    }

    public String getFilterSettings(){

        SharedPreferences sharedPreferences = getSharedPreferences("filter_settings", Context.MODE_PRIVATE);
        String query = "";

        if(sharedPreferences.contains("type_filter")) {
            String typeText = sharedPreferences.getString("type_filter", "");
            if (!typeText.equals("") && !typeText.equals(getResources().getStringArray(R.array.types)[0]))
                if (typeText.equals("Soirée"))
                    query = " WHERE type IN ('Soirée', 'Sabo-Soirée', 'Sandal-Soirée', 'Chaussure-Soirée') ";
                else
                    query = " WHERE type = '" + typeText + "' ";
        }

        if(sharedPreferences.contains("color_filter")) {
            String textColor = sharedPreferences.getString("color_filter", "");
            if (!textColor.equals("")) {
                if (query.equals(""))
                    query = " WHERE color = '" + textColor + "' ";
                else
                    query += "AND color = '" + textColor + "' ";
            }
        }

        if(sharedPreferences.contains("size_color")) {
            String size = sharedPreferences.getString("size_filter", "");
            if (query.equals(""))
                query = " WHERE size = " + Integer.parseInt(size) + " ";
            else
                query += "AND size = " + Integer.parseInt(size) + " ";
        }

        if(query.equals(""))
            query = " WHERE sold = ?";
        else
            query += "AND sold = ?";

        return query;

    }

    public void removeFilterPrefs(){
        SharedPreferences sharedPreferences = getSharedPreferences("filter_settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public void openTypeDialog(final TextView textView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.type_dialog_msg))
                .setItems(R.array.types, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String[] array = getResources().getStringArray(R.array.types);
                        textView.setText(array[i]);
                    }
                })
                .create().show();
    }

    public boolean isValidSize(String stringSize){

        int size = Integer.parseInt(stringSize);

        return size <= 45 && size >= 15;

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
