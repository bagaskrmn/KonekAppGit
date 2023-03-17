package com.example.konekapp.activity.chat.consultation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.konekapp.R;
import com.example.konekapp.activity.chat.models.ChatMessagesModel;

import java.util.ArrayList;
import java.util.List;

public class RecentConversationAdapter extends RecyclerView.Adapter<RecentConversationAdapter.ViewHolder> {

    private final List<ChatMessagesModel> listMessage;

    public RecentConversationAdapter(List<ChatMessagesModel> listMessage) {
        this.listMessage = listMessage;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(listMessage.get(position));
    }

    @Override
    public int getItemCount() {
        return listMessage.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivConversation;
        TextView tvConversationName, tvMessage, tvTime, tvBadge;
        LinearLayout linearBadge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivConversation = itemView.findViewById(R.id.imageViewConversation);
            tvConversationName = itemView.findViewById(R.id.textViewNama);
            tvMessage = itemView.findViewById(R.id.textViewMessage);
            tvTime = itemView.findViewById(R.id.textViewTime);
            tvBadge = itemView.findViewById(R.id.textViewBadge);
            linearBadge = itemView.findViewById(R.id.linearBadge);
        }

        void bind(ChatMessagesModel dataConversation) {
            tvConversationName.setText(dataConversation.conversionName);
            tvMessage.setText(dataConversation.getMessage());
            tvTime.setText(dataConversation.getDateTime());
//            if (dataConversation.getBadge() == 0) {
//                linearBadge.setVisibility(View.GONE);
//            } else {
//                linearBadge.setVisibility(View.VISIBLE);
//                tvBadge.setText(String.valueOf(dataConversation.getBadge()));
//            }
        }
    }
}
