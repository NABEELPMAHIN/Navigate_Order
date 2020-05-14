package com.nabeel.navigateorder.model;

import com.google.gson.annotations.SerializedName;

public class Order implements Comparable{

    @SerializedName("id")
    private String id;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("name")
    private String name;

    @SerializedName("avatar")
    private String avatar;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitute")
    private String longitute;

    @SerializedName("otp")
    private int otp;

    public Order(String id, String createdAt, String name, String avatar, String latitude, String longitute, int otp) {
        this.id = id;
        this.createdAt = createdAt;
        this.name = name;
        this.avatar = avatar;
        this.latitude = latitude;
        this.longitute = longitute;
        this.otp = otp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitute() {
        return longitute;
    }

    public void setLongitute(String longitute) {
        this.longitute = longitute;
    }

    public int getOtp() {
        return otp;
    }

    public void setOtp(int otp) {
        this.otp = otp;
    }

    @Override
    public int compareTo(Object o) {
        Order order=(Order) o;
        String createdAt=order.getCreatedAt();

        return this.createdAt.compareTo(createdAt);
    }
}
