package com.github.ben.zhihudaily.ui;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.github.ben.zhihudaily.R;
import com.github.ben.zhihudaily.ui.base.BaseActivity;

/**
 * Created on 17/2/23.
 *
 * @author Ben
 */


public class AboutActivity extends BaseActivity {

    public TextView mVersionTv;

    public Toolbar mToolbar;
    public ActionBar mActionbar;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mToolbar = findViewById(R.id.toolbar);
        mVersionTv = findViewById(R.id.version_tv);

        setSupportActionBar(mToolbar);
        mActionbar = getSupportActionBar();
        if (null != mActionbar) {
            mActionbar.setDisplayHomeAsUpEnabled(true);
        }
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        setTitle(R.string.app_name);
        mVersionTv.setText("Version " + getVersion() + "");
    }

    private String getVersion() {
        try {
            PackageInfo manager = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            return manager.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
}
