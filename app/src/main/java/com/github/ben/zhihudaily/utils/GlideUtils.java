package com.github.ben.zhihudaily.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created on 16/9/11.
 * @author Ben
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
