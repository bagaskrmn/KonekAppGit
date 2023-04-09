package com.example.konekapp.ui.dashboard.home.consultation.chatroom;

import static com.example.konekapp.helper.Constants.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.konekapp.R;
import com.example.konekapp.model.ChatMessagesModel;
import com.example.konekapp.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ChatRoomActivity extends AppCompatActivity {

    private final String TAG = "ChatRoomActivity";

    TextView tvNameMentor, tvTitleMentor;
    ImageView ivProfile, btnBack, btnSend;
    RecyclerView rvChat;
    EditText edtMessage;
    private UserModel userReceiver;

    //chat
    private ArrayList<ChatMessagesModel> listChatMessages;
    private ChatMessageAdapter chatMessageAdapter;

    //conversation
    private String conversationId;
    private String senderConversationId;

    //firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private String currentUserId;
    private DatabaseReference rootRef, usersRef;
    private DatabaseReference messageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        userReceiver = (UserModel) getIntent().getSerializableExtra("user");

        //viewInitialization
        initialization();

        //profile receiver
        getProfileReceiver();

        //message listener
        listenMessages();

        //tv name mentor
        tvNameMentor.setText(userReceiver.getName());

        //iv profile
        Picasso.get().load(userReceiver.getImage()).into(ivProfile);

        //back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //send button
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send message
                if (edtMessage.getText().toString().trim().length() > 0) {
                    sendMessage(edtMessage.getText().toString().trim());
                }
            }
        });
    }

    private void initialization() {
        //Database Reference
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("users");

        //view initialization
        tvNameMentor = findViewById(R.id.tvNameMentor);
        tvTitleMentor = findViewById(R.id.tvTitleMentor);
        ivProfile = findViewById(R.id.ivProfile);
        btnBack = findViewById(R.id.btnBack);
        rvChat = findViewById(R.id.rvChat);
        btnSend = findViewById(R.id.btnSend);
        edtMessage = findViewById(R.id.edtMessage);

        //current user
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserId = currentUser.getUid();

        //chat
        listChatMessages = new ArrayList<>();
        chatMessageAdapter = new ChatMessageAdapter(listChatMessages, currentUserId);
        //rvChat
        rvChat.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(layoutManager);
        rvChat.setAdapter(chatMessageAdapter);

        //database ref
        messageRef = FirebaseDatabase.getInstance().getReference(KEY_COLLECTION_CHAT);

    }

    private void getProfileReceiver() {
        usersRef.child(userReceiver.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //check role
                String role = snapshot.child("role").getValue().toString();
                //if role is user(1)
                switch (role) {
                    case "0":
                        tvTitleMentor.setText("Pengguna");
                        break;
                    case "1":
                        tvTitleMentor.setText("Petani Mitra");
                        break;
                    case "2":
                        tvTitleMentor.setText("Ahli Tani");
                        break;
                    case "3":
                        tvTitleMentor.setText("Admin");
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    //function to send message
    private void sendMessage(String message) {
        //create a new message
        ChatMessagesModel chatMessagesModel = new ChatMessagesModel();
        chatMessagesModel.setSenderId(currentUserId);
        chatMessagesModel.setReceiverId(userReceiver.getUserId());
        chatMessagesModel.setMessage(message);
        chatMessagesModel.setDateTime(getDateTime());
        chatMessagesModel.setIsSenderRead(true);
        chatMessagesModel.setIsReceiverRead(false);

        //save message to firebase
        FirebaseDatabase.getInstance().getReference()
                .child(KEY_COLLECTION_CHAT)
                .push()
                .setValue(chatMessagesModel);

        //add or update conversation
        addOrUpdateConversation(chatMessagesModel);

        edtMessage.setText("");
    }

    private void addOrUpdateConversation(ChatMessagesModel chatMessagesModel) {
        //Add new conversation if conversationId is null
        if (conversationId != null) {
            Log.d(TAG, "addOrUpdateConversation: update conversation");
            updateConversation(chatMessagesModel.getMessage());
        } else {
            Log.d(TAG, "addOrUpdateConversation: add new conversation");
            //Step 1. Get user information
            FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(currentUserId)
                    .addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String retrieveImage = snapshot.child("image").getValue().toString();
                                    String retrieveName = snapshot.child("name").getValue().toString();

                                    //create a new conversation
                                    HashMap <String, Object> conversation = new HashMap<>();
                                    conversation.put(KEY_SENDER_ID, currentUserId);
                                    conversation.put(KEY_SENDER_NAME, retrieveName);
                                    conversation.put(KEY_SENDER_IMAGE, retrieveImage);
                                    conversation.put(KEY_RECEIVER_ID, userReceiver.getUserId());
                                    conversation.put(KEY_RECEIVER_NAME, userReceiver.getName());
                                    conversation.put(KEY_RECEIVER_IMAGE, userReceiver.getImage());
                                    conversation.put(KEY_LAST_MESSAGE, chatMessagesModel.getMessage());
                                    conversation.put(KEY_DATE_TIME, chatMessagesModel.getDateTime());
                                    conversation.put(KEY_UNREAD_RECEIVER_COUNT, unReadCount);
                                    conversation.put(KEY_UNREAD_SENDER_COUNT, 0);

                                    senderConversationId = currentUserId;

                                    //Step 2. Add new conversation
                                    addConversation(conversation);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            }
                    );
        }
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", new Locale("id", "ID"));
        Date date = new Date();

        return dateFormat.format(date);
    }

    private void listenMessages() {
        messageRef.addValueEventListener(listener);
    }

    private int unReadCount = 0;

    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            listChatMessages.clear();
            unReadCount = 0;

            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                ChatMessagesModel chatMessagesModel = dataSnapshot.getValue(ChatMessagesModel.class);
                if (chatMessagesModel.getSenderId().equals(currentUserId) && chatMessagesModel.getReceiverId().equals(userReceiver.getUserId())) {
                    //count unread message
                    if (!chatMessagesModel.getIsReceiverRead()) {
                        unReadCount ++;
                    }
                    try {chatMessagesModel.setDateTime(setSimpleDate(chatMessagesModel.getDateTime()));}
                    catch (ParseException e) {Log.d("ChatRoomActivity", "onDataChange: " + e.getMessage());}
                    listChatMessages.add(chatMessagesModel);
                } else if (chatMessagesModel.getSenderId().equals(userReceiver.getUserId()) && chatMessagesModel.getReceiverId().equals(currentUserId)) {
                    //update message to has read
                    if (!chatMessagesModel.getIsReceiverRead()) {
                        updateReceiverReadMessage(dataSnapshot.getKey());
                    }
                    try {chatMessagesModel.setDateTime(setSimpleDate(chatMessagesModel.getDateTime()));}
                    catch (ParseException e) {Log.d("ChatRoomActivity", "onDataChange: " + e.getMessage());}
                    listChatMessages.add(chatMessagesModel);
                }
            }
            Collections.sort(listChatMessages, (obj1, obj2) -> obj1.getDateTime().compareTo(obj2.getDateTime()));
            if (listChatMessages.size() == 0) {
                chatMessageAdapter.notifyDataSetChanged();
            } else {
                chatMessageAdapter.notifyItemRangeInserted(listChatMessages.size(), listChatMessages.size());
                chatMessageAdapter.notifyDataSetChanged();
                rvChat.smoothScrollToPosition(listChatMessages.size() - 1);
            }

            //check for conversation
            if (conversationId == null) {
                checkForConversation();
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {}
    };

    private String setSimpleDate(String dateTime) throws ParseException {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date1 = originalFormat.parse(dateTime);

        SimpleDateFormat targetFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("id", "ID"));
        return targetFormat.format(date1);
    }

    void checkForConversation() {
        if (listChatMessages.size() != 0) {
            FirebaseDatabase.getInstance().getReference()
                    .child(KEY_COLLECTION_CONVERSATION)
                    .get()
                    .addOnCompleteListener(conversationOnCompleteListener);
        }
    }

    private final OnCompleteListener<DataSnapshot> conversationOnCompleteListener = task -> {
        if (task.isSuccessful()) {
            DataSnapshot dataSnapshot = task.getResult();
            if (dataSnapshot.exists()) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.child(KEY_SENDER_ID).getValue().equals(currentUserId) && child.child(KEY_RECEIVER_ID).getValue().equals(userReceiver.getUserId()) ||
                            child.child(KEY_SENDER_ID).getValue().equals(userReceiver.getUserId()) && child.child(KEY_RECEIVER_ID).getValue().equals(currentUserId)) {
                        conversationId = child.getKey();
                        senderConversationId = child.child(KEY_SENDER_ID).getValue().toString();
                        break;
                    }
                }
            }
        }
    };

    //add new conversation
    private void addConversation(HashMap<String, Object> conversation) {
        DatabaseReference conversationRef = FirebaseDatabase.getInstance().getReference()
                .child(KEY_COLLECTION_CONVERSATION).push();
        conversationId = conversationRef.getKey();
        conversationRef.setValue(conversation);
    }

    //update conversation
    private void updateConversation(String message) {
        HashMap<String, Object> conversation = new HashMap<>();
        conversation.put(KEY_LAST_MESSAGE, message);
        conversation.put(KEY_DATE_TIME, getDateTime());

        if (currentUser.getUid().equals(senderConversationId)) {
            conversation.put(KEY_UNREAD_RECEIVER_COUNT, unReadCount + 1);
            conversation.put(KEY_UNREAD_SENDER_COUNT, 0);
        } else {
            conversation.put(KEY_UNREAD_RECEIVER_COUNT, 0);
            conversation.put(KEY_UNREAD_SENDER_COUNT, unReadCount + 1);
        }

        FirebaseDatabase.getInstance().getReference()
                .child(KEY_COLLECTION_CONVERSATION)
                .child(conversationId)
                .updateChildren(conversation);
    }

    private void updateZeroCountConversation() {
        HashMap<String, Object> conversation = new HashMap<>();

        if (currentUser.getUid().equals(senderConversationId)) {
            conversation.put(KEY_UNREAD_SENDER_COUNT, 0);
        } else {
            conversation.put(KEY_UNREAD_RECEIVER_COUNT, 0);
        }

        FirebaseDatabase.getInstance().getReference()
                .child(KEY_COLLECTION_CONVERSATION)
                .child(conversationId)
                .updateChildren(conversation);
    }

    private void updateReceiverReadMessage(String key) {
        FirebaseDatabase.getInstance().getReference()
                .child(KEY_COLLECTION_CHAT)
                .child(key)
                .child("isReceiverRead")
                .setValue(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (conversationId != null) {
            updateZeroCountConversation();
        }
        messageRef.removeEventListener(listener);
    }
}