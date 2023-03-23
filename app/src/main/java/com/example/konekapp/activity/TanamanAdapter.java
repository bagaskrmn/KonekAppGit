package com.example.konekapp.activity;

import android.content.Context;
import android.media.Image;
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

public class TanamanAdapter extends RecyclerView.Adapter<TanamanAdapter.MyViewHolder> {
    Context context;
    ArrayList<TanamanModel> list;

    public TanamanAdapter(Context context, ArrayList<TanamanModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public TanamanAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.tanaman_card, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TanamanAdapter.MyViewHolder holder, int position) {
        TanamanModel tanaman = list.get(position);

        holder.NameTanamanTv.setText(tanaman.Name);
        Picasso.get().load(tanaman.Image).into(holder.ImageTanamanImg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //give action if item selected
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView NameTanamanTv;
        ImageView ImageTanamanImg;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            NameTanamanTv = itemView.findViewById(R.id.nameTanaman);
            ImageTanamanImg = itemView.findViewById(R.id.imageTanaman);
        }
    }
}
