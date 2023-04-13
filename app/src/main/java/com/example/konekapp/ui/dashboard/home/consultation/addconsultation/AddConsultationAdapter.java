package com.example.konekapp.ui.dashboard.home.consultation.addconsultation;

import android.util.Log;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddConsultationAdapter extends RecyclerView.Adapter<AddConsultationAdapter.ViewHolder> {

    private ArrayList<UserModel> listUserModels = new ArrayList<>();
    private UserListener userListener;

    public void setListUserModels(List<UserModel> listUserModels) {
        this.listUserModels.clear();
        this.listUserModels.addAll(listUserModels);
        notifyDataSetChanged();
    }

    public AddConsultationAdapter(UserListener userListener) {
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
        holder.name.setText(user.getName());
        if (user.getRole().equals("2")) {
            try {
                holder.title.setText("Ahli Tani sejak " + getMonthAndYear(user.getDateJoined()));
            } catch (ParseException e) {
                Log.d(AddConsultationAdapter.class.getSimpleName(), "onBindViewHolder: " + e.getMessage());
            }
        } else if (user.getRole().equals("1")) {
            try {
                holder.title.setText("Mitra Tani sejak " + getMonthAndYear(user.getDateJoined()));
            } catch (ParseException e) {
                Log.d(AddConsultationAdapter.class.getSimpleName(), "onBindViewHolder: " + e.getMessage());
            }
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

    private static String getMonthAndYear(String stringDate) throws ParseException {
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("id", "ID"));
        Date date = originalFormat.parse(stringDate);

        SimpleDateFormat targetFormat = new SimpleDateFormat("MMMM yyyy", new Locale("id", "ID"));
        String targetDateString = targetFormat.format(date);

        return targetDateString;
    }
}
