package com.example.konekapp.ui.dashboard.manageuser.manageuser;

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
import com.example.konekapp.ui.dashboard.manageuser.managemitra.DetailManageMitra;
import com.example.konekapp.ui.dashboard.manageuser.managemitra.ManageMitraAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ManageUserAdapter extends RecyclerView.Adapter<ManageUserAdapter.MyViewHolder> {
    Context context;
    ArrayList<UserModel> list;

    public ManageUserAdapter(Context context, ArrayList<UserModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ManageUserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.manage_mitra_card, parent, false);
        return new ManageUserAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageUserAdapter.MyViewHolder holder, int position) {
        UserModel user = list.get(position);

        holder.ManageUserName.setText(user.name);
        Picasso.get().load(user.image).into(holder.ManageUserImage);

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
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView ManageUserName;
        CircleImageView ManageUserImage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ManageUserName = itemView.findViewById(R.id.manageMitraName);
            ManageUserImage = itemView.findViewById(R.id.manageMitraImage);
        }
    }
}
