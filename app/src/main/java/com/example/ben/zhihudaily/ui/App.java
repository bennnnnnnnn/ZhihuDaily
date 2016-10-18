package com.example.ben.zhihudaily.ui;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;
import android.util.DisplayMetrics;

import com.example.ben.zhihudaily.BuildConfig;
import com.example.ben.zhihudaily.utils.SharePreUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.litesuits.orm.LiteOrm;

/**
 * Created by Zhou bangquan on 16/9/28.
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
    public static float screenDensityDpiRadio = 1;
    // 字体缩放比例
    public static float scaledDensity = 1;

    private static final String ZHIHU_DB_NAME = "zhihu.db";

    public static String ZHIHU_MODE = "zhihu_mode";

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

        boolean isNightMode = (boolean) SharePreUtils.get(this, App.ZHIHU_MODE, false);
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        initConfigues();
    }

    public static Context getInstance() {
        return mContext;
    }

    private void initConfigues() {
        DisplayMetrics display = this.getResources().getDisplayMetrics();
        screenWidth = display.widthPixels;
        screenHeight = display.heightPixels;
        screenDensityDpi = display.densityDpi;
        screenDensityDpiRadio = display.density;
        scaledDensity = display.scaledDensity;
    }

}
