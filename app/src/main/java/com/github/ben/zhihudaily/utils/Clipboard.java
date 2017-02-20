package com.github.ben.zhihudaily.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import com.github.ben.zhihudaily.utils.Constant;

/**
 * Created by Zhou bangquan on 17/2/20.
 */


public class Clipboard {

    public static void copyMessage(Context context, String message) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(Constant.COPY, message);
        clipboardManager.setPrimaryClip(clipData);
    }

}
