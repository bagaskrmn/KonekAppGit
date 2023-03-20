package com.example.konekapp.activity.chat.chatroom;

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
import com.example.konekapp.activity.chat.models.ChatMessagesModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ChatMessagesModel> listChatMessagesModel;
    private String senderId;

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private ChatMessagesModel previousChatMessagesModel;

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
            if (position == 0) {
                ((SentMessageViewHolder) holder).bind(null, listChatMessagesModel.get(position));
            } else {
                ((SentMessageViewHolder) holder).bind(listChatMessagesModel.get(position - 1), listChatMessagesModel.get(position));
            }
        } else {
            if (position == 0) {
                ((ReceivedMessageViewHolder) holder).bind(null, listChatMessagesModel.get(position));
            } else {
                ((ReceivedMessageViewHolder) holder).bind(listChatMessagesModel.get(position - 1), listChatMessagesModel.get(position));
            }
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
        TextView message, time, timeSeparator;
        LinearLayout linearTime;
        ImageView ivSentMessageStatus;
        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.tvSentMessage);
            time = itemView.findViewById(R.id.tvSentTime);
            linearTime = itemView.findViewById(R.id.linear_item_bubble_time);
            timeSeparator = linearTime.findViewById(R.id.tvTimeSeparator);
            ivSentMessageStatus = itemView.findViewById(R.id.icCheckReadSender);
        }

        void bind(ChatMessagesModel previousMessage, ChatMessagesModel currentMessage) {
            if (previousMessage != null) {
                String date1 = previousMessage.getDateTime();
                String date2 = currentMessage.getDateTime();
                int timeComparator = 0;

                try {
                    timeComparator = compareTime(date1, date2);
                } catch (ParseException e) {
                    Log.d("ChatMessageAdapter", "bind: " + e.getMessage());
                }

                if (timeComparator == 0) {
                    linearTime.setVisibility(View.GONE);
                } else {
                    linearTime.setVisibility(View.VISIBLE);
                    try { timeSeparator.setText(getDateAndMonth(currentMessage.getDateTime()));}
                    catch (ParseException e) { Log.d("ChatMessageAdapter", "bind: " + e.getMessage()); }
                }
            } else {
                linearTime.setVisibility(View.VISIBLE);
                try { timeSeparator.setText(getDateAndMonth(currentMessage.getDateTime()));
                } catch (ParseException e) { Log.d("ChatMessageAdapter", "bind: " + e.getMessage()); }
            }
            message.setText(currentMessage.getMessage());
            try {time.setText(getHours(currentMessage.getDateTime()));}
            catch (ParseException e) { Log.d("ChatMessageAdapter", "bind: " + e.getMessage()); }

            if (currentMessage.getIsReceiverRead()) {
                ivSentMessageStatus.setColorFilter(itemView.getContext().getResources().getColor(R.color.check_active, null));
            } else {
                ivSentMessageStatus.setColorFilter(itemView.getContext().getResources().getColor(R.color.neutral_dark, null));
            }
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView message, time, timeSeparator;
        LinearLayout linearTime;
        ImageView ivSentMessageStatus;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.tvReceivedMessage);
            time = itemView.findViewById(R.id.tvReceivedTime);
            linearTime = itemView.findViewById(R.id.linear_item_bubble_time);
            timeSeparator = linearTime.findViewById(R.id.tvTimeSeparator);
            ivSentMessageStatus = itemView.findViewById(R.id.icCheckReadReceiver);
        }

        void bind(ChatMessagesModel previousMessage, ChatMessagesModel currentMessage) {
            if (previousMessage != null) {
                String date1 = previousMessage.getDateTime();
                String date2 = currentMessage.getDateTime();
                int timeComparator = 0;

                try {
                    timeComparator = compareTime(date1, date2);
                } catch (ParseException e) {
                    Log.d("ChatMessageAdapter", "bind: " + e.getMessage());
                }

                if (timeComparator == 0) {
                    linearTime.setVisibility(View.GONE);
                } else {
                    linearTime.setVisibility(View.VISIBLE);
                    try { timeSeparator.setText(getDateAndMonth(currentMessage.getDateTime()));
                    } catch (ParseException e) { Log.d("ChatMessageAdapter", "bind: " + e.getMessage()); }
                }
            } else {
                linearTime.setVisibility(View.VISIBLE);
                try { timeSeparator.setText(getDateAndMonth(currentMessage.getDateTime()));
                } catch (ParseException e) { Log.d("ChatMessageAdapter", "bind: " + e.getMessage()); }
            }
            message.setText(currentMessage.getMessage());
            try {time.setText(getHours(currentMessage.getDateTime()));}
            catch (ParseException e) { Log.d("ChatMessageAdapter", "bind: " + e.getMessage()); }

            if (currentMessage.getIsReceiverRead()) {
                ivSentMessageStatus.setColorFilter(itemView.getContext().getResources().getColor(R.color.check_active, null));
            } else {
                ivSentMessageStatus.setColorFilter(itemView.getContext().getResources().getColor(R.color.neutral_dark, null));
            }
        }
    }

    private static int compareTime(String stringDate1, String stringDate2) throws ParseException {
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("id", "ID"));
        Date date1 = originalFormat.parse(stringDate1);
        Date date2 = originalFormat.parse(stringDate2);

        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        String targetDateString1 = targetFormat.format(date1);
        String targetDateString2 = targetFormat.format(date2);

        return targetFormat.parse(targetDateString1).compareTo(targetFormat.parse(targetDateString2));
    }

    private static String getDateAndMonth(String stringDate) throws ParseException {
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("id", "ID"));
        Date date = originalFormat.parse(stringDate);

        SimpleDateFormat targetFormat = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
        String targetDateString = targetFormat.format(date);

        return targetDateString;
    }

    private static String getHours(String stringDate) throws ParseException {
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("id", "ID"));
        Date date = originalFormat.parse(stringDate);

        SimpleDateFormat targetFormat = new SimpleDateFormat("HH:mm", new Locale("id", "ID"));
        String targetDateString = targetFormat.format(date);

        return targetDateString;
    }
}
