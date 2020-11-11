package com.averroes.hsstock.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.averroes.hsstock.R;
import com.averroes.hsstock.activities.UpdatePositionActivity;
import com.averroes.hsstock.database.DBHandler;
import com.averroes.hsstock.models.Depot;

import java.util.ArrayList;

public class DepotAdapter extends RecyclerView.Adapter<DepotAdapter.MyViewHolder> {

    private Context context;
    private Activity activity;
    ArrayList<Depot> depots;
    private DBHandler dbHandler;

    private Animation animation;



    public DepotAdapter(Context context, Activity activity, ArrayList depots){
        this.context = context;
        this.depots = depots;
        this.activity = activity;
        dbHandler = new DBHandler(activity);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.depot_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.referenceTV.setText(String.valueOf(depots.get(position).get_reference()));
        holder.locationTV.setText(String.valueOf(depots.get(position).get_location()));
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdatePositionActivity.class);
                intent.putExtra("id", String.valueOf(depots.get(position).get_id()));
                intent.putExtra("reference", String.valueOf(depots.get(position).get_reference()));
                intent.putExtra("position", String.valueOf(depots.get(position).get_location()));
                activity.startActivityForResult(intent, 1);
            }
        });

    }

    @Override
    public int getItemCount() {
        return depots.size();
    }

    public int getDuplicatesCount(){
        ArrayList<String> temp = new ArrayList<>();
        for(Depot depot: depots){
            if(!temp.contains(depot.get_reference()))
                temp.add(depot.get_reference());
        }

        return depots.size() - temp.size();
    }

    public void filteredList(ArrayList<Depot> filteredList) {
        depots = filteredList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView referenceTV,locationTV;
        private LinearLayout layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.mainLayout);
            referenceTV = itemView.findViewById(R.id.referenceTV);
            locationTV = itemView.findViewById(R.id.locationTV);
            animation = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
            layout.setAnimation(animation);

        }
    }

}