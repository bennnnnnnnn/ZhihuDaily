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
 * Created by Zhou bangquan on 16/9/20.
 */


public abstract class BaseActivity extends AppCompatActivity {

    protected Subscription subscription;
    protected Context mContext;
    public Toolbar mToolbar;
    public ActionBar mActionbar;

    abstract protected int provideContentViewId();

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
        setContentView(provideContentViewId());
        mContext = this;
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        mActionbar = getSupportActionBar();
        if (null != mActionbar) {
            mActionbar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unsubscribe();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
