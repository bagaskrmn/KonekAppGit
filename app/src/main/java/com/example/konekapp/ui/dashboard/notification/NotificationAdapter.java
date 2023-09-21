package com.example.konekapp.ui.dashboard.notification;

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
import com.example.konekapp.model.NotificationModel;
import com.example.konekapp.ui.dashboard.home.article.DetailArtikelActivity;
import com.example.konekapp.ui.dashboard.home.crops.mitracrops.MitraCropsListActivity;
import com.example.konekapp.ui.dashboard.manageuser.managemitra.requestmitra.DetailManageMitra;
import com.example.konekapp.ui.dashboard.home.registermitra.RegisterMitraApprovedActivity;
import com.example.konekapp.ui.dashboard.home.registermitra.RegisterMitraDeclinedActivity;
import com.example.konekapp.ui.toregistmitra.ToRegistMitraActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {
    Context context;
    ArrayList<NotificationModel> list;

    public NotificationAdapter(Context context, ArrayList<NotificationModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NotificationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.notification_card, parent, false);
        return new NotificationAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.MyViewHolder holder, int position) {

        NotificationModel notification = list.get(position);

        holder.TitleNotification.setText(notification.title);
        Picasso.get().load(notification.image).into(holder.ImageNotification);
        holder.DescriptionNotification.setText(notification.description);
        holder.DateNotification.setText(notification.date.substring(0, 10));

        //notification for new article
        if (notification.kind==0) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, DetailArtikelActivity.class);
                    i.putExtra("Key", notification.targetId);
                    context.startActivity(i);
                }
            });
        }

        //notification for register to mitra
        if (notification.kind==1) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ToRegistMitraActivity.class);
                    context.startActivity(i);
                }
            });
        }

        if (notification.kind==2) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, RegisterMitraApprovedActivity.class);
                    context.startActivity(i);
                }
            });
        }

        if (notification.kind==3) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, RegisterMitraDeclinedActivity.class);
                    context.startActivity(i);
                }
            });
        }

        if (notification.kind==4) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, MitraCropsListActivity.class);
                    context.startActivity(i);
                }
            });
        }

        if (notification.kind==5) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, DetailManageMitra.class);
                    i.putExtra("Key", notification.targetId);
                    context.startActivity(i);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ImageNotification;
        TextView TitleNotification, DescriptionNotification, DateNotification;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ImageNotification = itemView.findViewById(R.id.imageNotification);
            TitleNotification = itemView.findViewById(R.id.titleNotification);
            DescriptionNotification = itemView.findViewById(R.id.descriptionNotification);
            DateNotification = itemView.findViewById(R.id.dateNotification);
        }
    }
}
