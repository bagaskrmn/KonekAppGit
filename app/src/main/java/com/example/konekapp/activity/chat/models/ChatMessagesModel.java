package com.example.konekapp.activity.chat.models;

public class ChatMessagesModel {
    String senderId, receiverId, message, dateTime, isRead;

    public String conversionId, conversionName;

    public ChatMessagesModel() {
    }

    public ChatMessagesModel(String senderId, String receiverId, String message, String dateTime, String isRead) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.dateTime = dateTime;
        this.isRead = isRead;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }
}
