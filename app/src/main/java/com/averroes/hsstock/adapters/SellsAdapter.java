package com.averroes.hsstock.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.averroes.hsstock.R;
import com.averroes.hsstock.models.Sell;
import com.averroes.hsstock.activities.UpdateSellActivity;

import java.util.ArrayList;

public class SellsAdapter extends RecyclerView.Adapter<SellsAdapter.MyViewHolder> {

    private Context context;
    private Activity activity;
    ArrayList<Sell> sells;

    private Animation animation;

    public SellsAdapter(Context context, Activity activity, ArrayList sells){
        this.context = context;
        this.sells = sells;
        this.activity = activity;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.sells_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.name.setText(String.valueOf(sells.get(position).getProduct().get_name()));
        holder.price.setText(String.valueOf(sells.get(position).get_price()));
        holder.date.setText(String.valueOf(sells.get(position).get_date()));
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdateSellActivity.class);
                intent.putExtra("sell_id", String.valueOf(sells.get(position).get_id()));
                intent.putExtra("prod_id", String.valueOf(sells.get(position).getProduct().get_id()));
                intent.putExtra("price", String.valueOf(sells.get(position).get_price()));
                intent.putExtra("ref", String.valueOf(sells.get(position).getProduct().get_name()));
                intent.putExtra("size", String.valueOf(sells.get(position).getProduct().get_size()));
                intent.putExtra("color", String.valueOf(sells.get(position).getProduct().get_color()));
                activity.startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sells.size();
    }

    public int getSum(){
        int sum = 0;
        for(Sell sell : sells){
            sum += sell.get_price();
        }

        return sum;
    }

    public void filteredList(ArrayList<Sell> filteredList) {

        sells = filteredList;
        notifyDataSetChanged();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView name,price, date;
        private LinearLayout layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.mainLayout);
            name = itemView.findViewById(R.id.referenceTV);
            price = itemView.findViewById(R.id.priceTV);
            date = itemView.findViewById(R.id.dateTV);
            animation = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
            layout.setAnimation(animation);
        }
    }
}
