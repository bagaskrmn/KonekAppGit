package com.example.konekapp.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.konekapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PenyakitAdapter extends RecyclerView.Adapter<PenyakitAdapter.MyViewHolder> {

    Context context;
    ArrayList<PenyakitModel> list;

    public PenyakitAdapter(Context context, ArrayList<PenyakitModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PenyakitAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.penyakit_card, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PenyakitAdapter.MyViewHolder holder, int position) {
        PenyakitModel penyakit = list.get(position);

        holder.DetailPenyakitName.setText(penyakit.Name);
        Picasso.get().load(penyakit.Image).into(holder.DetailPenyakitImage);
        holder.DetailPenyakitDescription.setText(penyakit.Description);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView DetailPenyakitImage;
        TextView DetailPenyakitName, DetailPenyakitDescription;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            DetailPenyakitImage = itemView.findViewById(R.id.detailPenyakitImage);
            DetailPenyakitName = itemView.findViewById(R.id.detailPenyakitName);
            DetailPenyakitDescription= itemView.findViewById(R.id.detailPenyakitDescription);
        }
    }
}
