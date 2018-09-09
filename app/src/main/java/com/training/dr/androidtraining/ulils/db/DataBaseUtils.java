package com.training.dr.androidtraining.ulils.db;

import android.net.Uri;

public final class DataBaseUtils {
    private DataBaseUtils() {
    }

    public static final String BOOK_ID = "_id";
    public static final String BOOK_GOODREAD_ID = "goodread_id";
    public static final String BOOK_TITLE = "title";
    public static final String BOOK_AUTHOR = "author";
    public static final String BOOK_IMAGE_URL = "image_url";
    public static final String BOOK_YEAR = "year";
    public static final String BOOK_RATING = "rating";
    public static final String BOOK_DESCRIPTION = "description";
    public static final String BOOK_CREATED = "created";
    public static final String BOOK_MY_RATING = "my_rating";
    public static final String BOOK_FAVORITES = "favorites";
    public static final String BOOK_REVIEW_ID = "review_id";

    public static final String USER_GOODREAD_ID = "user_goodread_id";
    public static final String USER_NAME = "name";
    public static final String USER_AVATAR_URL = "avatar_url";

    public static final String RATING_ID = "id";
    public static final String RATING_BOOK_ID = "book_id";
    public static final String RATING_RATE = "rate";
    public static final String RATING_RATE_TIMESTAMP = "timestamp";

    public static final String FAVORED_ID = "id";
    public static final String FAVORED_BOOK_ID = "book_id";
    public static final String FAVORED_TIMESTAMP = "timestamp";

    public static final Uri BOOK_URI = Uri
            .parse("content://com.training.dr.androidtraining.BookList/books/");
    public static final Uri RATINGS_URI = Uri
            .parse("content://com.training.dr.androidtraining.RatingList/ratings/");
    public static final Uri FAVORED_URI = Uri
            .parse("content://com.training.dr.androidtraining.FavoredList/favorites/");

    public static final String AUTHORITY_BOOKS = "com.training.dr.androidtraining.BookList";
    public static final String AUTHORITY_RATINGS = "com.training.dr.androidtraining.RatingList";
    public static final String AUTHORITY_FAVORITED = "com.training.dr.androidtraining.FavoredList";

}
