package com.training.dr.androidtraining.ulils.image.cache;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;


public class Cache {

    private static LruCache<String, Bitmap> cache;
    private static volatile Cache instance;
    private static int cacheSize = ((int) (Runtime.getRuntime().maxMemory() / 1024)) / 8;

    private Cache() {
        cache = new LruCache<>(cacheSize);
    }

    public static Cache getInstance() {
        Cache result = instance;
        if (result == null) {
            synchronized (Cache.class) {
                result = instance;
                if (result == null) {
                    instance = result = new Cache();
                }
            }
        }
        return result;
    }

    public void putBitmapInCache(@NonNull String key, @NonNull Bitmap bitmap) {
        if (getBitmapFromCache(key) == null)
            cache.put(key, bitmap);
    }

    public Bitmap getBitmapFromCache(@NonNull String key) {
        return cache.get(key);
    }

    public boolean containsKey(@NonNull String key) {
        return cache.get(key) != null;
    }

    public static void setCacheSize(int cacheSize) {
        Cache.cacheSize = cacheSize;
    }
}
