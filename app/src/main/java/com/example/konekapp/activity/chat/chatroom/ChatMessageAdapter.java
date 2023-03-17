package com.example.konekapp.activity.chat.chatroom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.konekapp.R;
import com.example.konekapp.activity.chat.models.ChatMessagesModel;

import java.util.ArrayList;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ChatMessagesModel> listChatMessagesModel;
    private String senderId;

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    public ChatMessageAdapter(ArrayList<ChatMessagesModel> listChatMessagesModel, String senderId) {
        this.listChatMessagesModel = listChatMessagesModel;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bubble_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bubble_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_MESSAGE_SENT) {
            ((SentMessageViewHolder) holder).bind(listChatMessagesModel.get(position));
        } else {
            ((ReceivedMessageViewHolder) holder).bind(listChatMessagesModel.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return listChatMessagesModel.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (listChatMessagesModel.get(position).getSenderId().equals(senderId)) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView message, time;
        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.tvSentMessage);
            time = itemView.findViewById(R.id.tvSentTime);
        }

        void bind(ChatMessagesModel chatMessagesModel) {
            message.setText(chatMessagesModel.getMessage());
            time.setText(chatMessagesModel.getDateTime());
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView message, time;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.tvReceivedMessage);
            time = itemView.findViewById(R.id.tvReceivedTime);
        }

        void bind(ChatMessagesModel chatMessagesModel) {
            message.setText(chatMessagesModel.getMessage());
            time.setText(chatMessagesModel.getDateTime());
        }
    }

}
