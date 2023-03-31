package com.example.konekapp.activity;

public class TanamanModel {
    String image;
    String name;
    String key;

    public TanamanModel() {
    }

    public TanamanModel(String image, String name, String key) {
        this.image = image;
        this.name = name;
        this.key = key;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
