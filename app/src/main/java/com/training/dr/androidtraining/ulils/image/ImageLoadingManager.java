package com.training.dr.androidtraining.ulils.image;

import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.training.dr.androidtraining.R;
import com.training.dr.androidtraining.domain.loaders.ImageAsyncLoader;
import com.training.dr.androidtraining.ulils.image.cache.Cache;
import com.training.dr.androidtraining.ulils.image.cache.DiskLruImageCache;

import java.lang.ref.WeakReference;


public final class ImageLoadingManager {
    private ImageAsyncLoader loader;

    public static class ImageBuilder {
        private String url;
        private int placeholder = 0;
        private WeakReference<ImageView> viewReference;
        private int height;
        private int width;
        private boolean rounded = false;

        private ImageBuilder() {
        }

        public ImageBuilder transform(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public ImageBuilder imageUrl(@NonNull String url) {
            this.url = url;
            return this;
        }

        public ImageBuilder placeholder(@DrawableRes int placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        public ImageBuilder rounded(boolean rounded) {
            this.rounded = rounded;
            return this;
        }

        public ImageLoadingManager load(@NonNull ImageView view) {
            this.viewReference = new WeakReference<>(view);
            return new ImageLoadingManager(this);
        }
    }

    public static ImageBuilder startBuild() {
        return new ImageBuilder();
    }

    private ImageLoadingManager(ImageBuilder builder) {
        loadImage(builder.url, builder.viewReference, builder.placeholder, builder.width, builder.height, builder.rounded);
    }

    private void loadImage(@Nullable String url,
                           @NonNull WeakReference<ImageView> viewRef,
                           @DrawableRes int placeholder,
                           int width,
                           int height,
                           boolean rounded) {
        ImageView view = viewRef.get();
        if (view == null) {
            return;
        }
        if (placeholder != 0) {
            view.setImageResource(placeholder);
        } else {
            view.setImageResource(R.drawable.book_image_paceholder);
        }

        if (url == null || TextUtils.equals(url, "")) {
            return;
        }

        Cache cache = Cache.getInstance();
        DiskLruImageCache diskLruImageCache = DiskLruImageCache.getInstance();
        if (cache != null && cache.containsKey(url + width + height)) {
            view.setImageBitmap(cache.getBitmapFromCache(url + width + height));
        } else if (DiskLruImageCache.isEnabled() && diskLruImageCache != null
                && diskLruImageCache.containsKey(url)) {
            Bitmap bitmap = BitmapTransformation.transform(
                    diskLruImageCache.getBitmap(url),
                    width,
                    height,
                    rounded);
            view.setImageBitmap(bitmap);
        } else {
            loader = new ImageAsyncLoader(view, width, height, rounded);
            loader.executeOnExecutor(ImageAsyncLoader.THREAD_POOL_EXECUTOR, url);
        }
    }

    public void cancelLoader() {
        if (loader != null && !loader.isCancelled())
            loader.cancel(true);
    }
}



