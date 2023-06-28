package com.example.konekapp.ui.dashboard.home.crops.mitracrops;

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
import com.example.konekapp.ui.dashboard.home.crops.adminandahlitanicrops.ApprovedCropsAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MitraCropsListAdapter extends RecyclerView.Adapter<MitraCropsListAdapter.MyViewHolder> {
    Context context;
    ArrayList<CropsModel> list;

    public MitraCropsListAdapter(Context context, ArrayList<CropsModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MitraCropsListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.mitra_crops_list_card, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MitraCropsListAdapter.MyViewHolder holder, int position) {
        CropsModel crops = list.get(position);

        holder.CommodityCropsMitraList.setText(crops.commodity);
        holder.PeriodCropsMitraList.setText(crops.period);
        holder.DateCropsMitraList.setText(crops.date);

        holder.BtnDetailCropsMitraList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, MitraCropsStatusActivity.class);
                i.putExtra("CropsId", crops.cropsId);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView CommodityCropsMitraList, PeriodCropsMitraList, DateCropsMitraList;
        Button BtnDetailCropsMitraList;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            CommodityCropsMitraList = itemView.findViewById(R.id.commodityCropsMitraList);
            PeriodCropsMitraList = itemView.findViewById(R.id.periodCropsMitraList);
            DateCropsMitraList = itemView.findViewById(R.id.dateCropsMitraList);
            BtnDetailCropsMitraList = itemView.findViewById(R.id.btnDetailCropsMitraList);
        }
    }
}
