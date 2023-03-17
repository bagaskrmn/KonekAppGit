package com.example.konekapp.activity.chat.models;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;

public class UserModel implements Serializable {
    @PropertyName("UserID")
    String UserId;
    String Nama;
    String Role;
    String Image;
    @PropertyName("Bergabung pada")
    String BergabungPada;

    public UserModel() {
    }

    public UserModel(String userId, String nama, String role, String image, String bergabungPada) {
        UserId = userId;
        Nama = nama;
        Role = role;
        Image = image;
        BergabungPada = bergabungPada;
    }

    @PropertyName("Bergabung pada")
    public String getBergabungPada() {
        return BergabungPada;
    }

    @PropertyName("Bergabung pada")
    public void setBergabungPada(String bergabungPada) {
        BergabungPada = bergabungPada;
    }

    @PropertyName("UserID")
    public String getUserId() {
        return UserId;
    }

    @PropertyName("UserID")
    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getNama() {
        return Nama;
    }

    public void setNama(String nama) {
        Nama = nama;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
