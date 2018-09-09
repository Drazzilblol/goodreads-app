package com.training.dr.androidtraining.ulils.image;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.training.dr.androidtraining.ulils.StorageUtils;
import com.training.dr.androidtraining.ulils.image.cache.Cache;
import com.training.dr.androidtraining.ulils.image.cache.DiskLruImageCache;

import java.io.File;


public class CacheImageConfigManager {

    public static final String DEFAULT_CACHE_FOLDER_NAME = "cache";

    public static class ConfigBuilder {
        private int ramCacheSize;
        private int diskCacheSize;
        private String cacheFolder;
        private Context context;
        private boolean enabled;

        private ConfigBuilder(Context context) {
            this.context = context;
        }

        public ConfigBuilder ramCacheSize(int size) {
            this.ramCacheSize = size;
            return this;
        }

        public ConfigBuilder diskCacheFolderName(@NonNull String folderName) {
            this.cacheFolder = folderName;
            return this;
        }

        public ConfigBuilder enableDiskCache(boolean e) {
            this.enabled = e;
            return this;
        }

        public ConfigBuilder diskCacheSize(int size) {
            this.diskCacheSize = size;
            return this;
        }

        public CacheImageConfigManager build() {
            return new CacheImageConfigManager(this);
        }

    }

    private CacheImageConfigManager(ConfigBuilder builder) {
        if (builder.diskCacheSize > 0) {
            DiskLruImageCache.setCacheSize(builder.diskCacheSize);
        }
        if (builder.ramCacheSize > 0) {
            Cache.setCacheSize(builder.ramCacheSize);
        }

        DiskLruImageCache.setCacheFolder(getDiskCacheDir(builder.context, builder.cacheFolder));

        DiskLruImageCache.enable(builder.enabled);
    }

    public static ConfigBuilder buildConfig(@NonNull Context context) {
        return new ConfigBuilder(context);
    }

    private File getDiskCacheDir(Context context, String uniqueName) {
        if (uniqueName == null || TextUtils.equals(uniqueName, "")) {
            uniqueName = DEFAULT_CACHE_FOLDER_NAME;
        }

        String cachePath = StorageUtils.isExternalStorageAvailable()
                ? context.getExternalCacheDir().getPath()
                : context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }
}
