package com.example.konekapp.ui.dashboard.home.plant.disease;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.konekapp.R;
import com.example.konekapp.model.PenyakitModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PenyakitAdapter extends RecyclerView.Adapter<PenyakitAdapter.MyViewHolder> {

    Context context;
    ArrayList<PenyakitModel> list;
    String plantId, role;

    public PenyakitAdapter(Context context, ArrayList<PenyakitModel> list) {
        this.context = context;
        this.list = list;
    }

    public void setPlantId(String plantId) {
        this.plantId = plantId;
        notifyDataSetChanged();
    }

    public void setRole(String role){
        this.role=role;
    }

    @NonNull
    @Override
    public PenyakitAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.penyakit_card, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PenyakitAdapter.MyViewHolder holder, int position) {
        PenyakitModel penyakit = list.get(position);

        holder.DetailPenyakitName.setText(penyakit.name);
        Picasso.get().load(penyakit.image).into(holder.DetailPenyakitImage);
        holder.DetailPenyakitDescription.setText(penyakit.description);

        if (role.equals("2") || role.equals("3")) {
            holder.BtnDeletePenyakit.setVisibility(View.VISIBLE);
        } else {
            holder.BtnDeletePenyakit.setVisibility(View.GONE);
        }

        holder.BtnDeletePenyakit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.delete_item, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.deleteItem:
                                deletePenyakit();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
            private void deletePenyakit() {
                DatabaseReference rootRef, plantRef, diseaseRef;
                rootRef = FirebaseDatabase.getInstance().getReference();
                plantRef = rootRef.child("plant");
                diseaseRef=plantRef.child(plantId).child("disease");

                diseaseRef.child(penyakit.key).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Item berhasil dihapus", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Error :", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView DetailPenyakitImage;
        TextView DetailPenyakitName, DetailPenyakitDescription;
        ImageView BtnDeletePenyakit;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            DetailPenyakitImage = itemView.findViewById(R.id.detailPenyakitImage);
            DetailPenyakitName = itemView.findViewById(R.id.detailPenyakitName);
            DetailPenyakitDescription= itemView.findViewById(R.id.detailPenyakitDescription);
            BtnDeletePenyakit = itemView.findViewById(R.id.btnDeletePenyakit);
        }
    }
}
