package com.example.konekapp.activity.chat.consultation;

import com.example.konekapp.activity.chat.models.ChatMessagesModel;
import com.example.konekapp.activity.chat.models.UserModel;

public interface ConversationListener {
    void onConversationClick(ChatMessagesModel chatMessage, UserModel user);
}
