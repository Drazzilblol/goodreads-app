package com.training.dr.androidtraining.presentation.common.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.training.dr.androidtraining.R;

/**
 * Created by dr on 05.02.2017.
 */

public class CustomRatingBar extends View implements View.OnTouchListener {

    private static final boolean DEFAULT_IS_INDICATOR = false;
    private static final float DEFAULT_RATING = 0.0f;
    private static final int DEFAULT_MAX_COUNT = 5;
    private static final int DEFAULT_DRAWABLE_SIZE_IN_DP = 24;
    private static final int DEFAULT_DRAWABLE_MARGIN_IN_DP = 4;

    private OnRatingChangedListener onRatingChangedListener;
    private Bitmap drawableEmpty, drawableHalf, drawableFilled;
    private Rect rect = new Rect();
    private boolean isIndicator;

    @ColorInt
    private int starColor;
    @ColorInt
    private int indicatorStarColor;
    private float rating;
    private int maxCount;
    private int drawableSize;
    private int drawableMargin;
    private Paint paint;

    public interface OnRatingChangedListener {
        void onRatingChange(float newRating);
    }

    public CustomRatingBar(Context context) {
        super(context);
        init(null, 0, 0);
    }

    public CustomRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, 0);
    }

    public CustomRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, 0);
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = getContext().obtainStyledAttributes
                (attrs, R.styleable.CustomRatingBar, defStyleAttr, defStyleRes);
        rating = a.getFloat(R.styleable.CustomRatingBar_rating, DEFAULT_RATING);
        isIndicator = a.getBoolean(R.styleable.CustomRatingBar_is_indicator, DEFAULT_IS_INDICATOR);
        initDrawables(a);
        validateStarCount(a);
        initPaint();
        a.recycle();
        if (!isIndicator) {
            setOnTouchListener(this);
        }
    }

    private void initDrawables(TypedArray a) {
        drawableMargin = (int) a.getDimension(R.styleable.CustomRatingBar_drawable_margin,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_DRAWABLE_MARGIN_IN_DP, getResources().getDisplayMetrics()));
        drawableSize = (int) a.getDimension(R.styleable.CustomRatingBar_drawable_size,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_DRAWABLE_SIZE_IN_DP, getResources().getDisplayMetrics()));
        if (drawableSize < 0) {
            throw new IllegalArgumentException("Drawable size < 0");
        }
        drawableEmpty = BitmapFactory.decodeResource(getContext().getResources(),
                a.getResourceId(R.styleable.CustomRatingBar_drawable_empty, R.drawable.ic_star_empty));
        drawableHalf = BitmapFactory.decodeResource(getContext().getResources(),
                a.getResourceId(R.styleable.CustomRatingBar_drawable_half, R.drawable.ic_star_half));
        drawableFilled = BitmapFactory.decodeResource(getContext().getResources(),
                a.getResourceId(R.styleable.CustomRatingBar_drawable_filled, R.drawable.ic_star_filled));
        starColor = a.getColor(R.styleable.CustomRatingBar_star_color, getResources().getColor(R.color.colorAccent));
        indicatorStarColor = getResources().getColor(R.color.colorSecondaryText);
    }

    private void validateStarCount(TypedArray a) {
        maxCount = a.getInteger(R.styleable.CustomRatingBar_max_count, DEFAULT_MAX_COUNT);
        if (maxCount < 3) {
            maxCount = 3;
        }
        if (maxCount > 10) {
            maxCount = 10;
        }
    }

    private void initPaint() {
        int color;
        if (isIndicator) {
            color = indicatorStarColor;
        } else {
            color = starColor;
        }
        paint = new Paint();
        ColorFilter filter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
        paint.setColorFilter(filter);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(resolveSize((drawableSize * maxCount) + (drawableMargin * (maxCount - 1)), widthMeasureSpec),
                resolveSize(drawableSize, heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (drawableFilled != null && drawableHalf != null && drawableEmpty != null) {
            rect.set(0, 0, drawableSize, drawableSize);
            int fullDrawablesCount = (int) rating;
            int emptyDrawablesCount = maxCount - Math.round(rating);

            if (rating - fullDrawablesCount >= 0.75f)
                fullDrawablesCount++;

            for (int i = 0; i < fullDrawablesCount; i++) {
                canvas.drawBitmap(drawableFilled, null, rect, paint);
                rect.offset(drawableSize + drawableMargin, 0);
            }

            if (rating - fullDrawablesCount >= 0.25f && rating - fullDrawablesCount < 0.75f) {
                canvas.drawBitmap(drawableHalf, null, rect, paint);
                rect.offset(drawableSize + drawableMargin, 0);
            }

            for (int i = 0; i < emptyDrawablesCount; i++) {
                canvas.drawBitmap(drawableEmpty, null, rect, paint);
                rect.offset(drawableSize + drawableMargin, 0);
            }

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP:
                setRating(Math.round(event.getX() / getWidth() * maxCount + 0.5));
                return false;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setOnRatingChangedListener(OnRatingChangedListener listener) {
        onRatingChangedListener = listener;
    }

    public void setIsIndicator(boolean isIndicator) {
        this.isIndicator = isIndicator;
        setOnTouchListener(this.isIndicator ? null : this);
        initPaint();
        invalidate();
    }

    public void setRating(float rating) {
        if (rating != this.rating) {
            float newRating = rating;
            if (newRating < 0) {
                newRating = 0;
            } else if (newRating > maxCount) {
                newRating = maxCount;
            }
            if (onRatingChangedListener != null)
                onRatingChangedListener.onRatingChange(newRating);
            this.rating = newRating;
            invalidate();
        }
    }

    public void setDrawableEmpty(Bitmap drawableEmpty) {
        this.drawableEmpty = drawableEmpty;
        invalidate();
    }

    public void setDrawableHalf(Bitmap drawableHalf) {
        this.drawableHalf = drawableHalf;
        invalidate();
    }

    public void setDrawableFilled(Bitmap drawableFilled) {
        this.drawableFilled = drawableFilled;
        invalidate();
    }

    public boolean isIndicator() {
        return isIndicator;
    }

    public float getRating() {
        return rating;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public int getDrawableSize() {
        return drawableSize;
    }

    public int getDrawableMargin() {
        return drawableMargin;
    }


}
