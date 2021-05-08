package com.averroes.hsstock.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.averroes.hsstock.R;
import com.averroes.hsstock.activities.UpdateDepotActivity;
import com.averroes.hsstock.database.DBHandler;
import com.averroes.hsstock.models.Depot;

import java.util.ArrayList;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.MyViewHolder>{

    private Context context;
    private Activity activity;
    ArrayList<Depot> depots;

    public LocationAdapter(Context context, Activity activity, ArrayList<Depot> depots){
        this.context = context;
        this.depots = depots;
        this.activity = activity;
    }

    @NonNull
    @Override
    public LocationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.location_row, parent, false);
        return new LocationAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return depots.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private EditText referenceET,priceET;
        private LinearLayout layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.mainLayout);
            referenceET = itemView.findViewById(R.id.referenceET);
            priceET = itemView.findViewById(R.id.priceET);
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
            layout.setAnimation(animation);
        }
    }

}
