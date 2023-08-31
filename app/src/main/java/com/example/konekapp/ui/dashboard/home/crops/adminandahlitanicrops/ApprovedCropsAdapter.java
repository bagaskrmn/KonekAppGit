package com.example.konekapp.ui.dashboard.home.crops.adminandahlitanicrops;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.konekapp.R;
import com.example.konekapp.model.CropsModel;
import com.example.konekapp.model.UserModel;
import com.example.konekapp.ui.dashboard.home.consultation.chatroom.ChatRoomActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ApprovedCropsAdapter extends RecyclerView.Adapter<ApprovedCropsAdapter.MyViewHolder> {
    Context context;
    ArrayList<CropsModel> list;

    public ApprovedCropsAdapter(Context context, ArrayList<CropsModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ApprovedCropsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.reviewed_crops_card, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ApprovedCropsAdapter.MyViewHolder holder, int position) {
        CropsModel crops = list.get(position);

        holder.NameCropsMitra.setText(crops.name);
        holder.PeriodCropsMitra.setText(crops.period);
        holder.DateCropsMitra.setText(crops.date);

        holder.BtnDetailCropsMitra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ApprovedDetailMonitoringActivity.class);
                i.putExtra("Key", crops.cropsId);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView NameCropsMitra, PeriodCropsMitra, DateCropsMitra;
        Button BtnDetailCropsMitra;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            NameCropsMitra = itemView.findViewById(R.id.nameCropsMitra);
            PeriodCropsMitra = itemView.findViewById(R.id.periodCropsMitra);
            DateCropsMitra = itemView.findViewById(R.id.dateCropsMitra);
            BtnDetailCropsMitra = itemView.findViewById(R.id.btnDetailCropsMitra);
        }
    }
}
