package com.github.ben.zhihudaily.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ben.zhihudaily.R;
import com.github.ben.zhihudaily.data.entity.StoryDetail;
import com.github.ben.zhihudaily.network.BenFactory;
import com.github.ben.zhihudaily.ui.App;
import com.github.ben.zhihudaily.ui.base.BaseFragment;
import com.github.ben.zhihudaily.utils.GlideUtils;
import com.github.ben.zhihudaily.utils.HtmlUtils;
import com.github.ben.zhihudaily.utils.SharePreUtils;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created on 17/2/22.
 *
 * @author Ben
 */


public class StoryDetailFragment extends BaseFragment {

    @Bind(R.id.top_image)
    ImageView mTopImage;
    @Bind(R.id.title_textview)
    TextView mTitleTextView;
    @Bind(R.id.image_source)
    TextView mImageSource;
    @Bind(R.id.content_webview)
    WebView mContentWebView;
    @Bind(R.id.top_layout)
    AppBarLayout mAppBarLayout;

    private String id;

    public static StoryDetailFragment newInstance(String id) {
        StoryDetailFragment fragment = new StoryDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getArguments().getString("id");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.adapter_item_story_detail, container, false);
        ButterKnife.bind(this, rootView);
        setWebViewSettings(mContentWebView);
        getDetail(id);
        return rootView;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setWebViewSettings(WebView mContentWebView) {
        WebSettings mWebSetting = mContentWebView.getSettings();
        mWebSetting.setJavaScriptEnabled(true);
        mWebSetting.setLoadWithOverviewMode(true);
        mWebSetting.setAppCacheEnabled(true);
    }

    private void getDetail(String id) {
        BenFactory.getStoryApi()
                .getDailyNewsDetail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<StoryDetail>() {
                    @Override
                    public void call(final StoryDetail storyDetail) {
                        mTitleTextView.setText(storyDetail.title);
                        mImageSource.setText(storyDetail.image_source);
                        if (!TextUtils.isEmpty(storyDetail.image)) {
                            mAppBarLayout.getLayoutParams().height = (int) (200 * App.screenDensity);
                            GlideUtils.loadingImage(getActivity(), mTopImage, storyDetail.image);
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
                                getActivity().runOnUiThread(new Runnable() {
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
                                getActivity().runOnUiThread(new Runnable() {
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
    }
}
