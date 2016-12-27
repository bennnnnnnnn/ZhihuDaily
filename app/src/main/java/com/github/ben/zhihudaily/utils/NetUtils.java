package com.github.ben.zhihudaily.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.github.ben.zhihudaily.ui.App;

/**
 * Created by Zhou bangquan on 16/10/18.
 */


public class NetUtils {

    public static boolean isNetworkConnected() {
        if (App.getInstance() != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) App.getInstance()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean isWifiConnected() {
        if (App.getInstance() != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) App.getInstance()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
               return mNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI;
            }
        }
        return false;
    }

    public static boolean isMobileConnected() {
        if (App.getInstance() != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) App.getInstance()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            }
        }
        return false;
    }

}
