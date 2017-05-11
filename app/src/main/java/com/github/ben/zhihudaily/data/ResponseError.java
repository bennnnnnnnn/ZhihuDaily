package com.github.ben.zhihudaily.data;

import android.content.Context;
import android.util.Log;

import com.github.ben.zhihudaily.utils.ToastUtils;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.functions.Consumer;
import retrofit2.HttpException;

/**
 * Created on 16/9/27.
 *
 * @author Ben
 */


public class ResponseError {

    private static final String TAG = ResponseError.class.getSimpleName();

    public static void displayError(Context context, Throwable throwable) {
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
            displayError(context, errorMessage);
        } else {
            displayError(context, Error.errorMessage(throwable));
        }
    }

    public static void displayError(Context context, String errorMessage) {
        if (context == null) {
            return;
        }
        ToastUtils.longToast(context, errorMessage);
    }

    public static Consumer<Throwable> displayCustomErrorConsumer(final Context context) {
        return new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                displayError(context, throwable);
            }
        };
    }
}
