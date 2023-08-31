package com.example.konekapp.ui.dashboard.home.crops.adminandahlitanicrops;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
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
import com.example.konekapp.model.NotificationModel;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ReviewedCropsAdapter extends RecyclerView.Adapter<ReviewedCropsAdapter.MyViewHolder> {
    Context context;
    ArrayList<CropsModel> list;

    public ReviewedCropsAdapter(Context context, ArrayList<CropsModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ReviewedCropsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.reviewed_crops_card, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewedCropsAdapter.MyViewHolder holder, int position) {
        CropsModel crops = list.get(position);

        holder.NameCropsMitra.setText(crops.name);
        holder.PeriodCropsMitra.setText(crops.period);
        holder.DateCropsMitra.setText(crops.date);

        holder.BtnDetailCropsMitra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ReviewedDetailMonitoringActivity.class);
                i.putExtra("Key", crops.cropsId);
                context.startActivity(i);
            }
        });
    }
    private void showBottomSheetDialog(CropsModel crops) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.detail_crops_bs);

        DatabaseReference rootRef, usersRef, cropsRef, notificationRef;
        String currentUserId, notificationKey;
        FirebaseAuth firebaseAuth;
        FirebaseUser currentUser;

        Calendar calendar;
        SimpleDateFormat dateFormat;
        String date;

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
        cropsRef=rootRef.child("crops");
        notificationRef = rootRef.child("notification");
        notificationKey=rootRef.push().getKey();


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

        //calendar
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        date = dateFormat.format(calendar.getTime());

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

        //approve by ahli tani
        BtnApproveDetailCrops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> cropsMaps = new HashMap<>();
                cropsMaps.put("status", 1);

                cropsRef.child(crops.cropsId).updateChildren(cropsMaps)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    notifCropsToMitra();
                                    bottomSheetDialog.cancel();
                                    Toast.makeText(context, "Data Disetujui", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    String message = task.getException().toString();
                                    Toast.makeText(context, "Error: "+message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }

            private void notifCropsToMitra() {
                String targetId = crops.userId;
                String title = "Data Monitoring Disetujui";
                String description = "Data monitoring hasil panen anda telah disetujui";
                int kind = 4;

                StorageReference systemNotificationImageRef;
                systemNotificationImageRef = FirebaseStorage.getInstance().getReference().child("systemNotificationImage").child("konek_icon.png");
                systemNotificationImageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        String systemNotificationImageUrl = task.getResult().toString();
                        NotificationModel notificationModel = new NotificationModel(title, description, targetId, kind, date, systemNotificationImageUrl);

                        notificationRef.child(notificationKey).setValue(notificationModel)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        //
                                    }
                                });
                    }
                });


            }
        });

        //edit by admin
        BtnEditDetailCrops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.cancel();
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
