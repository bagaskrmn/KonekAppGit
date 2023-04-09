package com.example.konekapp.ui.dashboard.home.plant.drug;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.konekapp.R;
import com.example.konekapp.model.ObatModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ObatAdapter extends RecyclerView.Adapter<ObatAdapter.MyViewHolder> {

    Context context;
    ArrayList<ObatModel> list;

    public ObatAdapter(Context context, ArrayList<ObatModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ObatAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.obat_card, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ObatAdapter.MyViewHolder holder, int position) {
        ObatModel obat = list.get(position);

        holder.DetailObatName.setText(obat.name);
        Picasso.get().load(obat.image).into(holder.DetailObatImage);
        holder.DetailObatDescription.setText(obat.description);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView DetailObatImage;
        TextView DetailObatName, DetailObatDescription;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            DetailObatImage = itemView.findViewById(R.id.detailObatImage);
            DetailObatName = itemView.findViewById(R.id.detailObatName);
            DetailObatDescription = itemView.findViewById(R.id.detailObatDescription);

        }
    }
}
