package com.example.konekapp.ui.dashboard.home.crops;

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
import com.example.konekapp.ui.dashboard.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CommodityCropsAdapter extends RecyclerView.Adapter<CommodityCropsAdapter.MyViewHolder> {
    Context context;
    List<CommodityCropsModel> list = new ArrayList<>();
    String role;

    public CommodityCropsAdapter(Context context) {
        this.context = context;
    }


    public void setListCommodity(List<CommodityCropsModel> listCommodity) {
        this.list = listCommodity;
        Log.d("CommodityCrops", "listCommodity: "+ listCommodity.size());
        notifyDataSetChanged();
    }
    public void setRoleUser(String roleUser) {
        this.role = roleUser;
        Log.d("CommodityCrops", "RoleUser: " + role);
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
        CommodityCropsModel commodity = list.get(position);

        holder.NameCommodityCrops.setText(commodity.name);
        Picasso.get().load(commodity.image).into(holder.ImageCommodityCrops);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (role.equals("2")) {
                    Intent i = new Intent(context, AhliTaniCropsActivity.class);
                    //pass value with Commodity String
                    i.putExtra("Commodity", commodity.name);
                    context.startActivity(i);
                }
                else {
                    Intent i = new Intent(context, MainActivity.class);
                    //pass value with Commodity String
                    i.putExtra("Commodity", commodity.name);
                    context.startActivity(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
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
