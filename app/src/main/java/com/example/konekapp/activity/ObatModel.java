package com.example.konekapp.activity;

public class ObatModel {
    String Image;
    String Name;
    String Description;
    String Key;

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public ObatModel() {
    }

    public ObatModel(String image, String name, String description) {
        Image = image;
        Name = name;
        Description = description;
    }

    public String getImage() {
        return Image;
    }

    public String getName() {
        return Name;
    }

    public String getDescription() {
        return Description;
    }
}
