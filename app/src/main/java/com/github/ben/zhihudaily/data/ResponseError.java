package com.github.ben.zhihudaily.data;

import com.github.ben.zhihudaily.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.net.SocketTimeoutException;

import retrofit2.adapter.rxjava.HttpException;

/**
 * Created by Zhou bangquan on 16/9/27.
 */


public class ResponseError {

    public int status;
    public String error_msg;

    private Throwable throwable;
    private OnResult onResult;
    private ResponseError error = null;

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
        } else if (throwable instanceof SocketTimeoutException) {
            error = new ResponseError(0, "请求时间过长,请稍后再试");
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
