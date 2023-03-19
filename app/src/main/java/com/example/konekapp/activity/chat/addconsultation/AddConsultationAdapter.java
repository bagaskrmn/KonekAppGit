package com.example.konekapp.activity.chat.addconsultation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.konekapp.R;
import com.example.konekapp.activity.chat.models.UserModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AddConsultationAdapter extends RecyclerView.Adapter<AddConsultationAdapter.ViewHolder> {

    private ArrayList<UserModel> listUserModels;
    private UserListener userListener;

    public AddConsultationAdapter(ArrayList<UserModel> listUserModels, UserListener userListener) {
        this.listUserModels = listUserModels;
        this.userListener = userListener;
    }

    @NonNull
    @Override
    public AddConsultationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mentor, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AddConsultationAdapter.ViewHolder holder, int position) {
        UserModel user = listUserModels.get(position);
        Picasso.get().load(user.getImage()).into(holder.ivProfile);
        holder.name.setText(user.getNama());
        if (user.getRole().equals("3")) {
            holder.title.setText("Ahli Tani sejak " + user.getBergabungPada());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userListener.onUserClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listUserModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfile;
        TextView name, title;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfile = itemView.findViewById(R.id.imageViewProfile);
            name = itemView.findViewById(R.id.textViewNamaMentor);
            title = itemView.findViewById(R.id.textViewTitleMentor);
        }
    }

}