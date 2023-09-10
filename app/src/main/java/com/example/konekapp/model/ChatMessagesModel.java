package com.example.konekapp.model;

public class ChatMessagesModel {
    String senderId, receiverId, message, dateTime;
    Boolean isSenderRead, isReceiverRead;

    public Integer unreadCount;

    public String conversationKey, conversationId, conversationName, conversationImage, lastMessage;

    public ChatMessagesModel() {
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

    public Boolean getIsSenderRead() {
        return isSenderRead;
    }

    public void setIsSenderRead(Boolean isSenderRead) {
        this.isSenderRead = isSenderRead;
    }

    public Boolean getIsReceiverRead() {
        return isReceiverRead;
    }

    public void setIsReceiverRead(Boolean isReceiverRead) {
        this.isReceiverRead = isReceiverRead;
    }

}
