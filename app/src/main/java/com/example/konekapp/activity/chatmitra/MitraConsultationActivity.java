package com.example.konekapp.activity.chatmitra;

import static com.example.konekapp.activity.chat.helper.Constants.KEY_COLLECTION_CONVERSATION;
import static com.example.konekapp.activity.chat.helper.Constants.KEY_DATE_TIME;
import static com.example.konekapp.activity.chat.helper.Constants.KEY_LAST_MESSAGE;
import static com.example.konekapp.activity.chat.helper.Constants.KEY_RECEIVER_ID;
import static com.example.konekapp.activity.chat.helper.Constants.KEY_RECEIVER_IMAGE;
import static com.example.konekapp.activity.chat.helper.Constants.KEY_RECEIVER_NAME;
import static com.example.konekapp.activity.chat.helper.Constants.KEY_SENDER_ID;
import static com.example.konekapp.activity.chat.helper.Constants.KEY_SENDER_IMAGE;
import static com.example.konekapp.activity.chat.helper.Constants.KEY_SENDER_NAME;
import static com.example.konekapp.activity.chat.helper.Constants.KEY_UNREAD_RECEIVER_COUNT;
import static com.example.konekapp.activity.chat.helper.Constants.KEY_UNREAD_SENDER_COUNT;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.konekapp.R;
import com.example.konekapp.activity.chat.ChatRoomActivity;
import com.example.konekapp.activity.chat.addconsultation.UserListener;
import com.example.konekapp.activity.chat.consultation.ConversationListener;
import com.example.konekapp.activity.chat.consultation.RecentConversationAdapter;
import com.example.konekapp.activity.chat.models.ChatMessagesModel;
import com.example.konekapp.activity.chat.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MitraConsultationActivity extends AppCompatActivity implements ConversationListener {

    ImageView btnBack;
    LinearLayout btnAddNewConsultation;
    RecyclerView rvConversation;
    String currentUserId;

    //recent conversation
    private List<ChatMessagesModel> listConversation;
    private RecentConversationAdapter recentConversationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation);

        btnBack = findViewById(R.id.btnBack);
        btnAddNewConsultation = findViewById(R.id.btnAddNewConsultation);
        rvConversation = findViewById(R.id.rvConversation);

        //initialization
        btnAddNewConsultation.setVisibility(View.GONE);
        init();

        //listen conversation
        listenConversation();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

//        btnAddNewConsultation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MitraConsultationMActivity.this, TambahKonsultasiActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    void init() {
        listConversation = new ArrayList<>();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        recentConversationAdapter = new RecentConversationAdapter(currentUserId, listConversation, this);
        rvConversation.setAdapter(recentConversationAdapter);
        rvConversation.setHasFixedSize(true);
        rvConversation.setLayoutManager(new LinearLayoutManager(this));
    }

    private void listenConversation() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(KEY_COLLECTION_CONVERSATION)
                .addValueEventListener(listener);
    }

    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            listConversation.clear();
            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                if (dataSnapshot.child(KEY_SENDER_ID).getValue().equals(currentUserId) || dataSnapshot.child(KEY_RECEIVER_ID).getValue().equals(currentUserId)) {
                    String senderId = dataSnapshot.child(KEY_SENDER_ID).getValue(String.class);
                    String receiverId = dataSnapshot.child(KEY_RECEIVER_ID).getValue(String.class);
                    String message = dataSnapshot.child(KEY_LAST_MESSAGE).getValue(String.class);
                    String dateTime = dataSnapshot.child(KEY_DATE_TIME).getValue(String.class);
                    Integer unreadSenderCount = dataSnapshot.child(KEY_UNREAD_SENDER_COUNT).getValue(Integer.class);
                    Integer unreadReceiverCount = dataSnapshot.child(KEY_UNREAD_RECEIVER_COUNT).getValue(Integer.class);

                    ChatMessagesModel chatMessagesModel = new ChatMessagesModel();
                    chatMessagesModel.setSenderId(senderId);
                    chatMessagesModel.setReceiverId(receiverId);
                    chatMessagesModel.lastMessage = message;
                    chatMessagesModel.setDateTime(dateTime);
                    chatMessagesModel.conversationKey = dataSnapshot.getKey();

                    if (currentUserId.equals(senderId)) {
                        chatMessagesModel.conversationId = dataSnapshot.child(KEY_RECEIVER_ID).getValue(String.class);
                        chatMessagesModel.conversationName = dataSnapshot.child(KEY_RECEIVER_NAME).getValue(String.class);
                        chatMessagesModel.conversationImage = dataSnapshot.child(KEY_RECEIVER_IMAGE).getValue(String.class);
                        chatMessagesModel.unreadCount = unreadSenderCount;
                    } else {
                        chatMessagesModel.conversationId = dataSnapshot.child(KEY_SENDER_ID).getValue(String.class);
                        chatMessagesModel.conversationName = dataSnapshot.child(KEY_SENDER_NAME).getValue(String.class);
                        chatMessagesModel.conversationImage = dataSnapshot.child(KEY_SENDER_IMAGE).getValue(String.class);
                        chatMessagesModel.unreadCount = unreadReceiverCount;
                    }

                    listConversation.add(chatMessagesModel);
                }
            }
            Collections.sort(listConversation, (obj1, obj2) -> obj2.getDateTime().compareTo(obj1.getDateTime()));
            Log.d("ConsultationActivity", "onDataChange: " + listConversation.size() );
            recentConversationAdapter.notifyDataSetChanged();
            rvConversation.smoothScrollToPosition(0);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {}
    };

    private void updateConversationCount(String conversationId, String child, int count) {
        HashMap<String, Object> conversation = new HashMap<>();
        conversation.put(child, count);

        DatabaseReference conversationRef = FirebaseDatabase.getInstance().getReference()
                .child(KEY_COLLECTION_CONVERSATION)
                .child(conversationId);

        conversationRef.updateChildren(conversation);

    }
    @Override
    public void onConversationClick(ChatMessagesModel chatMessage, UserModel user) {
        if (chatMessage.getSenderId().equals(currentUserId)) {
            //update count unread
            updateConversationCount(chatMessage.conversationKey, KEY_UNREAD_SENDER_COUNT, 0);
        } else {
            //update count unread
            updateConversationCount(chatMessage.conversationKey, KEY_UNREAD_RECEIVER_COUNT, 0);
        }
        Intent intent = new Intent(MitraConsultationActivity.this, ChatRoomActivity.class);
        intent.putExtra("conversationId", chatMessage.conversationId);
        intent.putExtra("user", user);
        startActivity(intent);
    }
}