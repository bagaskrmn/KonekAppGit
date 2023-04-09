package com.example.konekapp.ui.dashboard.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.konekapp.R;
import com.example.konekapp.model.ArtikelModel;
import com.example.konekapp.ui.dashboard.home.article.DetailArtikelActivity;
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

        holder.TitleArtikel.setText(artikel.title);
        Picasso.get().load(artikel.image).into(holder.ImageArtikel);
        holder.SourceArtikel.setText(artikel.source);
        holder.DateArtikel.setText(artikel.date);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailArtikelIntent = new Intent(context, DetailArtikelActivity.class);
                detailArtikelIntent.putExtra("Key", artikel.key);
                context.startActivity(detailArtikelIntent);
            }
        });
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