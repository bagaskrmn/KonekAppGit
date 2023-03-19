package com.example.konekapp.activity.chat.consultation;

import com.example.konekapp.activity.chat.models.UserModel;

public interface ConversationListener {
    void onConversationClick(String conversationId, UserModel user);
}
