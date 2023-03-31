package com.example.konekapp.activity;

public class PenyakitModel {
    String image;
    String name;
    String description;
    String key;

    public PenyakitModel() {
    }

    public PenyakitModel(String image, String name, String description, String key) {
        this.image = image;
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
