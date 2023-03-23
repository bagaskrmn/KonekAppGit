package com.example.konekapp.activity;

public class TanamanModel {
    String Image;
    String Name;
    String Key;

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public TanamanModel() {
    }

    public TanamanModel(String image, String name) {
        Image = image;
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public String getName() {
        return Name;
    }
}
