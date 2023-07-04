package com.example.konekapp.ui.dashboard.home.plant;

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
import com.example.konekapp.model.TanamanModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TanamanAdapter extends RecyclerView.Adapter<TanamanAdapter.MyViewHolder> {
    Context context;
    List<TanamanModel> filteredTanaman = new ArrayList<>();

    public TanamanAdapter(Context context) {
        this.context = context;
//        this.list = list;
    }

    public void setListTanaman(List<TanamanModel> listTanaman) {
        this.filteredTanaman = listTanaman;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TanamanAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.tanaman_card, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TanamanAdapter.MyViewHolder holder, int position) {
        TanamanModel tanaman = filteredTanaman.get(position);

        holder.NameTanamanTv.setText(tanaman.name);
        Picasso.get().load(tanaman.image).into(holder.ImageTanamanImg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailTanaman = new Intent(context, PenyakitDanObatActivity.class);
                detailTanaman.putExtra("Key", tanaman.key);
                context.startActivity(detailTanaman);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredTanaman.size();
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
