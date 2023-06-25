package com.example.konekapp.model;

public class CropsModel {
    public String userId, name, commodity, period, date, qty, location,
    fertilizer, result, notes, status, cropsId;

    public CropsModel() {
    }

    public CropsModel(String userId, String name, String commodity, String period, String date, String qty, String location, String fertilizer, String result, String notes, String status) {
        this.userId = userId;
        this.name = name;
        this.commodity = commodity;
        this.period = period;
        this.date = date;
        this.qty = qty;
        this.location = location;
        this.fertilizer = fertilizer;
        this.result = result;
        this.notes = notes;
        this.status = status;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFertilizer() {
        return fertilizer;
    }

    public void setFertilizer(String fertilizer) {
        this.fertilizer = fertilizer;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCropsId() {
        return cropsId;
    }

    public void setCropsId(String cropsId) {
        this.cropsId = cropsId;
    }
}
