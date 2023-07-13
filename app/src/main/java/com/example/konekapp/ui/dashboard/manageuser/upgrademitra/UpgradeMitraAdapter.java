package com.example.konekapp.ui.dashboard.manageuser.upgrademitra;

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
import com.example.konekapp.model.UserModel;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class UpgradeMitraAdapter extends RecyclerView.Adapter<UpgradeMitraAdapter.MyViewHolder> {
    Context context;
    ArrayList<UserModel> list;

    public UpgradeMitraAdapter(Context context, ArrayList<UserModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public UpgradeMitraAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.manage_mitra_card, parent, false);
        return new UpgradeMitraAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UpgradeMitraAdapter.MyViewHolder holder, int position) {
        UserModel user = list.get(position);

        holder.UpgradeMitraName.setText(user.name);
        Picasso.get().load(user.image).into(holder.UpgradeMitraImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailManageMitra = new Intent(context, DetailUpgradeMitraActivity.class);
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
        TextView UpgradeMitraName;
        ImageView UpgradeMitraImage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            UpgradeMitraImage =itemView.findViewById(R.id.manageMitraImage);
            UpgradeMitraName = itemView.findViewById(R.id.manageMitraName);
        }
    }
}
