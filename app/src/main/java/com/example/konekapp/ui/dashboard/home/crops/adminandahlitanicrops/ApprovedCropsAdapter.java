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
                showBottomSheetDialog(crops);
            }
        });
    }
    private void showBottomSheetDialog(CropsModel crops) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.detail_crops_bs);
        DatabaseReference rootRef, usersRef, cropsRef;
        String currentUserId;
        FirebaseAuth firebaseAuth;
        FirebaseUser currentUser;


        TextView NameDetailCrops, PeriodDetailCrops, DateDetailCrops, QtyDetailCrops, LocationDetailCrops,
                FertilizerDetailCrops, ResultDetailCrops, NotesDetailCrops;

        ImageView DetailCropsClose;

        LinearLayout BtnDeleteDetailCrops, BtnEditDetailCrops, BtnChatDetailCrops;
        ConstraintLayout ConstraintDetailAdmin, ConstraintDetailAhliTani;
        Button BtnApproveDetailCrops;

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef=rootRef.child("users");
        cropsRef = rootRef.child("crops");


        ConstraintDetailAdmin = bottomSheetDialog.findViewById(R.id.constraintDetailAdmin);
        ConstraintDetailAhliTani =bottomSheetDialog.findViewById(R.id.constraintDetailAhliTani);

        BtnDeleteDetailCrops = bottomSheetDialog.findViewById(R.id.btnDeleteDetailCrops);
        BtnEditDetailCrops = bottomSheetDialog.findViewById(R.id.btnEditDetailCrops);
        BtnApproveDetailCrops = bottomSheetDialog.findViewById(R.id.btnApproveDetailCrops);
        BtnChatDetailCrops = bottomSheetDialog.findViewById(R.id.btnChatDetailCrops);

        NameDetailCrops = bottomSheetDialog.findViewById(R.id.nameDetailCrops);
        PeriodDetailCrops = bottomSheetDialog.findViewById(R.id.periodDetailCrops);
        DateDetailCrops = bottomSheetDialog.findViewById(R.id.dateDetailCrops);
        QtyDetailCrops =bottomSheetDialog.findViewById(R.id.qtyDetailCrops);
        LocationDetailCrops =bottomSheetDialog.findViewById(R.id.locationDetailCrops);
        FertilizerDetailCrops = bottomSheetDialog.findViewById(R.id.fertilizerDetailCrops);
        ResultDetailCrops = bottomSheetDialog.findViewById(R.id.resultDetailCrops);
        NotesDetailCrops = bottomSheetDialog.findViewById(R.id.notesDetailCrops);

        DetailCropsClose = bottomSheetDialog.findViewById(R.id.detailCropsClose);

        BtnApproveDetailCrops.setVisibility(View.GONE);

        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String role=snapshot.child("role").getValue().toString();
                if (role.equals("2")) {
                    ConstraintDetailAdmin.setVisibility(View.GONE);
                    ConstraintDetailAhliTani.setVisibility(View.VISIBLE);
                } else {
                    ConstraintDetailAdmin.setVisibility(View.VISIBLE);
                    ConstraintDetailAhliTani.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DetailCropsClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.cancel();
            }
        });


        //edit by admin
        BtnEditDetailCrops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, AdminEditMitraCrops.class);
                i.putExtra("CropsId", crops.cropsId);
                context.startActivity(i);
            }
        });

        //delete by admin
        BtnDeleteDetailCrops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropsRef.child(crops.cropsId).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    bottomSheetDialog.cancel();
                                    Toast.makeText(context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        BtnChatDetailCrops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserModel user = new UserModel();
                user.setUserId(crops.userId);
                user.setName(crops.name);
                user.setImage(crops.userImage);
                Intent i = new Intent(context, ChatRoomActivity.class);
                i.putExtra("user", user);
                context.startActivity(i);
            }
        });

        NameDetailCrops.setText(crops.name);
        PeriodDetailCrops.setText(crops.period);
        DateDetailCrops.setText(crops.date);
        QtyDetailCrops.setText(crops.qty);
        LocationDetailCrops.setText(crops.location);
        FertilizerDetailCrops.setText(crops.fertilizer);
        ResultDetailCrops.setText(crops.result);
        NotesDetailCrops.setText(crops.notes);

        bottomSheetDialog.show();
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
