package com.github.ben.zhihudaily.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created on 16/10/12.
 * @author Ben
 */


public class ToastUtils {

    public static void shortToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void longToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
}
