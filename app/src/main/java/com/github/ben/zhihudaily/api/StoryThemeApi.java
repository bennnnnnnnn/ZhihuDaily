package com.github.ben.zhihudaily.api;


import com.github.ben.zhihudaily.data.entity.StoryThemeResult;
import com.github.ben.zhihudaily.data.entity.ThemeStories;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created on 16/9/10.
 * @author Ben
 */

public interface StoryThemeApi {
    @GET("api/4/themes")
    Flowable<StoryThemeResult> getDailyThemes();

    @GET("api/4/theme/{id}")
    Flowable<ThemeStories> getThemeStories(@Path("id") String id);
}
