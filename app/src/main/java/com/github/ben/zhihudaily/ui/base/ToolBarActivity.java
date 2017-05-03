package com.github.ben.zhihudaily.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.github.ben.zhihudaily.R;

/**
 * Created on 17/2/24.
 *
 * @author Ben
 */


public abstract class ToolBarActivity extends BaseActivity {

    public Toolbar mToolbar;
    public ActionBar mActionbar;

    abstract protected int provideContentViewId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(provideContentViewId());

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (null != mToolbar) {
            setSupportActionBar(mToolbar);
            mActionbar = getSupportActionBar();
            if (null != mActionbar) {
                mActionbar.setDisplayHomeAsUpEnabled(true);
            }
        }
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
