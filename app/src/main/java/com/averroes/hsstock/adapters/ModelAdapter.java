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

import com.averroes.hsstock.R;
import com.averroes.hsstock.activities.UpdatePositionActivity;
import com.averroes.hsstock.database.DBHandler;
import com.averroes.hsstock.models.Model;
import com.averroes.hsstock.models.Position;

import java.util.ArrayList;

public class ModelAdapter extends RecyclerView.Adapter<ModelAdapter.MyViewHolder> {

    private Context context;
    private Activity activity;
    ArrayList<Model> models;
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
        holder.typeTV.setText(models.get(position).get_type());
        holder.counterTV.setText(models.get(position).get_count());
        holder.colorsTV.setText(models.get(position).get_colors());
        if(!models.get(position).get_image().equals("") && Uri.parse(models.get(position).get_image()) != null) {
            holder.imageIV.setImageURI(Uri.parse(models.get(position).get_image()));
        }
        else
            holder.imageIV.setImageResource(R.drawable.ic_image_grey_48);
        /*
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdatePositionActivity.class);
                intent.putExtra("name", positions.get(position).get_name());
                intent.putExtra("references", positions.get(position).get_refs());
                activity.startActivityForResult(intent, 1);
            }
        });*/

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

        }
    }
}
