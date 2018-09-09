package com.training.dr.androidtraining.presentation.common.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.training.dr.androidtraining.R;

/**
 * Created by dr on 05.02.2017.
 */

public class Dots extends View {

    private static final int DEFAULT_COUNT = 3;
    private static final int DEFAULT_SIZE_IN_DP = 10;
    private static final int DEFAULT_MARGIN_IN_DP = 4;
    private static final int DEFAULT_CHECKED_COLOR = Color.WHITE;
    private static final int DEFAULT_UNCHECKED_COLOR = Color.parseColor("#555555");

    private int count;
    private int size;
    private int margin;
    private Paint paint;
    private int checkedPosition;

    @ColorInt
    private int checkedColor;
    @ColorInt
    private int uncheckedColor;

    public Dots(Context context) {
        super(context);
        init(null, 0, 0);
    }

    public Dots(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, 0);
    }

    public Dots(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, 0);
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = getContext().obtainStyledAttributes
                (attrs, R.styleable.CustomRatingBar, defStyleAttr, defStyleRes);
        count = a.getInteger(R.styleable.Dots_count, DEFAULT_COUNT);
        sizeInit(a);
        colorInit(a);
        initPaint();
        a.recycle();
    }

    private void sizeInit(TypedArray a) {
        margin = (int) a.getDimension(R.styleable.Dots_dots_margin,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_MARGIN_IN_DP, getResources().getDisplayMetrics()));
        size = (int) a.getDimension(R.styleable.Dots_dots_size,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_SIZE_IN_DP, getResources().getDisplayMetrics()));

        if (size < 0) {
            throw new IllegalArgumentException("Drawable size < 0");
        }
    }

    private void colorInit(TypedArray a) {
        checkedColor = a.getColor(R.styleable.Dots_checked_color, DEFAULT_CHECKED_COLOR);
        uncheckedColor = a.getColor(R.styleable.Dots_unchecked_color, DEFAULT_UNCHECKED_COLOR);
    }

    private void initPaint() {
        paint = new Paint();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(resolveSize((size * count) + (margin * (count - 1)), widthMeasureSpec),
                resolveSize(size, heightMeasureSpec));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int position = 0;
        for (int i = 0; i < count; i++) {
            paint.setColor(position == checkedPosition ? checkedColor : uncheckedColor);
            float cx = i * size + size / 2 + margin * i;
            float cy = size / 2;
            canvas.drawCircle(cx, cy, size / 2, paint);
            position++;
        }
    }


    public void setCheckedPosition(int checkedPosition) {
        this.checkedPosition = checkedPosition;
        invalidate();
    }

}
