package com.example.ben.zhihudaily.api;


import com.example.ben.zhihudaily.data.entity.DailyDetail;
import com.example.ben.zhihudaily.data.entity.DailyNews;
import com.example.ben.zhihudaily.data.entity.StoryExtra;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 *Created by Zhou bangquan on 16/9/11.
 */

public interface DailyNewsApi {
    @GET("api/4/news/{latest}")
    Observable<DailyNews> getDailyNews(@Path("latest") String latest);

    @GET("api/4/news/{id}")
    Observable<DailyDetail> getDailyNewsDetail(@Path("id") String id);

    @GET("api/4/news/before/{date}")
    Observable<DailyNews> getBeforeDailyNews(@Path("date") String date);

    @GET("api/4/story-extra/{id}")
    Observable<StoryExtra> getStoryExtra(@Path("id") String id);
}
