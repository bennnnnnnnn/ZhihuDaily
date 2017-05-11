package com.github.ben.zhihudaily.data;

import com.google.gson.Gson;

import retrofit2.HttpException;

/**
 * Created on 17/2/23.
 *
 * @author Ben
 */


public class Error {
    public int status;
    public String error_msg;

    public static Error getError(HttpException throwable) {
        return new Gson().fromJson(throwable.response().errorBody().charStream(), Error.class);
    }

    public static String errorMessage(Throwable throwable) {
        try {
            if (throwable instanceof HttpException) {
                return getError((HttpException) throwable).error_msg;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return throwable.getMessage();
    }
}
