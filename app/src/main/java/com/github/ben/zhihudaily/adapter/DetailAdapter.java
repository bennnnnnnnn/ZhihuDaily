package com.github.ben.zhihudaily.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.design.widget.AppBarLayout;
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
 * @author Ben
 */


public class DetailAdapter extends PagerAdapter {

    private List<Story> dailies;
    private Context context;
    private Subscription itemSub;

    public DetailAdapter(Context context) {
        this.context = context;
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
    public Object instantiateItem(ViewGroup container, int position) {
        View view = View.inflate(context, R.layout.adapter_item_story_detail, null);
        final ImageView mTopImage = (ImageView) view.findViewById(R.id.top_image);
        final TextView mTitleTextView = (TextView) view.findViewById(R.id.title_textview);
        final TextView mImageSource = (TextView) view.findViewById(R.id.image_source);
        final WebView mContentWebView = (WebView) view.findViewById(R.id.content_webview);
        final AppBarLayout mAppBarLayout = (AppBarLayout) view.findViewById(R.id.top_layout);
        setWebViewSettings(mContentWebView);

        Story singleDaily = dailies.get(position);

        itemSub = BenFactory.getStoryApi()
                .getDailyNewsDetail(singleDaily.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<StoryDetail>() {
                    @Override
                    public void call(final StoryDetail storyDetail) {
                        mTitleTextView.setText(storyDetail.title);
                        mImageSource.setText(storyDetail.image_source);
                        if (!TextUtils.isEmpty(storyDetail.image)) {
                            mAppBarLayout.getLayoutParams().height = (int) (200 * App.screenDensity);
                            GlideUtils.loadingImage(context, mTopImage, storyDetail.image);
                        } else {
                            mAppBarLayout.getLayoutParams().height = 0;
                        }
                        String cssUrl = storyDetail.css[0];
                        OkHttpClient mOkHttpClient = new OkHttpClient();
                        final Request request = new Request.Builder()
                                .url(cssUrl)
                                .build();
                        Call call = mOkHttpClient.newCall(request);
                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                String htmlWithCss = HtmlUtils.get("", storyDetail.body, (boolean) SharePreUtils.get(App.NIGHT_MODE, false));
                                final String html = htmlWithCss.replace("<div class=\"img-place-holder\">", "");
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mContentWebView.loadDataWithBaseURL("", html, "text/html", "utf-8", "");
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String css = response.body().string();
                                String htmlWithCss = HtmlUtils.get(css, storyDetail.body, (boolean) SharePreUtils.get(App.NIGHT_MODE, false));
                                final String html = htmlWithCss.replace("<div class=\"img-place-holder\">", "");
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mContentWebView.loadDataWithBaseURL("", html, "text/html", "utf-8", "");
                                    }
                                });
                            }
                        });
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });

        container.addView(view);

        return view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setWebViewSettings(WebView mContentWebView) {
        WebSettings mWebSetting = mContentWebView.getSettings();
        mWebSetting.setJavaScriptEnabled(true);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        if (null != itemSub && itemSub.isUnsubscribed()) itemSub.unsubscribe();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }

}
