package com.example.konekapp.ui.dashboard.manageuser.manageuser;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.konekapp.R;
import com.example.konekapp.model.CommodityCropsModel;
import com.example.konekapp.model.UserModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ManageUserAdapter extends RecyclerView.Adapter<ManageUserAdapter.MyViewHolder> {
    Context context;
    List<UserModel> filteredUser = new ArrayList<>();

    public ManageUserAdapter(Context context) {
        this.context = context;
    }

    public void setListUser(List<UserModel> listUser) {
        this.filteredUser = listUser;
        Log.d("CommodityCrops", "listCommodity: "+ listUser.size());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ManageUserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.manage_user_card, parent, false);
        return new ManageUserAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageUserAdapter.MyViewHolder holder, int position) {
        UserModel user = filteredUser.get(position);

        holder.ManageUserName.setText(user.name);
        Picasso.get().load(user.image).into(holder.ManageUserImage);

        if (user.role.equals("0")) {
            holder.ManageUserRole.setText("Pengguna");
        }
        if (user.role.equals("1")) {
            holder.ManageUserRole.setText("Mitra");
        }
        if (user.role.equals("2")) {
            holder.ManageUserRole.setText("Ahli Tani");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailManageMitra = new Intent(context, DetailManageUserActivity.class);
                detailManageMitra.putExtra("Key", user.userId);
                context.startActivity(detailManageMitra);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredUser.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView ManageUserName;
        CircleImageView ManageUserImage;
        TextView ManageUserRole;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ManageUserName = itemView.findViewById(R.id.manageUserName);
            ManageUserImage = itemView.findViewById(R.id.manageUserImage);
            ManageUserRole = itemView.findViewById(R.id.manageUserRole);
        }
    }
}
