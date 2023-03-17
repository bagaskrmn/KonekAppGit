package com.example.konekapp.activity.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.konekapp.activity.chat.chatroom.ChatMessageAdapter;
import com.example.konekapp.activity.chat.models.ChatMessagesModel;
import com.example.konekapp.activity.chat.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EventListener;
import java.util.HashMap;

public class ChatRoomActivity extends AppCompatActivity {

    TextView tvNameMentor, tvTitleMentor;
    ImageView ivProfile, btnBack, btnSend;
    RecyclerView rvChat;
    EditText edtMessage;
    private UserModel userReceiver;

    //chat
    private ArrayList<ChatMessagesModel> listChatMessages;
    private ChatMessageAdapter chatMessageAdapter;
    private static final String KEY_COLLECTION_CHAT = "chat";
    private static final String KEY_SENDER_ID = "senderId";
    private static final String KEY_RECEIVER_ID = "receiverId";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_DATE_TIME = "dateTime";

    //conversation
    private static final String KEY_COLLECTION_CONVERSATION = "conversations";
    private static final String KEY_SENDER_NAME = "senderName";
    private static final String KEY_RECEIVER_NAME = "receiverName";
    private static final String KEY_SENDER_IMAGE = "senderImage";
    private static final String KEY_RECEIVER_IMAGE = "receiverImage";
    private static final String KEY_LAST_MESSAGE = "lastMessage";

    private static final String KEY_LAST_TIMESTAMP = "lastTimestamp";
    private static final String KEY_UNREAD_COUNT = "unreadCount";
    private String conversationId = null;


    //firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        userReceiver = (UserModel) getIntent().getSerializableExtra("user");

        //viewInitialization
        initialization();

        //message listener
        listenMessages();

        //tv name mentor
        tvNameMentor.setText(userReceiver.getNama());

        //tv title mentor
        if (userReceiver.getRole().equals("3")) {
            tvTitleMentor.setText("Ahli Tani");
        } else if (userReceiver.getRole().equals("2")) {
            tvTitleMentor.setText("Mitra Tani");
        }

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
                sendMessage(edtMessage.getText().toString());
            }
        });
    }

    private void initialization() {
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
    }

    //function to send message
    private void sendMessage(String message) {
        //create a new message
        ChatMessagesModel chatMessagesModel = new ChatMessagesModel();
        chatMessagesModel.setSenderId(currentUserId);
        chatMessagesModel.setReceiverId(userReceiver.getUserId());
        chatMessagesModel.setMessage(message);
        chatMessagesModel.setDateTime(getDateTime());

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
        if (conversationId != null) {
            updateConversation(chatMessagesModel.getMessage());
        } else {
            FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(currentUserId)
                    .addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String retrieveImage = snapshot.child("Image").getValue().toString();
                                    String retrieveName = snapshot.child("Nama").getValue().toString();

                                    //create a new conversation
                                    HashMap <String, Object> conversation = new HashMap<>();
                                    conversation.put(KEY_SENDER_ID, currentUserId);
                                    conversation.put(KEY_SENDER_NAME, retrieveName);
                                    conversation.put(KEY_SENDER_IMAGE, retrieveImage);
                                    conversation.put(KEY_RECEIVER_ID, userReceiver.getUserId());
                                    conversation.put(KEY_RECEIVER_NAME, userReceiver.getNama());
                                    conversation.put(KEY_RECEIVER_IMAGE, userReceiver.getImage());
                                    conversation.put(KEY_LAST_MESSAGE, chatMessagesModel.getMessage());
                                    conversation.put(KEY_DATE_TIME, chatMessagesModel.getDateTime());
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
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        return dateFormat.format(date);
    }

    private void listenMessages() {
        FirebaseDatabase.getInstance().getReference(KEY_COLLECTION_CHAT).addValueEventListener(listener);
    }

    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            listChatMessages.clear();
            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                ChatMessagesModel chatMessagesModel = dataSnapshot.getValue(ChatMessagesModel.class);
                if (chatMessagesModel.getSenderId().equals(currentUserId) && chatMessagesModel.getReceiverId().equals(userReceiver.getUserId()) ||
                        chatMessagesModel.getSenderId().equals(userReceiver.getUserId()) && chatMessagesModel.getReceiverId().equals(currentUserId)) {
                    listChatMessages.add(chatMessagesModel);
                }
            }
            Collections.sort(listChatMessages, (obj1, obj2) -> obj1.getDateTime().compareTo(obj2.getDateTime()));
            if (listChatMessages.size() == 0) {
                chatMessageAdapter.notifyDataSetChanged();
            } else {
                chatMessageAdapter.notifyItemRangeInserted(listChatMessages.size(), listChatMessages.size());
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

        FirebaseDatabase.getInstance().getReference()
                .child(KEY_COLLECTION_CONVERSATION)
                .child(conversationId)
                .updateChildren(conversation);
    }
}