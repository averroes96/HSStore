package com.averroes.hsstock.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
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
    public ArrayList<Depot> depots;

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
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                depots.remove(position);
                notifyItemRemoved(position);
            }
        });

        holder.referenceET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                depots.get(position).set_reference(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        holder.referenceET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                depots.get(position).set_price(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return depots.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private EditText referenceET,priceET;
        private ImageButton removeBtn;
        private LinearLayout layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.mainLayout);
            referenceET = itemView.findViewById(R.id.referenceET);
            priceET = itemView.findViewById(R.id.priceET);
            removeBtn = itemView.findViewById(R.id.removeBtn);
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
            layout.setAnimation(animation);
        }
    }

}
