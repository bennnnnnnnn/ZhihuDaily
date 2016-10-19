package com.example.ben.zhihudaily.api;


import com.example.ben.zhihudaily.data.entity.StartImage;
import com.example.ben.zhihudaily.data.entity.StoryDetail;
import com.example.ben.zhihudaily.data.entity.StoriesResult;
import com.example.ben.zhihudaily.data.entity.StoryExtra;
import com.example.ben.zhihudaily.network.BenRetrofit;

import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Zhou bangquan on 16/9/11.
 */

public interface StoryApi {
    @Headers(BenRetrofit.CACHE_CONTROL + BenRetrofit.CHCHE_MAX_STALE_SHORT)
    @GET("api/4/news/{latest}")
    Observable<StoriesResult> getDailyNews(@Path("latest") String latest);

    @GET("api/4/news/{id}")
    Observable<StoryDetail> getDailyNewsDetail(@Path("id") String id);

    @GET("api/4/news/before/{date}")
    Observable<StoriesResult> getBeforeDailyNews(@Path("date") String date);

    @GET("api/4/story-extra/{id}")
    Observable<StoryExtra> getStoryExtra(@Path("id") String id);

    @Headers(BenRetrofit.CACHE_CONTROL + BenRetrofit.CHCHE_MAX_STALE_SHORT)
    @GET("api/4/start-image/{size}")
    Observable<StartImage> getStartImage(@Path("size") String size);
}
