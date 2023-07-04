package com.example.konekapp.ui.dashboard.home.plant.drug;

import android.content.Context;
import android.util.Log;
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
import com.example.konekapp.model.CommodityCropsModel;
import com.example.konekapp.model.ObatModel;
import com.example.konekapp.ui.dashboard.home.crops.mitracrops.MitraCropsListActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ObatAdapter extends RecyclerView.Adapter<ObatAdapter.MyViewHolder> {

    Context context;
    ArrayList<ObatModel> list;
    String plantId, role;

    public ObatAdapter(Context context, ArrayList<ObatModel> list) {
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
    public ObatAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.obat_card, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ObatAdapter.MyViewHolder holder, int position) {
        ObatModel obat = list.get(position);

        holder.DetailObatName.setText(obat.name);
        Picasso.get().load(obat.image).into(holder.DetailObatImage);
        holder.DetailObatDescription.setText(obat.description);

        if (role.equals("2") || role.equals("3")) {
            holder.BtnDeleteObat.setVisibility(View.VISIBLE);
        } else {
            holder.BtnDeleteObat.setVisibility(View.GONE);
        }

        holder.BtnDeleteObat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.delete_item, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.deleteItem:
                                deleteObat();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }

            private void deleteObat() {
                DatabaseReference rootRef, plantRef, drugRef;
                rootRef = FirebaseDatabase.getInstance().getReference();
                plantRef = rootRef.child("plant");
                drugRef=plantRef.child(plantId).child("drug");

                drugRef.child(obat.key).removeValue()
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
        ImageView DetailObatImage;
        TextView DetailObatName, DetailObatDescription;
        ImageView BtnDeleteObat;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            DetailObatImage = itemView.findViewById(R.id.detailObatImage);
            DetailObatName = itemView.findViewById(R.id.detailObatName);
            DetailObatDescription = itemView.findViewById(R.id.detailObatDescription);
            BtnDeleteObat = itemView.findViewById(R.id.btnDeleteObat);

        }
    }
}
