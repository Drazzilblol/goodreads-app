package com.training.dr.androidtraining.presentation.common.holders;

import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.training.dr.androidtraining.BuildConfig;
import com.training.dr.androidtraining.R;
import com.training.dr.androidtraining.data.api.ApiMethods;
import com.training.dr.androidtraining.data.models.Book;
import com.training.dr.androidtraining.data.services.ApiDeleteService;
import com.training.dr.androidtraining.data.services.ApiPutService;
import com.training.dr.androidtraining.presentation.common.views.CustomRatingBar;
import com.training.dr.androidtraining.presentation.common.events.OnBookItemClickListener;
import com.training.dr.androidtraining.ulils.Utils;
import com.training.dr.androidtraining.ulils.image.ImageLoadingManager;

import java.lang.ref.WeakReference;


public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CustomRatingBar.OnRatingChangedListener {
    private WeakReference<OnBookItemClickListener> callbackReference;
    private ImageLoadingManager loadingManager;
    private CardView cv;
    private TextView bookTitle;
    private TextView bookAuthor;
    private ImageView bookCover;
    private TextView bookDescription;
    private CustomRatingBar ratingBar;
    private Button btnDelete;
    private Book book;


    public ItemViewHolder(View itemView, OnBookItemClickListener callback) {
        super(itemView);
        itemView.setOnClickListener(this);
        callbackReference = new WeakReference<>(callback);
        bookTitle = (TextView) itemView.findViewById(R.id.card_title_view);
        bookCover = (ImageView) itemView.findViewById(R.id.book_cover);
        bookAuthor = (TextView) itemView.findViewById(R.id.card_author_view);
        bookDescription = (TextView) itemView.findViewById(R.id.card_description_view);
        ratingBar = (CustomRatingBar) itemView.findViewById(R.id.cv_rating_bar);
        btnDelete = (Button) itemView.findViewById(R.id.cv_button_delete);
    }

    public void onBindData(Book book, String search) {
        this.book = book;
        ratingBar.setOnRatingChangedListener(null);
        ratingBar.setIsIndicator(false);
        bookTitle.setText(Utils.highlightText(search, book.getTitle()));
        bookAuthor.setText(book.getAuthor());
        loadingManager = ImageLoadingManager.startBuild()
                .imageUrl(book.getImageUrl())
                .placeholder(R.drawable.book_image_paceholder)
                .transform(400, 600)
                .load(bookCover);
        bookCover.setId(book.getId());
        bookDescription.setText(book.getDescription());
        if (book.getMyRating() != 0) {
            ratingBar.setRating(book.getMyRating());
        } else {
            ratingBar.setRating(book.getRating());
        }
        ratingBar.setOnRatingChangedListener(this);
        btnDelete.setOnClickListener(this);
    }

    public void cancelLoad() {
        loadingManager.cancelLoader();
    }

    @Override
    public void onClick(View v) {
        OnBookItemClickListener clickListener = callbackReference.get();
        if (v.getId() == itemView.getId()) {
            if (clickListener != null) {
                clickListener.onClick(v, getAdapterPosition());
            }
        }
        if (v.getId() == btnDelete.getId()) {
            Intent intent = new Intent(Intent.ACTION_SYNC, null, itemView.getContext(), ApiDeleteService.class);
            intent.putExtra("url", BuildConfig.BASE_URL + ApiMethods.DELETE + book.getReviewId() + "?format=xml");
            intent.putExtra("method", ApiDeleteService.DELETE_BOOK);
            intent.putExtra("id", book.getReviewId());
            itemView.getContext().startService(intent);
        }

    }

    private void startApiService(int rating) {
        Intent intent = new Intent(Intent.ACTION_SYNC, null, itemView.getContext(), ApiPutService.class);
        intent.putExtra("url", BuildConfig.BASE_URL + ApiMethods.REVIEW + book.getReviewId() + ".xml");
        intent.putExtra("method", ApiPutService.RATE_BOOK);
        intent.putExtra("rate", rating);
        intent.putExtra("id", book.getReviewId());
        itemView.getContext().startService(intent);
    }

    @Override
    public void onRatingChange(float newRating) {
        ratingBar.setIsIndicator(true);
        startApiService((int) newRating);
    }
}
