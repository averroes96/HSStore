package com.averroes.hsstock.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.averroes.hsstock.R;
import com.averroes.hsstock.activities.UpdateModelActivity;
import com.averroes.hsstock.database.DBHandler;
import com.averroes.hsstock.models.Model;
import com.averroes.hsstock.models.Product;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ModelAdapter extends RecyclerView.Adapter<ModelAdapter.MyViewHolder> {

    private Context context;
    private Activity activity;
    ArrayList<Model> models;
    ArrayList<Product> modelProducts;
    CustomAdapter adapter;
    private DBHandler dbHandler;

    private Animation animation;


    public ModelAdapter(Context context, Activity activity, ArrayList<Model> models){
        this.context = context;
        this.models = models;
        this.activity = activity;
        dbHandler = new DBHandler(activity);
    }


    @NonNull
    @Override
    public ModelAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.model_row, parent, false);
        return new ModelAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModelAdapter.MyViewHolder holder, final int position) {

        holder.referenceTV.setText(models.get(position).get_name());
        if(models.get(position).get_type().equals(""))
            holder.typeTV.setText(activity.getString(R.string.not_defined));
        else
            holder.typeTV.setText(models.get(position).get_type());
        holder.counterTV.setText(models.get(position).get_count() + " " + activity.getString(R.string.piece_s) + " |");
        holder.colorsTV.setText(models.get(position).get_colors());
        try{
            Picasso.get().load(models.get(position).get_image()).placeholder(R.drawable.ic_image_grey_48).into(holder.imageIV);
        }catch(Exception e){
            holder.imageIV.setImageResource(R.drawable.ic_image_grey_48);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showModel(models.get(position), position);
            }
        });

    }

    private void showModel(final Model model, final int i) {
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        final View v = LayoutInflater.from(activity).inflate(R.layout.model_layout, null);
        dialog.setContentView(v);

        ImageButton backBtn,editBtn,deleteBtn;
        ImageView imageIV;
        TextView colorsTV,sizesTV,counterTV, nameTV;
        RecyclerView productsRV;

        backBtn = v.findViewById(R.id.backBtn);
        imageIV = v.findViewById(R.id.imageIV);
        colorsTV = v.findViewById(R.id.colorsTV);
        sizesTV = v.findViewById(R.id.sizesTV);
        counterTV = v.findViewById(R.id.counterTV);
        productsRV = v.findViewById(R.id.productsRV);
        nameTV = v.findViewById(R.id.nameTV);
        editBtn = v.findViewById(R.id.editBtn);
        deleteBtn = v.findViewById(R.id.deleteBtn);

        dialog.show();

        try{
            Picasso.get().load(model.get_image()).placeholder(R.drawable.ic_image_grey_48).into(imageIV);
        }catch(Exception e){
            imageIV.setImageResource(R.drawable.ic_image_grey_48);
        }

        nameTV.setText(model.get_name());
        colorsTV.setText(dbHandler.getModelColorsWithCount(model.get_name()).replace(",", " "));
        sizesTV.setText(dbHandler.getModelSizes(model.get_name()).replace(",", " "));
        counterTV.setText(model.get_count());

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdateModelActivity.class);
                intent.putExtra("name", model.get_name());
                intent.putExtra("type", model.get_type());
                intent.putExtra("image", model.get_image());
                intent.putExtra("colors", dbHandler.getModelColors(model.get_name()).split(","));
                activity.startActivityForResult(intent, 1);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog(i);
            }

            private void confirmDialog(final int i) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                String title = activity.getString(R.string.delete) + " " + model.get_name();
                String message = activity.getString(R.string.delete_confirm_msg) + model.get_name() + activity.getString(R.string.interogation_mark);
                builder.setTitle(title);
                builder.setMessage(message);
                builder.setPositiveButton(activity.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteProduct();
                        dialog.dismiss();
                    }

                    private void deleteProduct() {
                        dbHandler.deleteProductByNameAndType(model.get_name(), model.get_type());
                        activity.recreate();
                    }
                });
                builder.setNegativeButton(activity.getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.create().show();
            }

        });

        storeData(model);

        adapter = new CustomAdapter(v.getContext(), activity, modelProducts);
        productsRV.setAdapter(adapter);
        productsRV.setLayoutManager(new LinearLayoutManager(v.getContext()));
        adapter.notifyDataSetChanged();
    }

    private void storeData(Model model) {

        Cursor cursor = dbHandler.getResults(" WHERE name = '" + model.get_name() + "' AND type = '" + model.get_type() + "' AND sold = ?");

        if(cursor != null){

                modelProducts.clear();

                while(cursor.moveToNext()){
                    Product product = new Product();
                    product.set_id(cursor.getInt(0));
                    product.set_name(cursor.getString(1));
                    product.set_color(cursor.getString(3));
                    product.set_size(cursor.getInt(2));
                    product.set_image(cursor.getString(4));
                    product.set_type(cursor.getString(5));
                    modelProducts.add(product);
                }

        }
        else
            Toast.makeText(activity, "Erreur de la base de données", Toast.LENGTH_LONG).show();
    }

    @Override
    public int getItemCount() {
        return models.size();
    }


    public void filteredList(ArrayList<Model> filteredList) {
        models = filteredList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageIV;
        private TextView referenceTV,colorsTV,counterTV,typeTV;
        private LinearLayout layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.mainLayout);
            imageIV = itemView.findViewById(R.id.imageIV);
            referenceTV = itemView.findViewById(R.id.referenceTV);
            colorsTV = itemView.findViewById(R.id.colorsTV);
            counterTV = itemView.findViewById(R.id.counterTV);
            typeTV = itemView.findViewById(R.id.typeTV);
            animation = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
            layout.setAnimation(animation);

            modelProducts = new ArrayList<>();

        }
    }
}
