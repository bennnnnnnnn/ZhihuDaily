package com.example.ben.zhihudaily.adapter;

import android.content.Context;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ben.zhihudaily.R;
import com.example.ben.zhihudaily.data.entity.DailyDetail;
import com.example.ben.zhihudaily.data.entity.SingleDaily;
import com.example.ben.zhihudaily.data.entity.StoryExtra;
import com.example.ben.zhihudaily.network.BenFactory;
import com.example.ben.zhihudaily.ui.DailyDetailActivity;
import com.example.ben.zhihudaily.utils.DetailDailyActionProvider;
import com.example.ben.zhihudaily.utils.GlideUtils;

import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Zhou bangquan on 16/9/20.
 */


public class DetailAdapter extends PagerAdapter {

    private List<SingleDaily> dailies;
    private Context context;
    private Subscription itemSub;

    public DetailAdapter(Context context) {
        this.context = context;
    }

    public void setDailyNews(List<SingleDaily> mDailyNews) {
        this.dailies = mDailyNews;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return null == dailies ? 0 : dailies.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = View.inflate(context, R.layout.daily_detail_item, null);
        final ImageView mTopImage = (ImageView) view.findViewById(R.id.top_image);
        final TextView mTitleTextView = (TextView) view.findViewById(R.id.title_textview);
        final TextView mImageSource = (TextView) view.findViewById(R.id.image_source);
        final WebView mContentWebView = (WebView) view.findViewById(R.id.content_webview);

        SingleDaily singleDaily = dailies.get(position);

        itemSub = BenFactory.getDailyNewsApi()
                .getDailyNewsDetail(singleDaily.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DailyDetail>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(DailyDetail dailyDetail) {
                        mTitleTextView.setText(dailyDetail.title);
                        mImageSource.setText(dailyDetail.image_source);
                        GlideUtils.loadingImage(context, mTopImage, dailyDetail.image);
                        mContentWebView.loadDataWithBaseURL(dailyDetail.css[0], dailyDetail.body, "text/html", "utf-8", null);
                    }
                });

        container.addView(view);

        return view;
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
