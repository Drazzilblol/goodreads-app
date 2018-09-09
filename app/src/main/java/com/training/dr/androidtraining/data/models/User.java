package com.training.dr.androidtraining.data.models;

import android.os.Parcel;
import android.os.Parcelable;


public class User implements Parcelable {
    private String name;
    private String avatarUrl;
    private int goodreadId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getGoodreadId() {
        return goodreadId;
    }

    public void setGoodreadId(int goodreadId) {
        this.goodreadId = goodreadId;
    }

    public User() {
        this.goodreadId = 0;
        this.name = "";
        this.avatarUrl = "";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(goodreadId);
        dest.writeString(name);
        dest.writeString(avatarUrl);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    private User(Parcel p) {
        this.goodreadId = p.readInt();
        this.name = p.readString();
        this.avatarUrl = p.readString();
    }

}
