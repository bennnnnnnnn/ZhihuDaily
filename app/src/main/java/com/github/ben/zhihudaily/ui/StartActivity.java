package com.github.ben.zhihudaily.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ben.zhihudaily.R;
import com.github.ben.zhihudaily.data.entity.StartImage;
import com.github.ben.zhihudaily.network.BenFactory;
import com.github.ben.zhihudaily.ui.base.BaseActivity;
import com.github.ben.zhihudaily.utils.GlideUtils;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Zhou bangquan on 16/10/8.
 */


public class StartActivity extends BaseActivity {

    @Bind(R.id.start_image)
    ImageView mStartImageView;
    @Bind(R.id.image_author)
    TextView mAuthorTextView;
    @Bind(R.id.start_image_layout)
    RelativeLayout mStartLayout;
    @Bind(R.id.bottom_animation_layout)
    RelativeLayout mBottomLayout;
    private int imageLayoutHeight = 0;

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_start;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        getHeights();
        setImageLayout();
        translateBottonLayout();
        loadStartImage();
    }

    private void loadStartImage() {
        String size = App.screenWidth + "*" + imageLayoutHeight;
        unsubscribe();
        subscription = BenFactory.getStoryApi()
                .getStartImage(size)
                .subscribeOn(Schedulers.io())
                .delay(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<StartImage>() {
                    @Override
                    public void call(StartImage startImage) {
                        GlideUtils.loadingImage(mContext, mStartImageView, startImage.img);
                        mAuthorTextView.setText(startImage.text);
                    }
                })
                .delay(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<StartImage>() {
                    @Override
                    public void call(StartImage startImage) {
                        startActivity();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Observable.timer(3, TimeUnit.SECONDS).subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                startActivity();
                            }
                        });
                    }
                });
    }

    private void startActivity() {
        startActivity(new Intent(mContext, MainActivity.class));
        finish();
    }

    private void setImageLayout() {
        mStartLayout.getLayoutParams().height = imageLayoutHeight;
    }

    private void translateBottonLayout() {
        float fromY = App.screenHeight;
        float toY = App.screenHeight - (mBottomLayout.getLayoutParams().height) - App.statusBarHeight;
        TranslateAnimation animation = new TranslateAnimation(0, 0, fromY, toY);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        mBottomLayout.startAnimation(animation);
    }

    private void getHeights() {
        imageLayoutHeight = (int) (App.screenHeight - (mBottomLayout.getLayoutParams().height) - App.statusBarHeight - 5 * App.screenDensity);
    }

    @Override
    public void onBackPressed() {
    }
}
