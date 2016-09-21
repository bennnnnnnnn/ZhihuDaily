package com.example.ben.zhihudaily.network;


import com.example.ben.zhihudaily.api.DailyNewsApi;
import com.example.ben.zhihudaily.api.DailyThemeApi;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Zhou bangquan on 16/9/10.
 */

public class BenRetrofit {

    private static final String BASE_URL = "http://news-at.zhihu.com/";
    private static final int DEFAULT_TIMEOUT = 5;
    private Retrofit retrofit;
    private DailyThemeApi dailyThemeApi;
    private DailyNewsApi dailyNewsApi;

    BenRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(interceptor).connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();
    }

    public DailyThemeApi getDailyThemeApi() {
        if (null == dailyThemeApi) {
            dailyThemeApi = retrofit.create(DailyThemeApi.class);
        }
        return dailyThemeApi;
    }

    public DailyNewsApi getDailyNewsApi() {
        if (null == dailyNewsApi) {
            dailyNewsApi = retrofit.create(DailyNewsApi.class);
        }
        return dailyNewsApi;
    }

}
