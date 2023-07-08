package com.example.konekapp.ui.dashboard.home.consultation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.konekapp.R;
import com.example.konekapp.model.ChatMessagesModel;
import com.example.konekapp.model.UserModel;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecentConversationAdapter extends RecyclerView.Adapter<RecentConversationAdapter.ViewHolder> {
    private List<ChatMessagesModel> filteredListConversation = new ArrayList<>();
    private ConversationListener conversationListener;
    private String currentId;

    public RecentConversationAdapter(String currentId, ConversationListener conversationListener) {
        this.currentId = currentId;
        this.conversationListener = conversationListener;
    }

    public void setListConversation(List<ChatMessagesModel> listConversation) {
        this.filteredListConversation = listConversation;
        Log.d("RecentConversationAdapter", "oldList: "+ listConversation.size());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(filteredListConversation.get(position));
    }

    @Override
    public int getItemCount() {
        return filteredListConversation.size();
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
            tvConversationName.setText(dataConversation.conversationName);
            tvMessage.setText(dataConversation.lastMessage);
            try {
                tvTime.setText(getTime(dataConversation.getDateTime()));
            } catch (ParseException e) {
                Log.d("RecentConversationAdapter", "bind: " + e.getMessage());
            }
            Picasso.get().load(dataConversation.conversationImage).into(ivConversation);

            if (dataConversation.unreadCount == 0) {
                linearBadge.setVisibility(View.GONE);
            } else {
                linearBadge.setVisibility(View.VISIBLE);
                tvBadge.setText(String.valueOf(dataConversation.unreadCount));
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserModel user = new UserModel();
                    user.setUserId(dataConversation.conversationId);
                    user.setName(dataConversation.conversationName);
                    user.setImage(dataConversation.conversationImage);
                    conversationListener.onConversationClick(dataConversation, user);
                }
            });

//            if (dataConversation.getBadge() == 0) {
//                linearBadge.setVisibility(View.GONE);
//            } else {
//                linearBadge.setVisibility(View.VISIBLE);
//                tvBadge.setText(String.valueOf(dataConversation.getBadge()));
//            }
        }
    }

    private static String getTime(String stringDate) throws ParseException {
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = originalFormat.parse(stringDate);

        SimpleDateFormat targetFormatDate = new SimpleDateFormat("dd MMM", new Locale("id", "ID"));
        String targetDateString = targetFormatDate.format(date);

        SimpleDateFormat targetFormatTime = new SimpleDateFormat("HH:mm", new Locale("id", "ID"));
        String targetTimeString = targetFormatTime.format(date);

        Date today = new Date();

        if (targetDateString.equals(targetFormatDate.format(today))) {
            return targetTimeString;
        } else {
            return targetDateString;
        }

    }
}
