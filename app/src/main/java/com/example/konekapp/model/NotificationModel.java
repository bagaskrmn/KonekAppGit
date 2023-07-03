package com.example.konekapp.model;

public class NotificationModel {
    public String title, description, targetId, date, image, key;
    public int kind;
    public boolean isNotificationRead;

    public NotificationModel() {
    }

    public NotificationModel(String title, String description, String targetId, int kind, String date, String image, boolean isNotificationRead) {
        this.title = title;
        this.description = description;
        this.targetId = targetId;
        this.kind = kind;
        this.date = date;
        this.image = image;
        this.isNotificationRead = isNotificationRead;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

//    public boolean isNotificationRead() {
//        return isNotificationRead;
//    }
//
//    public void setNotificationRead(boolean notificationRead) {
//        isNotificationRead = notificationRead;
//    }
}
