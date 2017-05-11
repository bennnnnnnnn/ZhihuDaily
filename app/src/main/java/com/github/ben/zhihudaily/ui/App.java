package com.github.ben.zhihudaily.ui;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;
import android.util.DisplayMetrics;

import com.github.ben.zhihudaily.BuildConfig;
import com.github.ben.zhihudaily.utils.SharePreUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.litesuits.orm.LiteOrm;

import io.reactivex.plugins.RxJavaPlugins;

import static com.github.ben.zhihudaily.data.ResponseError.displayCustomErrorConsumer;

/**
 * Created on 16/9/28.
 * @author Ben
 */


public class App extends Application {

    public static Context mContext;
    // 屏幕宽度
    public static int screenWidth = 480;
    // 屏幕高度
    public static int screenHeight = 800;
    // 屏幕密度dpi
    public static int screenDensityDpi = 160;
    // 屏幕密度dpi比例
    public static float screenDensity = 1;
    // 字体缩放比例
    public static float scaledScaledDensity = 1;

    public static int statusBarHeight = 0;

    private static final String ZHIHU_DB_NAME = "zhihu.db";

    public static String NIGHT_MODE = "isNight";

    public static LiteOrm mDb;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        Fresco.initialize(this);
        mDb = LiteOrm.newCascadeInstance(this, ZHIHU_DB_NAME);
        if (BuildConfig.DEBUG) {
            mDb.setDebugged(true);
        }

        boolean isNightMode = SharePreUtils.isNight();
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        initStatusBarHeight();
        initConfigues();

        RxJavaPlugins.setErrorHandler(displayCustomErrorConsumer(this));
    }

    public static Context getInstance() {
        return mContext;
    }

    private void initStatusBarHeight() {
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = mContext.getResources().getDimensionPixelSize(resourceId);
        }
    }

    private void initConfigues() {
        DisplayMetrics display = this.getResources().getDisplayMetrics();
        screenWidth = display.widthPixels;
        screenHeight = display.heightPixels;
        screenDensityDpi = display.densityDpi;
        screenDensity = display.density;
        scaledScaledDensity = display.scaledDensity;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mContext = null;
    }
}
