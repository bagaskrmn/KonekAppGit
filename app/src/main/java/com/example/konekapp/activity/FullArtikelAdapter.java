package com.example.konekapp.activity;

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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FullArtikelAdapter extends RecyclerView.Adapter<FullArtikelAdapter.MyViewHolder> {
    Context context;
    ArrayList<ArtikelModel> list;

    public FullArtikelAdapter(Context context, ArrayList<ArtikelModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public FullArtikelAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.full_artikel_card, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FullArtikelAdapter.MyViewHolder holder, int position) {
        ArtikelModel artikel = list.get(position);

        Picasso.get().load(artikel.Image).into(holder.FullImageArtikel);
        holder.FullTitleArtikel.setText(artikel.Title);
        holder.FullSourceArtikel.setText(artikel.Source);
        holder.FullDateArtikel.setText(artikel.Date);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailArtikelIntent = new Intent(context, DetailArtikelActivity.class);
                detailArtikelIntent.putExtra("Key", artikel.Key);
                context.startActivity(detailArtikelIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView FullTitleArtikel, FullSourceArtikel, FullDateArtikel;
        ImageView FullImageArtikel;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            FullTitleArtikel = itemView.findViewById(R.id.fullTitleArtikel);
            FullSourceArtikel = itemView.findViewById(R.id.fullSourceArtikel);
            FullDateArtikel = itemView.findViewById(R.id.fullDateArtikel);
            FullImageArtikel = itemView.findViewById(R.id.fullImageArtikel);

        }
    }
}
