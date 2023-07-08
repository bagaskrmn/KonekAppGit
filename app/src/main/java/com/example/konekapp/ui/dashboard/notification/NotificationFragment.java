package com.example.konekapp.ui.dashboard.notification;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.konekapp.R;
import com.example.konekapp.model.NotificationModel;
import com.example.konekapp.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class NotificationFragment extends Fragment {

    private NotificationAdapter adapter;
    private RecyclerView RecyclerViewNotification;
    private ArrayList<NotificationModel> list;
    private View decorView;
    private ProgressDialog pd;
    private DatabaseReference usersRef, rootRef, notificationRef;
    private String currentUserId, notificationId;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private TextView TvNoNotification;
    private String role, dateNTimeUser;

    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("users");
        notificationRef = rootRef.child("notification");
        TvNoNotification = (TextView) getView().findViewById(R.id.tvNoNotification);

        RecyclerViewNotification = (RecyclerView) getView().findViewById(R.id.recyclerViewNotification);
        list = new ArrayList<>();
        RecyclerViewNotification.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificationAdapter(getContext(), list);
        RecyclerViewNotification.setAdapter(adapter);

        sharedPreferences = requireActivity().getSharedPreferences("notifPreferences", Context.MODE_PRIVATE);

        //init ProgressDialog
        pd = new ProgressDialog(getActivity());
        pd.setTitle("Please Wait...");
        pd.setCanceledOnTouchOutside(false);

        pd.setMessage("Memuat data anda");
        pd.show();
        usersRef.child(currentUserId).addValueEventListener(userListener);

        Log.d("Notification", "role: "+role);

    }

    ValueEventListener notifListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            list.clear();
            for (DataSnapshot ds : snapshot.getChildren()) {
                NotificationModel notification = ds.getValue(NotificationModel.class);
                notification.setKey(ds.getKey());
                //ini fungsinya buat apa

                //userDate, notifArticleDate
                try {
//
                    if (notification.getKind() ==0 && dateNTimeUser.compareTo(notification.getDate()) >0 ) {
                        list.add(notification);
                    }
                    if (role.equals("0") && notification.getTargetId().equals(currentUserId) && notification.getKind()==1) {
                        list.add(notification);
                    }
                    if (notification.getTargetId().equals(currentUserId) && notification.getKind()==2) {
                        list.add(notification);
                    }
                    if (notification.getTargetId().equals(currentUserId) && notification.getKind()==3) {
                        list.add(notification);
                    }
                    if (notification.getTargetId().equals(currentUserId) && notification.getKind()==4) {
                        list.add(notification);
                    }
                    if (role.equals("3") && notification.getKind()==5){
                        list.add(notification);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            RecyclerViewNotification.getAdapter().notifyDataSetChanged();

            if (list.size() > 0) {
                RecyclerViewNotification.setVisibility(View.VISIBLE);
                TvNoNotification.setVisibility(View.GONE);
            } else {
                RecyclerViewNotification.setVisibility(View.GONE);
                TvNoNotification.setVisibility(View.VISIBLE);
            }

            //edit sharedPreferences
            //set recentNotifCount == newest list.size
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("notifCount", list.size());
            editor.apply();
            Log.d("NotifFragment", "int in sharedPreferences: "+sharedPreferences.getInt("notifCount", 0));


            //sorting list
            Collections.sort(list, (obj1, obj2) -> obj2.getDate().compareTo(obj1.getDate()));
            pd.dismiss();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    ValueEventListener userListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            role= snapshot.child("role").getValue().toString();
            dateNTimeUser=snapshot.child("dateJoined").getValue().toString();

            notificationRef.addValueEventListener(notifListener);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        usersRef.child(currentUserId).removeEventListener(userListener);
        notificationRef.removeEventListener(notifListener);
    }
}