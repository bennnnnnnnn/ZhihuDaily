package com.github.ben.zhihudaily.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ben.zhihudaily.R;
import com.github.ben.zhihudaily.data.entity.StoryDetail;
import com.github.ben.zhihudaily.data.entity.Story;
import com.github.ben.zhihudaily.network.BenFactory;
import com.github.ben.zhihudaily.ui.App;
import com.github.ben.zhihudaily.ui.fragment.StoryDetailFragment;
import com.github.ben.zhihudaily.utils.GlideUtils;
import com.github.ben.zhihudaily.utils.HtmlUtils;
import com.github.ben.zhihudaily.utils.SharePreUtils;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created on 16/9/20.
 *
 * @author Ben
 */


public class DetailAdapter extends FragmentPagerAdapter {

    private List<Story> dailies;

    public DetailAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setDailyNews(List<Story> mDailyNews) {
        this.dailies = mDailyNews;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return null == dailies ? 0 : dailies.size();
    }

    @Override
    public Fragment getItem(int position) {
        return null == dailies ? null : StoryDetailFragment.newInstance(dailies.get(position).id);
    }

}
