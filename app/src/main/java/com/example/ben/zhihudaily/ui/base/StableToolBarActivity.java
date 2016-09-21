package com.example.ben.zhihudaily.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.example.ben.zhihudaily.R;

/**
 * Created by Zhou bangquan on 16/9/20.
 */


public abstract class StableToolBarActivity extends BaseActivity {

    abstract protected int provideContentViewId();

    public Toolbar mToolbar;
    public ActionBar mActionbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(provideContentViewId());
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        mActionbar = getSupportActionBar();
        if (null != mActionbar) {
            mActionbar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
