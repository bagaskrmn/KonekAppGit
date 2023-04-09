package com.example.konekapp.model;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;

public class UserModel implements Serializable {
    public String userId;
    public String city;
    public String dateJoined;
    public String domicile;
    public String email;
    public String fullAddress;
    public String idCardImage;
    public String image;
    public String name;
    public String nik;
    public String phoneNumber;
    public String province;
    public String question1;
    public String question2;
    public String role;
    public String subdistrict;
    public String village;

    public UserModel() {
    }

    public UserModel(String userId, String city, String dateJoined, String domicile, String email, String fullAddress, String idCardImage, String image, String name, String nik, String phoneNumber, String province, String question1, String question2, String role, String subdistrict, String village) {
        this.userId = userId;
        this.city = city;
        this.dateJoined = dateJoined;
        this.domicile = domicile;
        this.email = email;
        this.fullAddress = fullAddress;
        this.idCardImage = idCardImage;
        this.image = image;
        this.name = name;
        this.nik = nik;
        this.phoneNumber = phoneNumber;
        this.province = province;
        this.question1 = question1;
        this.question2 = question2;
        this.role = role;
        this.subdistrict = subdistrict;
        this.village = village;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(String dateJoined) {
        this.dateJoined = dateJoined;
    }

    public String getDomicile() {
        return domicile;
    }

    public void setDomicile(String domicile) {
        this.domicile = domicile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getIdCardImage() {
        return idCardImage;
    }

    public void setIdCardImage(String idCardImage) {
        this.idCardImage = idCardImage;
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

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getQuestion1() {
        return question1;
    }

    public void setQuestion1(String question1) {
        this.question1 = question1;
    }

    public String getQuestion2() {
        return question2;
    }

    public void setQuestion2(String question2) {
        this.question2 = question2;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSubdistrict() {
        return subdistrict;
    }

    public void setSubdistrict(String subdistrict) {
        this.subdistrict = subdistrict;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }
}
