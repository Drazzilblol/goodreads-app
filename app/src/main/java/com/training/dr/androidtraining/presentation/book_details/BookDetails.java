package com.training.dr.androidtraining.presentation.book_details;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.training.dr.androidtraining.BuildConfig;
import com.training.dr.androidtraining.R;
import com.training.dr.androidtraining.data.api.ApiMethods;
import com.training.dr.androidtraining.data.models.Book;
import com.training.dr.androidtraining.data.services.ApiPutService;
import com.training.dr.androidtraining.domain.loaders.BookInfoCursorLoader;
import com.training.dr.androidtraining.presentation.common.customtabs.LinkTransformation;
import com.training.dr.androidtraining.presentation.common.views.CustomRatingBar;
import com.training.dr.androidtraining.ulils.NetworkUtils;
import com.training.dr.androidtraining.ulils.db.DataBaseUtils;
import com.training.dr.androidtraining.ulils.image.ImageLoadingManager;

import butterknife.BindView;

public class BookDetails extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, CustomRatingBar.OnRatingChangedListener {

    private static final String TAG = BookDetails.class.getSimpleName();

    private int bookId;
    private Cursor cursor;
    private Book book;
    private AlertDialog dialog;

    @BindView(R.id.book_details_rating)
    CustomRatingBar ratingBar;

    @BindView(R.id.book_details_toolbar)
    Toolbar toolbar;

    @BindView(R.id.book_details_author_view)
    TextView authorView;

