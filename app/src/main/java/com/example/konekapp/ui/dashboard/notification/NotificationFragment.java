package com.example.konekapp.ui.dashboard.notification;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private String role;

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

        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                role = snapshot.child("role").getValue().toString();

                notificationRef.addValueEventListener(notifListener);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    ValueEventListener notifListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            list.clear();

            for (DataSnapshot ds : snapshot.getChildren()) {
                NotificationModel notification = ds.getValue(NotificationModel.class);
                notification.setKey(ds.getKey());
                try {

                    if (notification.getTargetId().equals(currentUserId)) {
                        list.add(notification);
                    }
                    if (role.equals("3")) {
                        if (notification.getKind().equals("4")) {
                            list.add(notification);
                        }
                    }
                    if (notification.getKind().equals("6")) {
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

            //sorting list
            Collections.sort(list, (obj1, obj2) -> obj2.getDate().compareTo(obj1.getDate()));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
}