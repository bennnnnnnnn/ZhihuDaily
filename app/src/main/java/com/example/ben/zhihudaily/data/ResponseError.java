package com.example.ben.zhihudaily.data;

import android.content.Context;

import com.example.ben.zhihudaily.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import retrofit2.adapter.rxjava.HttpException;

import static java.net.HttpURLConnection.HTTP_SERVER_ERROR;

/**
 * Created by Zhou bangquan on 16/9/27.
 */


public class ResponseError {

    public int status;
    public String error_msg;
    private Throwable throwable;
    private OnResult onResult;
    private ResponseError error = null;

    public ResponseError() {

    }

    public ResponseError(int status, String error_msg) {
        this.status = status;
        this.error_msg = error_msg;
    }

    public ResponseError(Throwable throwable) {
        this.throwable = throwable;
    }

    public void handle(OnResult onResult) {
        this.onResult = onResult;
        if (throwable instanceof HttpException) {
            HttpException exception = (HttpException) throwable;
            try {
                error = new Gson().fromJson(exception.response().errorBody().string(), ResponseError.class);
            } catch (Exception e) {
                if (e instanceof JsonParseException) {
                } else {
                }
            }
        } else {
        }
        if (BuildConfig.DEBUG) {
            throwable.printStackTrace();
        }
        if (null != onResult) {
            onResult.onResult(error);
        }
    }

    public interface OnResult {
        void onResult(ResponseError error);
    }
}
