package com.training.dr.androidtraining.ulils.image.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;
import com.training.dr.androidtraining.ulils.image.ImageUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DiskLruImageCache {

    private DiskLruCache diskCache;
    private static DiskLruImageCache diskLruImageCache;
    private Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
    private int compressQuality = 100;
    private static final int APP_VERSION = 1;
    private static final int VALUE_COUNT = 1;
    private static int cacheSize = 1024 * 1024 * 8 * 50;
    private static File cacheFolder;

    private static volatile boolean enabled = false;


    public static DiskLruImageCache getInstance() {
        DiskLruImageCache result = diskLruImageCache;
        if (result == null) {
            synchronized (DiskLruImageCache.class) {
                result = diskLruImageCache;
                if (result == null) {
                    diskLruImageCache = result = new DiskLruImageCache();
                }
            }
        }
        return result;
    }

    public static void setCacheSize(int cacheSize) {
        DiskLruImageCache.cacheSize = cacheSize;
    }

    public static void setCacheFolder(File cacheFolder) {
        DiskLruImageCache.cacheFolder = cacheFolder;
    }

    private DiskLruImageCache() {
        initCache();
    }

    private void initCache() {
        try {
            diskCache = DiskLruCache.open(cacheFolder, APP_VERSION, VALUE_COUNT, cacheSize);
        } catch (IOException e) {
            Log.e("Disk LRU Cache", e.getMessage());
        }
    }

    private boolean writeBitmapToFile(Bitmap bitmap, DiskLruCache.Editor editor) {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(editor.newOutputStream(0), ImageUtils.IO_BUFFER_SIZE);
            return bitmap.compress(compressFormat, compressQuality, out);
        } catch (IOException e) {
            Log.e("Disk LRU Cache", e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Log.e("Disk LRU Cache", e.getMessage());
                }
            }
        }
        return false;
    }

    public void put(@NonNull String key, @NonNull Bitmap data) {

        DiskLruCache.Editor editor = null;
        try {
            int hash = key.hashCode();
            editor = diskCache.edit(Integer.toString(hash));
            if (editor == null) {
                return;
            }

            if (writeBitmapToFile(data, editor)) {
                diskCache.flush();
                editor.commit();
            } else {
                editor.abort();
            }
        } catch (IOException e) {
            try {
                if (editor != null) {
                    editor.abort();
                }
            } catch (IOException ignored) {
            }
        }
    }

    public Bitmap getBitmap(@NonNull String key) {
        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapshot = null;
        try {

            int hash = key.hashCode();
            snapshot = diskCache.get(Integer.toString(hash));
            if (snapshot == null) {
                return null;
            }
            final InputStream in = snapshot.getInputStream(0);
            if (in != null) {
                final BufferedInputStream buffIn =
                        new BufferedInputStream(in, ImageUtils.IO_BUFFER_SIZE);
                bitmap = BitmapFactory.decodeStream(buffIn);
            }
        } catch (IOException e) {
            Log.e("Disk LRU Cache", e.getMessage());
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
        }
        return bitmap;

    }

    public boolean containsKey(@NonNull String key) {

        int hash = key.hashCode();
        boolean contained = false;
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = diskCache.get(Integer.toString(hash));
            contained = snapshot != null;
        } catch (IOException e) {
            Log.e("Disk LRU Cache", e.getMessage());
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
        }
        return contained;
    }

    public static void enable(boolean e) {
        enabled = e;
    }

    public static boolean isEnabled() {
        return enabled;
    }

}

