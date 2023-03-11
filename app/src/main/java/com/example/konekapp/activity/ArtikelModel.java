package com.example.konekapp.activity;

public class ArtikelModel {
    String Title;
    String Image;
    String Source;
    String Date;
    String Description;

    //tanpa masukin data bisa pakai constructor ini
    public ArtikelModel() {
    }

    public ArtikelModel(String title, String image, String source, String date, String description) {
        Title = title;
        Image = image;
        Source = source;
        Date = date;
        Description = description;
    }

    public String getTitle() {
        return Title;
    }

    public String getImage() {
        return Image;
    }

    public String getSource() {
        return Source;
    }

    public String getDate() {
        return Date;
    }

    public String getDescription() {
        return Description;
    }
}
