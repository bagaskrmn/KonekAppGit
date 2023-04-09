package com.example.konekapp.ui.dashboard.home.consultation;

import com.example.konekapp.model.ChatMessagesModel;
import com.example.konekapp.model.UserModel;

public interface ConversationListener {
    void onConversationClick(ChatMessagesModel chatMessage, UserModel user);
}
