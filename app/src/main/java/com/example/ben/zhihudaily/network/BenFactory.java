package com.example.ben.zhihudaily.network;

import com.example.ben.zhihudaily.api.DailyNewsApi;
import com.example.ben.zhihudaily.api.DailyThemeApi;


/**
 * Created by Zhou bangquan on 16/9/10.
 */

public class BenFactory {

    static DailyThemeApi mDailyThemeApi = null;
    static DailyNewsApi mDailyNewsApi = null;

    public static DailyThemeApi getDailyThemeApi() {
        if (null == mDailyThemeApi) {
            mDailyThemeApi = new BenRetrofit().getDailyThemeApi();
        }
        return mDailyThemeApi;
    }

    public static DailyNewsApi getDailyNewsApi() {
        if (null == mDailyNewsApi) {
            mDailyNewsApi = new BenRetrofit().getDailyNewsApi();
        }
        return mDailyNewsApi;
    }

}
