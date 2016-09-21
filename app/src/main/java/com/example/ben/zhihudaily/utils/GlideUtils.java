package com.example.ben.zhihudaily.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by Zhou bangquan on 16/9/11.
 */

public class GlideUtils {

    public GlideUtils() {
    }

    public static void loadingImage(Context context, ImageView imageView, String url) {
        if (null == context || ((Activity) context).isFinishing()) {
            return;
        }
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }
}
