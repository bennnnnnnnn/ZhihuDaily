package com.github.ben.zhihudaily.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.ben.zhihudaily.utils.SharePreUtils;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

/**
 * Created on 16/9/20.
 *
 * @author Ben
 */


public abstract class BaseActivity extends RxAppCompatActivity {

    protected Context mContext;

    protected boolean isNightMode() {
        return SharePreUtils.isNight();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
