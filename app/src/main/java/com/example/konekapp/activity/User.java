package com.example.konekapp.activity;

public class User {
    private String name;
    private String age;


    //untuk apa empty constructor
    public User() {
    }

    public User(String name, String age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }
}
