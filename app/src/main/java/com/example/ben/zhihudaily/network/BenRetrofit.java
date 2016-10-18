package com.example.ben.zhihudaily.network;


import android.text.TextUtils;

import com.example.ben.zhihudaily.api.DailyNewsApi;
import com.example.ben.zhihudaily.api.DailyThemeApi;
import com.example.ben.zhihudaily.ui.App;
import com.example.ben.zhihudaily.utils.NetUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Zhou bangquan on 16/9/10.
 */

public class BenRetrofit {
    private static final String BASE_URL = "http://news-at.zhihu.com/";
    public static final String CACHE_CONTROL = "Cache-Control: public, max-age=";
    public static final int CACHE_MAX_STALE_LONG = 60 * 60 * 24 * 3;
    public static final int CHCHE_MAX_STALE_SHORT = 60;
    private static final int DEFAULT_TIMEOUT = 5;
    private OkHttpClient okHttpClient;
    private static Retrofit retrofit;
    private DailyThemeApi dailyThemeApi;
    private DailyNewsApi dailyNewsApi;

    BenRetrofit() {
        initOkHttpClient();
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();
    }

    private void initOkHttpClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        if (okHttpClient == null) {
            synchronized (BenRetrofit.class) {
                if (okHttpClient == null) {
                    // 指定缓存路径,缓存大小100Mb
                    Cache cache = new Cache(new File(App.getInstance().getCacheDir(), "HttpCache"),
                            1024 * 1024 * 100);

                    okHttpClient = new OkHttpClient.Builder()
                            .cache(cache)
                            .addInterceptor(networkInterceptor)
                            .addNetworkInterceptor(networkInterceptor)
                            .addInterceptor(loggingInterceptor)
                            .retryOnConnectionFailure(true)
                            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
    }

    private Interceptor networkInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {

            Request request = chain.request();

            if (!NetUtils.isNetworkConnected()) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)  //无网络时只从缓存中读取
                        .build();
            }

            Response response = chain.proceed(request);
            if (NetUtils.isNetworkConnected()) {
                //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
                String cacheControl = request.cacheControl().toString();
                if (TextUtils.isEmpty(cacheControl)) {
                    cacheControl = "public, max-age=" + CHCHE_MAX_STALE_SHORT;
                }
                response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", cacheControl)
                        .build();
            } else {
                //查询缓存的Cache-Control设置，为if-only-cache时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
                response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + CACHE_MAX_STALE_LONG)//设置缓存策略，及超时策略
                        .build();
            }
            return response;
        }
    };

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
