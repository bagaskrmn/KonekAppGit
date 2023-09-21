package com.example.konekapp.ui.dashboard.manageuser.manageAhliTani.listAhliTani;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.konekapp.R;
import com.example.konekapp.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListAhliTaniAdapter extends RecyclerView.Adapter<ListAhliTaniAdapter.MyViewHolder> {
    Context context;
    List<UserModel> filteredUser = new ArrayList<>();

    public ListAhliTaniAdapter(Context context) {this.context = context;}

    public void setListUser(List<UserModel> listUser) {
        this.filteredUser = listUser;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListAhliTaniAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_ahli_tani_card, parent, false);
        return new ListAhliTaniAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAhliTaniAdapter.MyViewHolder holder, int position) {
        UserModel user = filteredUser.get(position);

        holder.ListAhliTaniName.setText(user.name);
        Picasso.get().load(user.image).into(holder.ListAhliTaniImage);
        holder.BtnDowngrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog(user);
            }
        });
    }

    private void confirmDialog(UserModel user) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.ahli_tani_downgrade_confirm_bs);

        ImageView CloseBs;
        Button BtnConfirm, BtnCancel;
        FirebaseAuth firebaseAuth;
        DatabaseReference rootRef, usersRef;

        CloseBs = bottomSheetDialog.findViewById(R.id.closeBs);
        BtnConfirm = bottomSheetDialog.findViewById(R.id.btnConfirm);
//        BtnCancel = bottomSheetDialog.findViewById(R.id.btnCancel);

        firebaseAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef=rootRef.child("users");

        CloseBs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.cancel();
            }
        });

        BtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> userMap = new HashMap<>();
                userMap.put("role", "0");

                usersRef.child(user.userId).updateChildren(userMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Pengubahan berhasil", Toast.LENGTH_SHORT).show();
                                    bottomSheetDialog.cancel();
                                }
                                else {
                                    Toast.makeText(context, "Gagal", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
//
//        BtnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bottomSheetDialog.cancel();
//            }
//        });

        bottomSheetDialog.show();
    }

    @Override
    public int getItemCount() {
        return filteredUser.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView ListAhliTaniName;
        CircleImageView ListAhliTaniImage;
        LinearLayout BtnDowngrade;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ListAhliTaniImage = itemView.findViewById(R.id.listAhliTaniImage);
            ListAhliTaniName = itemView.findViewById(R.id.listAhliTaniName);
            BtnDowngrade = itemView.findViewById(R.id.btnDowngrade);
        }
    }
}
