package com.averroes.hsstock.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.averroes.hsstock.models.Product;
import com.averroes.hsstock.R;
import com.averroes.hsstock.activities.UpdateProductActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private Activity activity;
    ArrayList<Product> products;

    private Animation animation;

    public CustomAdapter(Context context, Activity activity, ArrayList<Product> products){
        this.context = context;
        this.products = products;
        this.activity = activity;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.products_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.name.setText(String.valueOf(products.get(position).get_name()));
        holder.size.setText(String.valueOf(products.get(position).get_size()));
        holder.color.setText(String.valueOf(products.get(position).get_color()));
        /*
        if(!products.get(position).get_image().equals("") && Uri.parse(products.get(position).get_image()) != null) {
            holder.image.setImageURI(Uri.parse(products.get(position).get_image()));
        }
        else
            holder.image.setImageResource(R.drawable.ic_image_grey_48);*/

        try{
            Picasso.get().load(products.get(position).get_image()).placeholder(R.drawable.ic_image_grey_48).into(holder.image);
        }catch(Exception e){
            holder.image.setImageResource(R.drawable.ic_image_grey_48);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdateProductActivity.class);
                intent.putExtra("id", String.valueOf(products.get(position).get_id()));
                intent.putExtra("ref", String.valueOf(products.get(position).get_name()));
                intent.putExtra("size", String.valueOf(products.get(position).get_size()));
                intent.putExtra("color", String.valueOf(products.get(position).get_color()));
                intent.putExtra("type", products.get(position).get_type());
                intent.putExtra("image", products.get(position).get_image());
                activity.startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public int getRefCount(){
        ArrayList<String> refsList = new ArrayList<>();
        for (Product product : products){
            if(!refsList.contains(product.get_name())){
                refsList.add(product.get_name());
            }
        }

        return refsList.size();
    }

    public void filteredList(ArrayList<Product> filteredList) {

        products = filteredList;
        notifyDataSetChanged();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView name,color, size;
        private LinearLayout layout;
        private ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.mainLayout);
            name = itemView.findViewById(R.id.referenceTV);
            color = itemView.findViewById(R.id.colorTV);
            size = itemView.findViewById(R.id.sizeTv);
            image = itemView.findViewById(R.id.productImageTV);
            animation = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
            layout.setAnimation(animation);
        }
    }
}
