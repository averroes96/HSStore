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
import com.averroes.hsstock.activities.UpdatePositionActivity;
import com.averroes.hsstock.database.DBHandler;
import com.averroes.hsstock.models.Position;

import java.util.ArrayList;

public class PositionAdapter extends RecyclerView.Adapter<PositionAdapter.MyViewHolder> {

    private Context context;
    private Activity activity;
    ArrayList<Position> positions;
    private DBHandler dbHandler;

    private Animation animation;



    public PositionAdapter(Context context, Activity activity, ArrayList positions){
        this.context = context;
        this.positions = positions;
        this.activity = activity;
        dbHandler = new DBHandler(activity);
    }


    @NonNull
    @Override
    public PositionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.position_row, parent, false);
        return new PositionAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PositionAdapter.MyViewHolder holder, final int position) {
        holder.nameTV.setText(positions.get(position).get_name());
        String refCount = positions.get(position).get_num_refs() + " " + holder.itemView.getResources().getString(R.string.references2);
        holder.refsTV.setText(refCount);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdatePositionActivity.class);
                intent.putExtra("name", positions.get(position).get_name());
                intent.putExtra("references", positions.get(position).get_refs());
                activity.startActivityForResult(intent, 1);
            }
        });

    }

    @Override
    public int getItemCount() {
        return positions.size();
    }


    public void filteredList(ArrayList<Position> filteredList) {
        positions = filteredList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTV,refsTV;
        private LinearLayout layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.mainLayout);
            nameTV = itemView.findViewById(R.id.nameTV);
            refsTV = itemView.findViewById(R.id.refsTV);
            animation = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
            layout.setAnimation(animation);

        }
    }
}
