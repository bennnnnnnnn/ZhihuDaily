package com.example.ben.zhihudaily.network;

import com.example.ben.zhihudaily.api.DailyNewsApi;
import com.example.ben.zhihudaily.api.DailyThemeApi;


/**
 * Created by Zhou bangquan on 16/9/10.
 */

public class BenFactory {
    private static final Object object = new Object();
    private static DailyThemeApi mDailyThemeApi = null;
    private static DailyNewsApi mDailyNewsApi = null;

    public static DailyThemeApi getDailyThemeApi() {
        synchronized (object) {
            if (null == mDailyThemeApi) {
                mDailyThemeApi = new BenRetrofit().getDailyThemeApi();
            }
            return mDailyThemeApi;
        }
    }

    public static DailyNewsApi getDailyNewsApi() {
        synchronized (object) {
            if (null == mDailyNewsApi) {
                mDailyNewsApi = new BenRetrofit().getDailyNewsApi();
            }
            return mDailyNewsApi;
        }
    }
}
