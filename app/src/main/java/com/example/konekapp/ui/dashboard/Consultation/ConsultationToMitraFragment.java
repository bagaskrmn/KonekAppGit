package com.example.konekapp.ui.dashboard.Consultation;

import static com.example.konekapp.helper.Constants.KEY_COLLECTION_CONVERSATION;
import static com.example.konekapp.helper.Constants.KEY_DATE_TIME;
import static com.example.konekapp.helper.Constants.KEY_LAST_MESSAGE;
import static com.example.konekapp.helper.Constants.KEY_RECEIVER_ID;
import static com.example.konekapp.helper.Constants.KEY_RECEIVER_IMAGE;
import static com.example.konekapp.helper.Constants.KEY_RECEIVER_NAME;
import static com.example.konekapp.helper.Constants.KEY_SENDER_ID;
import static com.example.konekapp.helper.Constants.KEY_SENDER_IMAGE;
import static com.example.konekapp.helper.Constants.KEY_SENDER_NAME;
import static com.example.konekapp.helper.Constants.KEY_UNREAD_RECEIVER_COUNT;
import static com.example.konekapp.helper.Constants.KEY_UNREAD_SENDER_COUNT;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.konekapp.R;
import com.example.konekapp.model.ChatMessagesModel;
import com.example.konekapp.model.UserModel;
import com.example.konekapp.ui.dashboard.home.consultation.ConversationListener;
import com.example.konekapp.ui.dashboard.home.consultation.RecentConversationAdapter;
import com.example.konekapp.ui.dashboard.home.consultation.addconsultation.ahlitani.TambahKonsultasiToMitraActivity;
import com.example.konekapp.ui.dashboard.home.consultation.chatroom.ChatRoomActivity;
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

public class ConsultationToMitraFragment extends Fragment implements ConversationListener {

    ImageView btnBack;
    LinearLayout btnAddNewConsultation;
    RecyclerView rvConversation;
    String currentUserId;
    TextView tvNoData;
    EditText edtSearch;

    //recent conversation
    private List<ChatMessagesModel> listConversation;
    private RecentConversationAdapter recentConversationAdapter;

    public ConsultationToMitraFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_consultation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //initialization
        init();

        //listen conversation
        listenConversation();

//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
//            }
//        });

        btnAddNewConsultation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), TambahKonsultasiToMitraActivity.class);
                startActivity(intent);
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    void init() {
        //initialize view
        btnBack = requireActivity().findViewById(R.id.btnBack);
        btnAddNewConsultation = requireActivity().findViewById(R.id.btnAddNewConsultation);
        rvConversation = requireActivity().findViewById(R.id.rvConversation);
        tvNoData = requireActivity().findViewById(R.id.tvNoData);
        edtSearch = requireActivity().findViewById(R.id.edtSearch);

        //initialization
        listConversation = new ArrayList<>();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        recentConversationAdapter = new RecentConversationAdapter(currentUserId, this);
        rvConversation.setAdapter(recentConversationAdapter);
        rvConversation.setHasFixedSize(true);
        rvConversation.setLayoutManager(new LinearLayoutManager(requireContext()));
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

                    //create chat model and assign the value
                    ChatMessagesModel chatMessagesModel = new ChatMessagesModel();
                    chatMessagesModel.setSenderId(senderId);
                    chatMessagesModel.setReceiverId(receiverId);
                    chatMessagesModel.lastMessage = message;
                    chatMessagesModel.setDateTime(dateTime);
                    chatMessagesModel.conversationKey = dataSnapshot.getKey();

                    if (currentUserId.equals(senderId)) {
                        //conversationId mean the id of the receiver
                        chatMessagesModel.conversationId = dataSnapshot.child(KEY_RECEIVER_ID).getValue(String.class);
                        chatMessagesModel.conversationName = dataSnapshot.child(KEY_RECEIVER_NAME).getValue(String.class);
                        chatMessagesModel.conversationImage = dataSnapshot.child(KEY_RECEIVER_IMAGE).getValue(String.class);
                        chatMessagesModel.unreadCount = unreadSenderCount;
                    } else {
                        //conversationId mean the id of the sender
                        chatMessagesModel.conversationId = dataSnapshot.child(KEY_SENDER_ID).getValue(String.class);
                        chatMessagesModel.conversationName = dataSnapshot.child(KEY_SENDER_NAME).getValue(String.class);
                        chatMessagesModel.conversationImage = dataSnapshot.child(KEY_SENDER_IMAGE).getValue(String.class);
                        chatMessagesModel.unreadCount = unreadReceiverCount;
                    }

                    listConversation.add(chatMessagesModel);
                }
            }
            //check if list conversation is empty or not
            if (listConversation.size() > 0) {
                rvConversation.setVisibility(View.VISIBLE);
                tvNoData.setVisibility(View.GONE);
            } else {
                rvConversation.setVisibility(View.GONE);
                tvNoData.setVisibility(View.VISIBLE);
            }
            //sorting list conversation by date time
            Collections.sort(listConversation, (obj1, obj2) -> obj2.getDateTime().compareTo(obj1.getDateTime()));

            Log.d("ConsultationToAhliTaniActivity", "onDataChange: " + listConversation.size() );
            recentConversationAdapter.setListConversation(listConversation);
            rvConversation.smoothScrollToPosition(0);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {}
    };

    public void filterList(String query) {
        List<ChatMessagesModel> filteredListConversation = new ArrayList<>();
        filteredListConversation.clear();
        Log.d("RecentConversationAdapter", "filterList: "+query);
        if (query.isEmpty()) {
            filteredListConversation.addAll(listConversation);
        } else {
            for (ChatMessagesModel item : listConversation) {
                if (item.conversationName.toLowerCase().contains(query.toLowerCase())) {
                    filteredListConversation.add(item);
                }
            }
        }

        if (filteredListConversation.size() > 0) {
            rvConversation.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
        } else {
            rvConversation.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
        }

        Log.d("RecentConversationAdapter", "list: "+ listConversation);
        Log.d("RecentConversationAdapter", "filterList: "+ filteredListConversation);
        recentConversationAdapter.setListConversation(filteredListConversation);
    }

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
        //updating unread count
        if (chatMessage.getSenderId().equals(currentUserId)) {
            //update count unread
            updateConversationCount(chatMessage.conversationKey, KEY_UNREAD_SENDER_COUNT, 0);
        } else {
            //update count unread
            updateConversationCount(chatMessage.conversationKey, KEY_UNREAD_RECEIVER_COUNT, 0);
        }
        Intent intent = new Intent(requireContext(), ChatRoomActivity.class);
        intent.putExtra("conversationId", chatMessage.conversationId);
        intent.putExtra("user", user);
        startActivity(intent);
    }
}