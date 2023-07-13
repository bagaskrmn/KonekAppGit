package com.example.konekapp.ui.dashboard.manageuser.managemitra;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.konekapp.R;
import com.example.konekapp.model.UserModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ManageMitraAdapter extends RecyclerView.Adapter<ManageMitraAdapter.MyViewHolder> {
    Context context;
    ArrayList<UserModel> list;

    public ManageMitraAdapter(Context context, ArrayList<UserModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ManageMitraAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.manage_mitra_card, parent, false);
        return new ManageMitraAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageMitraAdapter.MyViewHolder holder, int position) {
        UserModel user = list.get(position);

        holder.ManageMitraName.setText(user.name);
        Picasso.get().load(user.image).into(holder.ManageMitraImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailManageMitra = new Intent(context, DetailManageMitra.class);
                detailManageMitra.putExtra("Key", user.userId);
                context.startActivity(detailManageMitra);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView ManageMitraName;
        CircleImageView ManageMitraImage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ManageMitraName = itemView.findViewById(R.id.manageMitraName);
            ManageMitraImage = itemView.findViewById(R.id.manageMitraImage);
        }
    }
}
