package com.github.ben.zhihudaily.ui;

import android.annotation.SuppressLint;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created on 16/10/8.
 *
 * @author Ben
 */


public class StartActivity extends BaseActivity {

    @BindView(R.id.start_image)
    ImageView mStartImageView;
    @BindView(R.id.image_author)
    TextView mAuthorTextView;
    @BindView(R.id.start_image_layout)
    RelativeLayout mStartLayout;
    @BindView(R.id.bottom_animation_layout)
    RelativeLayout mBottomLayout;
    private int imageLayoutHeight = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        getHeights();
        setImageLayout();
        translateBottonLayout();
        loadStartImage();
    }

    @SuppressLint("CheckResult")
    private void loadStartImage() {
        String size = App.screenWidth + "*" + imageLayoutHeight;
        BenFactory.getStoryApi()
                .getStartImage(size)
                .compose(this.<StartImage>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .delay(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<StartImage>() {
                    @Override
                    public void accept(@NonNull StartImage startImage) {
                        GlideUtils.loadingImage(mContext, mStartImageView, startImage.img);
                        mAuthorTextView.setText(startImage.text);
                    }
                })
                .delay(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<StartImage>() {
                    @Override
                    public void accept(@NonNull StartImage startImage) {
                        startActivity();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) {
                        Flowable.timer(3, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(@NonNull Long aLong) {
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
