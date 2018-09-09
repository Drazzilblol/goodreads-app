package com.training.dr.androidtraining.ulils.image;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;


public class BitmapTransformation {

    public static Bitmap transform(Bitmap bitmap, int imageWidth, int imageHeight, boolean rounded) {
        float scale = calculateScale(bitmap, imageWidth, imageHeight);
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap resizedBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (rounded) {
            return getCroppedBitmap(resizedBmp, bitmap.getWidth());
        }
        return resizedBmp;

    }

    private static float calculateScale(
            Bitmap bitmap, int reqWidth, int reqHeight) {
        if (reqHeight == 0 || reqWidth == 0) {
            return 1;
        }

        final int height = bitmap.getHeight();
        final int width = bitmap.getWidth();
        float scale = 1;

        if (height > reqHeight || width > reqWidth) {

            final double halfHeight = height / 1.2;
            final double halfWidth = width / 1.2;

            while ((halfHeight * scale) >= reqHeight
                    && (halfWidth * scale) >= reqWidth) {
                scale /= 1.2;
            }
        }
        return scale;
    }

    private static Bitmap getCroppedBitmap(Bitmap bmp, int diameter) {
        Bitmap sbmp;

        if (bmp.getWidth() != diameter || bmp.getHeight() != diameter) {
            float smallest = Math.min(bmp.getWidth(), bmp.getHeight());
            float factor = smallest / diameter;
            sbmp = Bitmap.createScaledBitmap(bmp,
                    (int) (bmp.getWidth() / factor),
                    (int) (bmp.getHeight() / factor), false);
        } else {
            sbmp = bmp;
        }

        Bitmap output = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final String color = "#BAB399";
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, diameter, diameter);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor(color));
        canvas.drawCircle(diameter / 2, diameter / 2,
                diameter / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);

        return output;
    }
}
