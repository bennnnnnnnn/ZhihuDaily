package com.github.ben.zhihudaily.network;

import com.github.ben.zhihudaily.api.StoryApi;
import com.github.ben.zhihudaily.api.StoryThemeApi;


/**
 * Created on 16/9/10.
 * @author Ben
 */

public class BenFactory {
    private static final Object object = new Object();
    private static StoryThemeApi mDailyThemeApi = null;
    private static StoryApi mDailyNewsApi = null;

    public static StoryThemeApi getStoryThemeApi() {
        synchronized (object) {
            if (null == mDailyThemeApi) {
                mDailyThemeApi = BenRetrofit.build().getStoryThemeApi();
            }
            return mDailyThemeApi;
        }
    }

    public static StoryApi getStoryApi() {
        synchronized (object) {
            if (null == mDailyNewsApi) {
                mDailyNewsApi = BenRetrofit.build().getStoryApi();
            }
            return mDailyNewsApi;
        }
    }
}
