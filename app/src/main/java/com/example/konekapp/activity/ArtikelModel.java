package com.example.konekapp.activity;

public class ArtikelModel {
    String title;
    String image;
    String source;
    String date;
    String description;
    String sourceImage;
    String key;

    public ArtikelModel() {
    }

    public ArtikelModel(String title, String image, String source, String date, String description, String sourceImage) {
        this.title = title;
        this.image = image;
        this.source = source;
        this.date = date;
        this.description = description;
        this.sourceImage = sourceImage;
//        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSourceImage() {
        return sourceImage;
    }

    public void setSourceImage(String sourceImage) {
        this.sourceImage = sourceImage;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
