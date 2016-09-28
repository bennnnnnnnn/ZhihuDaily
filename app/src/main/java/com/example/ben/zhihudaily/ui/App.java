package com.example.ben.zhihudaily.ui;

import android.app.Application;
import android.util.DisplayMetrics;

import com.example.ben.zhihudaily.BuildConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.litesuits.orm.LiteOrm;

/**
 * Created by Zhou bangquan on 16/9/28.
 */


public class App extends Application {

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

    public static LiteOrm mDb;

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        mDb = LiteOrm.newCascadeInstance(this, ZHIHU_DB_NAME);
        if (BuildConfig.DEBUG) {
            mDb.setDebugged(true);
        }
        initConfigues();
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
