package com.training.dr.androidtraining.data.models;

import android.os.Parcel;
import android.os.Parcelable;


public class Book implements Item {

    private String title;
    private String author;
    private String imageUrl;
    private String year;
    private float rating;
    private int id;
    private int goodreadId;
    private int reviewId;
    private String description;
    private String created;
    private int myRating;
    private int favoriteId;

    public int getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(int favoriteId) {
        this.favoriteId = favoriteId;
    }

    public int getMyRating() {
        return myRating;
    }

    public void setMyRating(int myRating) {
        this.myRating = myRating;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public int getGoodreadId() {
        return goodreadId;
    }

    public void setGoodreadId(int goodreadId) {
        this.goodreadId = goodreadId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public Book() {
        this.title = "";
        this.author = "";
        this.imageUrl = "";
        this.year = "";
        this.rating = 0;
        this.id = 0;
        this.description = "";
        this.goodreadId = 0;
        this.reviewId = 0;
        this.created = "";
        this.myRating = 0;
        this.favoriteId = 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(imageUrl);
        dest.writeString(year);
        dest.writeFloat(rating);
        dest.writeInt(id);
        dest.writeInt(goodreadId);
        dest.writeInt(reviewId);
        dest.writeString(description);
        dest.writeString(created);
        dest.writeInt(myRating);
        dest.writeInt(favoriteId);
    }

    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    private Book(Parcel p) {
        this.title = p.readString();
        this.author = p.readString();
        this.imageUrl = p.readString();
        this.year = p.readString();
        this.rating = p.readFloat();
        this.id = p.readInt();
        this.goodreadId = p.readInt();
        this.reviewId = p.readInt();
        this.description = p.readString();
        this.created = p.readString();
        this.myRating = p.readInt();
        this.favoriteId = p.readInt();
    }

}
