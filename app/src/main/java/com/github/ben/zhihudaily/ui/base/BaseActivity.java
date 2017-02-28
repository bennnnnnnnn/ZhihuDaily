package com.github.ben.zhihudaily.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.github.ben.zhihudaily.R;
import com.github.ben.zhihudaily.utils.SharePreUtils;

import rx.Subscription;

/**
 * Created on 16/9/20.
 * @author Ben
 */


public abstract class BaseActivity extends AppCompatActivity {

    protected Subscription subscription;
    protected Context mContext;

    protected void unsubscribe() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

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
        unsubscribe();
    }
}
