package com.example.konekapp.ui.dashboard.home.crops.adminandahlitanicrops;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.konekapp.R;
import com.example.konekapp.model.CropsModel;

import java.util.ArrayList;

public class ReviewedCropsAdapter extends RecyclerView.Adapter<ReviewedCropsAdapter.MyViewHolder> {
    Context context;
    ArrayList<CropsModel> list;

    public ReviewedCropsAdapter(Context context, ArrayList<CropsModel> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public ReviewedCropsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.reviewed_crops_card, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewedCropsAdapter.MyViewHolder holder, int position) {
        CropsModel crops = list.get(position);

        holder.NameCropsMitra.setText(crops.name);
        holder.PeriodCropsMitra.setText(crops.period);
        holder.DateCropsMitra.setText(crops.date);

        holder.BtnDetailCropsMitra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, CropsDetail.class);
                i.putExtra("CropsId", crops.cropsId);
                i.putExtra("Commodity", crops.commodity);
                context.startActivity(i);
//                ((Activity)context).finish();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView NameCropsMitra, PeriodCropsMitra, DateCropsMitra;
        Button BtnDetailCropsMitra;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            NameCropsMitra = itemView.findViewById(R.id.nameCropsMitra);
            PeriodCropsMitra = itemView.findViewById(R.id.periodCropsMitra);
            DateCropsMitra = itemView.findViewById(R.id.dateCropsMitra);
            BtnDetailCropsMitra = itemView.findViewById(R.id.btnDetailCropsMitra);
        }
    }
}
