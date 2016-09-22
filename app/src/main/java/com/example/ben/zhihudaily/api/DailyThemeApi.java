package com.example.ben.zhihudaily.api;


import com.example.ben.zhihudaily.data.entity.StoryThemeResult;
import com.example.ben.zhihudaily.data.entity.ThemeStories;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Zhou bangquan on 16/9/10.
 */

public interface DailyThemeApi {
    @GET("api/4/themes")
    Observable<StoryThemeResult> getDailyThemes();

    @GET("api/4/theme/{id}")
    Observable<ThemeStories> getThemeStories(@Path("id") String id);
}
