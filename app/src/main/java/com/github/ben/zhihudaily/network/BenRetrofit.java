package com.github.ben.zhihudaily.network;


import android.text.TextUtils;
import android.util.Log;

import com.github.ben.zhihudaily.api.StoryApi;
import com.github.ben.zhihudaily.api.StoryThemeApi;
import com.github.ben.zhihudaily.ui.App;
import com.github.ben.zhihudaily.utils.NetUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created on 16/9/10.
 * @author Ben
 */

public class BenRetrofit {
    private static final String BASE_URL = "http://news-at.zhihu.com/";
    public static final String CACHE_CONTROL = "Cache-Control: public, max-age=";
    public static final int CACHE_MAX_STALE_LONG = 60 * 60 * 24 * 3;
    public static final int CHCHE_MAX_STALE_SHORT = 60;
    private static final int DEFAULT_TIMEOUT = 15;
    private OkHttpClient okHttpClient;
    private static Retrofit retrofit;
    private StoryThemeApi dailyThemeApi;
    private StoryApi dailyNewsApi;
    private static final String TAG = "LogInterceptor.java";

    public static BenRetrofit build() {
        return new BenRetrofit();
    }

    private BenRetrofit() {
        initOkHttpClient();
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
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
                            .addInterceptor(loggingInterceptor)  // 和下面的 logInterceptor 等价 logInterceptor 可以自定义; loggingInterceptor 默认
                            .addInterceptor(networkInterceptor)
                            .addNetworkInterceptor(networkInterceptor)
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
                //查询缓存的Cache-Control设置，为only-if-cached时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
                response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + CACHE_MAX_STALE_LONG)//设置缓存策略，及超时策略
                        .build();
            }
            return response;
        }
    };

    //log interceptor
    private Interceptor logInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            //the request url
            String url = request.url().toString();
            //the request method
            String method = request.method();
            //the request headers
            String headers = request.headers().toString();

            long t1 = System.nanoTime();
            Response response = chain.proceed(request);
            long t2 = System.nanoTime();
            //the response state
            Log.d(TAG, String.format(Locale.CHINA, "Received response is %s ,message[%s],code[%d]", response.isSuccessful() ? "success" : "fail", response.message(), response.code()));

            //the response data
            ResponseBody body = response.body();

            BufferedSource source = body.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();
            Charset charset = Charset.defaultCharset();
            MediaType contentType = body.contentType();

            Log.d(TAG, method);
            Log.d(TAG, url);
            Log.d(TAG, headers);
            Log.d(TAG, response.code() + "");
            Log.d(TAG, response.headers() + "");
            Log.d(TAG, (t2 - t1) / 1e6d + "ms");

            if (contentType != null) {
                charset = contentType.charset(charset);
            }
            String bodyString = buffer.clone().readString(charset);
            Log.d(TAG, String.format("Received response json string [%s]", bodyString)); //requst result

            return response;
        }
    };

    public StoryThemeApi getStoryThemeApi() {
        if (null == dailyThemeApi) {
            dailyThemeApi = retrofit.create(StoryThemeApi.class);
        }
        return dailyThemeApi;
    }

    public StoryApi getStoryApi() {
        if (null == dailyNewsApi) {
            dailyNewsApi = retrofit.create(StoryApi.class);
        }
        return dailyNewsApi;
    }
}
