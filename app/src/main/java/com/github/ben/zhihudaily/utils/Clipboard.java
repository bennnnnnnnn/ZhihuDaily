package com.github.ben.zhihudaily.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * Created on 17/2/20.
 * @author Ben
 */


public class Clipboard {

    public static void copyMessage(Context context, String message) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(Constant.COPY, message);
        clipboardManager.setPrimaryClip(clipData);
    }

}
