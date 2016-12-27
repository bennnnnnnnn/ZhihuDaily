package com.github.ben.zhihudaily.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.ben.zhihudaily.ui.App;

/**
 * Created by Zhou bangquan on 16/10/11.
 */


public class SharePreUtils {

    private static final String FILE_NAME = "zhihu_sharePre_dates";
    public static final String NIGHT_MODE = "isNight";

    private static SharedPreferences getSharedPreferences() {
        return App.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public static void put(String key, Object object) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        editor.apply();
    }

    public static Object get(String key, Object defaultObject) {
        SharedPreferences sp = getSharedPreferences();
        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }
        return null;
    }

    public static boolean isNight() {
        return getSharedPreferences().getBoolean(NIGHT_MODE, false);
    }

}