    private ContentObserver contentObserver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
        setContentView(R.layout.activity_book_details);
        setupWindowAnimations();
        if (getIntent() != null && getIntent().getExtras() != null) {
            getStateFromExtras();
        }
        createDialog();
        initToolbar();
        getSupportLoaderManager().initLoader(0, null, this).forceLoad();
        contentObserver = new NotifiedContentObserver(new Handler());

    }

    private void setupWindowAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slideTransition = new Slide();
            slideTransition.setSlideEdge(Gravity.END);
            slideTransition.setDuration(500);
            getWindow().setEnterTransition(slideTransition);
            getWindow().setExitTransition(slideTransition);
        }
    }

    private void getStateFromExtras() {
        bookId = getIntent().getIntExtra(DataBaseUtils.BOOK_ID, 0);
    }

    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.network_is_not_available);
        builder.setCancelable(true);
        builder.setNegativeButton(R.string.close_button, (dialog, id) -> dialog.dismiss());
        dialog = builder.create();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        toolbarViewsFade();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void toolbarViewsFade() {
        AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            int maxScroll = appBarLayout1.getTotalScrollRange();
            float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;
            if (ratingBar != null && authorView != null) {
                ratingBar.setAlpha(1 - percentage);
                authorView.setAlpha(1 - percentage);
            }
        });
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (loader instanceof BookInfoCursorLoader) {
            cursor = data;
            if (this.cursor != null) {
                this.cursor.registerContentObserver(contentObserver);
            }
            if (cursor != null && cursor.getCount() > 0) {
                if (book != null) {
                    cursor.moveToFirst();
                    book.setMyRating(cursor.getInt(8));
                    ratingBar.setIsIndicator(false);
                } else {
                    getBookFromCursor();
                    initViews();
                }
            }
        } else {
            Log.w(TAG, "Wrong Loader");
        }
    }

    private void getBookFromCursor() {
        cursor.moveToFirst();
        book = new Book();
        book.setId(cursor.getInt(0));
        book.setGoodreadId(cursor.getInt(1));
        book.setReviewId(cursor.getInt(2));
        book.setTitle(cursor.getString(3));
        book.setImageUrl(cursor.getString(4));
        book.setAuthor(cursor.getString(5));
        book.setYear(cursor.getString(6));
        book.setRating(cursor.getFloat(7));
        book.setMyRating(cursor.getInt(8));
        book.setDescription(cursor.getString(9));
        book.setCreated(cursor.getString(10));
        book.setFavoriteId(cursor.getInt(11));
    }

    private void initViews() {
        authorView.setText(book.getAuthor());
        toolbar.setTitle(book.getTitle());
        TextView descriptionView = findViewById(R.id.book_details_description_view);
        descriptionView.setMovementMethod(LinkMovementMethod.getInstance());
        descriptionView.setTransformationMethod(new LinkTransformation());
        descriptionView.setText(book.getDescription());
        imageViewInit();
        initRatingViews();
        initFavoredButton();
    }

    private void imageViewInit() {
        ImageView imageView = findViewById(R.id.book_cover);

        ImageLoadingManager.startBuild()
                .imageUrl(book.getImageUrl())
                .placeholder(R.drawable.book_image_paceholder)
                .transform(500, 800)
                .load(imageView);

    }

    private void initRatingViews() {
        if (book.getMyRating() != 0) {
            ratingBar.setRating(book.getMyRating());
        } else {
            ratingBar.setRating(book.getRating());
        }
        ratingBar.setOnRatingChangedListener(this);
    }

    private void initFavoredButton() {
        final FloatingActionButton favBtn = findViewById(R.id.book_details_favorited_button);
        if (book.getFavoriteId() == 0) {
            favBtn.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        } else {
            favBtn.setImageResource(R.drawable.ic_favorite_black_24dp);
        }
        favBtn.setOnClickListener(v -> {
            if (book.getFavoriteId() == 0) {
                Uri uri = insertFavorited();
                if (uri == null) {
                    return;
                }
                favBtn.setImageResource(R.drawable.ic_favorite_black_24dp);
                updateBookFavoritedId(uri.getLastPathSegment());
            } else {
                favBtn.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                deleteFavorited();
                updateBookFavoritedId("0");
            }
        });
    }

    private Uri insertFavorited() {
        ContentValues values = new ContentValues();
        values.put(DataBaseUtils.FAVORED_BOOK_ID, book.getId());
        values.put(DataBaseUtils.FAVORED_TIMESTAMP, System.currentTimeMillis());
        return getContentResolver().insert(
                DataBaseUtils.FAVORED_URI, values
        );
    }

    private void updateBookFavoritedId(String id) {
        ContentValues values = new ContentValues();
        values.put(DataBaseUtils.BOOK_FAVORITES, id);
        Uri uri = ContentUris.withAppendedId(DataBaseUtils.BOOK_URI, book.getId());
        getContentResolver().update(uri, values, null, null);
        book.setFavoriteId(Integer.parseInt(id));
    }

    private void deleteFavorited() {
        Uri uri = ContentUris.withAppendedId(DataBaseUtils.BOOK_URI, book.getFavoriteId());
        getContentResolver().delete(uri, null, null);
    }

    @Override
    public void onRatingChange(float newRating) {
        ratingBar.setIsIndicator(true);

        startApiService((int) newRating);
    }

    private void startApiService(int rating) {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            dialog.show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, ApiPutService.class);
        intent.putExtra("url", BuildConfig.BASE_URL + ApiMethods.REVIEW + book.getReviewId() + ".xml");
        intent.putExtra("method", ApiPutService.RATE_BOOK);
        intent.putExtra("rate", rating);
        intent.putExtra("id", book.getReviewId());
        startService(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            if (!ratingBar.isIndicator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else {
                    finish();
                }
            } else {
                Toast.makeText(this, getString(R.string.rating_updating), Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == 0) {
            return new BookInfoCursorLoader(this, bookId);
        }
        return null;
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cursor != null) {
            cursor.unregisterContentObserver(contentObserver);
        }
    }

    @Override
    public void onBackPressed() {
        if (!ratingBar.isIndicator()) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, getString(R.string.rating_updating), Toast.LENGTH_SHORT).show();
        }
    }

    private class NotifiedContentObserver extends ContentObserver {

        private NotifiedContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            getSupportLoaderManager().restartLoader(0, null, BookDetails.this);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }
    }
}
