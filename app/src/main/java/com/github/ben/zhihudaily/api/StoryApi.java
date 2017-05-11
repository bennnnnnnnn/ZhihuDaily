package com.github.ben.zhihudaily.api;


import com.github.ben.zhihudaily.data.entity.CommentsResult;
import com.github.ben.zhihudaily.data.entity.StartImage;
import com.github.ben.zhihudaily.data.entity.StoryDetail;
import com.github.ben.zhihudaily.data.entity.StoriesResult;
import com.github.ben.zhihudaily.data.entity.StoryExtra;
import com.github.ben.zhihudaily.network.BenRetrofit;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * Created on 16/9/11.
 * @author Ben
 */

public interface StoryApi {
    @Headers(BenRetrofit.CACHE_CONTROL + BenRetrofit.CHCHE_MAX_STALE_SHORT)
    @GET("api/4/news/{latest}")
    Flowable<StoriesResult> getDailyNews(@Path("latest") String latest);

    @GET("api/4/news/{id}")
    Flowable<StoryDetail> getDailyNewsDetail(@Path("id") String id);

    @GET("api/4/news/before/{date}")
    Flowable<StoriesResult> getBeforeDailyNews(@Path("date") String date);

    @GET("api/4/story-extra/{id}")
    Flowable<StoryExtra> getStoryExtra(@Path("id") String id);

    @Headers(BenRetrofit.CACHE_CONTROL + BenRetrofit.CHCHE_MAX_STALE_SHORT)
    @GET("api/4/start-image/{size}")
    Flowable<StartImage> getStartImage(@Path("size") String size);

    @GET("api/4/story/{id}/long-comments")
    Flowable<CommentsResult> getLongComments(@Path("id") String id);

    @GET("api/4/story/{id}/short-comments")
    Flowable<CommentsResult> getShortComments(@Path("id") String id);
}
