package com.example.ben.zhihudaily.api;


import com.example.ben.zhihudaily.data.entity.DailyThemeResult;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by Zhou bangquan on 16/9/10.
 */

public interface DailyThemeApi {
    @GET("api/4/themes")
    Observable<DailyThemeResult> getDailyThemes();
}
