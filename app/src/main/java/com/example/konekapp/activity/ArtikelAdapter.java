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

public class ArtikelAdapter extends RecyclerView.Adapter<ArtikelAdapter.MyViewHolder> {

    Context context;
    ArrayList<ArtikelModel> list;


    public ArtikelAdapter(Context context, ArrayList<ArtikelModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ArtikelAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.artikel_card, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtikelAdapter.MyViewHolder holder, int position) {
        ArtikelModel artikel = list.get(position);

        holder.TitleArtikel.setText(artikel.Title);
        Picasso.get().load(artikel.Image).into(holder.ImageArtikel);
        holder.SourceArtikel.setText(artikel.Source);
        holder.DateArtikel.setText(artikel.Date);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView TitleArtikel, SourceArtikel, DateArtikel;
        ImageView ImageArtikel;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            TitleArtikel = itemView.findViewById(R.id.titleArtikel);
            SourceArtikel = itemView.findViewById(R.id.sourceArtikel);
            DateArtikel = itemView.findViewById(R.id.dateArtikel);
            ImageArtikel = itemView.findViewById(R.id.imageArtikel);
        }
    }
}
