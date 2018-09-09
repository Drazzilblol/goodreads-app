package com.training.dr.androidtraining.ulils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.widget.ImageView;

import com.training.dr.androidtraining.R;
import com.training.dr.androidtraining.presentation.book_details.BookDetails;
import com.training.dr.androidtraining.presentation.favored.FavoredBooks;
import com.training.dr.androidtraining.presentation.introduction.IntroductionActivity;
import com.training.dr.androidtraining.presentation.login.LoginActivity;
import com.training.dr.androidtraining.presentation.main.MainActivity;
import com.training.dr.androidtraining.presentation.search.SearchActivity;
import com.training.dr.androidtraining.ulils.db.DataBaseUtils;


public class Navigator {

    public static void goToIntroductionScreen(Context context) {
        Intent i = new Intent(context, IntroductionActivity.class);
        context.startActivity(i);
    }

    public static void goToSearchScreen(Activity activity) {
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(activity);
        Intent intent = new Intent(activity, SearchActivity.class);
        activity.startActivity(intent, options.toBundle());
    }

    public static void goToLoginScreen(Context context) {
        Intent i = new Intent(context, LoginActivity.class);
        context.startActivity(i);
    }

    public static void goToMainScreen(Context context) {
        Intent i = new Intent(context, MainActivity.class);
        context.startActivity(i);
    }

    public static void goToFavoredBooksScreen(Activity activity) {
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(activity);
        Intent intent = new Intent(activity, FavoredBooks.class);
        activity.startActivity(intent, options.toBundle());
    }

    public static void goToMainScreenFromLogin(Context context) {
        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);
    }

    public static void goToLoginScreenFromMain(Context context) {
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);
    }

    public static void goToBookDetailsScreen(Activity activity, int bookId) {
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(activity);
        Intent i = new Intent(activity, BookDetails.class);
        i.putExtra(DataBaseUtils.BOOK_ID, bookId);
        activity.startActivity(i, options.toBundle());
    }

    public static void goToBookDetailsScreenWithTransition(Activity activity, int bookId) {
        Intent i = new Intent(activity, BookDetails.class);
        i.putExtra(DataBaseUtils.BOOK_ID, bookId);
        ImageView im = (ImageView) activity.findViewById(bookId);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(activity, im, activity.getString(R.string.transition_name));
        activity.startActivity(i, options.toBundle());
    }

}
