package com.example.konekapp.ui.dashboard.home.crops.adminandahlitanicrops;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.konekapp.R;
import com.example.konekapp.model.CommodityCropsModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CommodityCropsAdapter extends RecyclerView.Adapter<CommodityCropsAdapter.MyViewHolder> {
    Context context;
    List<CommodityCropsModel> filteredCommodity = new ArrayList<>();
//    String role;

    public CommodityCropsAdapter(Context context) {
        this.context = context;
    }


    public void setListCommodity(List<CommodityCropsModel> listCommodity) {
        this.filteredCommodity = listCommodity;
        Log.d("CommodityCrops", "listCommodity: "+ listCommodity.size());
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public CommodityCropsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.commodity_crops_card, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommodityCropsAdapter.MyViewHolder holder, int position) {
        CommodityCropsModel commodity = filteredCommodity.get(position);

        holder.NameCommodityCrops.setText(commodity.name);
        Picasso.get().load(commodity.image).into(holder.ImageCommodityCrops);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent i = new Intent(context, AhliTaniAndAdminCropsActivity.class);
                    //pass value with Commodity String
                    i.putExtra("Commodity", commodity.name);
                    context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredCommodity.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView NameCommodityCrops;
        ImageView ImageCommodityCrops;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            NameCommodityCrops = itemView.findViewById(R.id.nameCommodityCrops);
            ImageCommodityCrops = itemView.findViewById(R.id.imageCommodityCrops);
        }
    }
}
