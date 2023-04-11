package com.example.konekapp.ui.dashboard.notification;

import android.content.Context;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.konekapp.R;
import com.example.konekapp.model.NotificationModel;
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
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.MyViewHolder holder, int position) {

        NotificationModel notification = list.get(position);

        holder.TitleNotification.setText(notification.title);
        Picasso.get().load(notification.image).into(holder.ImageNotification);
        holder.DescriptionNotification.setText(notification.description);
        holder.DateNotification.setText(notification.date);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if item clicked
            }
        });

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
