package com.github.ben.zhihudaily.data;

import android.content.Context;
import android.util.Log;

import com.github.ben.zhihudaily.utils.ToastUtils;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.adapter.rxjava.HttpException;
import rx.functions.Action1;

/**
 * Created on 16/9/27.
 *
 * @author Ben
 */


public class ResponseError {

    private static final String TAG = ResponseError.class.getSimpleName();

    public interface OnResult {
        void onResult(String errorMessage);
    }

    public static void handleError(Context context, Throwable throwable, OnResult onResult) {
        if (context == null) {
            Log.e(TAG, "Context is null");
            return;
        }

        String errorMessage = null;
        if (throwable instanceof HttpException) {
            HttpException exception = (HttpException) throwable;
            errorMessage = Error.getError(exception).error_msg;
        } else if (throwable instanceof SocketTimeoutException) {
            errorMessage = "请求时间过长,请稍后再试";
        } else if (throwable instanceof UnknownHostException) {
            errorMessage = "网络未连接或不可用，请检查后重试";
        }

        if (errorMessage != null) {
            responseResult(context, onResult, errorMessage);
        } else {
            responseResult(context, onResult, Error.errorMessage(throwable));
        }
    }

    public static void responseResult(Context context, OnResult onResult, String errorMessage) {
        if (null != onResult) {
            showToast(context, errorMessage);
            onResult.onResult(errorMessage);
        } else {
            showToast(context, errorMessage);
        }
    }

    public static void showToast(Context context, String errorMessage) {
        if (context == null) {
            return;
        }
        ToastUtils.longToast(context, errorMessage);
    }

    public static Action1<Throwable> displayCustomErrorAction(final Context context, final OnResult onResult) {
        return new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                handleError(context, throwable, onResult);
            }
        };
    }
}
