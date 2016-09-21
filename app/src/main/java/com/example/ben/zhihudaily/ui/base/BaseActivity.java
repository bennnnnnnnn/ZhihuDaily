package com.example.ben.zhihudaily.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.ben.zhihudaily.R;

import rx.Subscription;


/**
 * Created by Zhou bangquan on 16/9/9.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected Subscription subscription;

    protected void unsubscribe() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unsubscribe();
    }
}
