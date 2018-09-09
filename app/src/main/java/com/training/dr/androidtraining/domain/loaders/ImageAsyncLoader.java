package com.training.dr.androidtraining.domain.loaders;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.training.dr.androidtraining.R;
import com.training.dr.androidtraining.ulils.image.BitmapTransformation;
import com.training.dr.androidtraining.ulils.image.cache.Cache;
import com.training.dr.androidtraining.ulils.image.cache.DiskLruImageCache;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ImageAsyncLoader extends AsyncTask<String, Integer, Bitmap> {

    private static final String TAG = ImageAsyncLoader.class.getSimpleName();

    public static final Executor THREAD_POOL_EXECUTOR = Executors.newFixedThreadPool(2);
    private ImageView imageView;
    private int imageWidth;
    private int imageHeight;
    private boolean rounded;

    public ImageAsyncLoader(@Nullable ImageView view, int imageWidth, int imageHeight, boolean rounded) {
        imageView = view;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.rounded = rounded;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap resizedBitmap = null;
        InputStream stream = null;
        try {
            publishProgress(1);
            URLConnection conn = new URL(params[0]).openConnection();
            conn.connect();
            stream = conn.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            resizedBitmap = BitmapTransformation.transform(bitmap, imageWidth, imageHeight, rounded);
            stream.close();

            Cache.getInstance().putBitmapInCache(params[0] + imageWidth + imageHeight, resizedBitmap);
            DiskLruImageCache diskLruImageCache = DiskLruImageCache.getInstance();
            if (DiskLruImageCache.isEnabled() && diskLruImageCache != null) {
                diskLruImageCache.put(params[0], bitmap);
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            try {
                if (stream != null)
                    stream.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return resizedBitmap;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (values[0] > 0) {
            imageView.setImageResource(R.drawable.loading_placeholder);
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
