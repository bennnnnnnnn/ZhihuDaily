package com.example.ben.zhihudaily.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ben.zhihudaily.R;
import com.example.ben.zhihudaily.adapter.DetailAdapter;
import com.example.ben.zhihudaily.data.entity.DailyNews;
import com.example.ben.zhihudaily.data.entity.SingleDaily;
import com.example.ben.zhihudaily.data.entity.StoryExtra;
import com.example.ben.zhihudaily.network.BenFactory;
import com.example.ben.zhihudaily.ui.base.BaseActivity;
import com.example.ben.zhihudaily.utils.Constant;
import com.example.ben.zhihudaily.utils.DetailDailyActionProvider;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Zhou bangquan on 16/9/11.
 */

public class DailyDetailActivity extends BaseActivity {

    @Bind(R.id.daily_viewPager)
    ViewPager mViewPager;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    public ActionBar mActionbar;

    private DetailAdapter mAdapter;
    private String id;
    private String before;
    private String type;
    List<SingleDaily> dailies;
    private DetailDailyActionProvider commentActionProvider;
    private DetailDailyActionProvider popularityActionProvider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daily_detail_layout);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mActionbar = getSupportActionBar();
        if (null != mActionbar) {
            mActionbar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle("");
        id = getIntent().getStringExtra("id");
        before = getIntent().getStringExtra("before");
        type = getIntent().getStringExtra("type");
        initViewPager();
        if (Constant.TOP_STORIES.equals(type)) {
            getTopStories();
        } else {
            getBeforeDailies();
        }
    }

    private void initViewPager() {
        mAdapter = new DetailAdapter(this);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getStoryExtra(dailies.get(position).id);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void getStoryExtra(String id) {
        unsubscribe();
        subscription = BenFactory.getDailyNewsApi()
                .getStoryExtra(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<StoryExtra>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(StoryExtra storyExtra) {
                        commentActionProvider.setNum(storyExtra.comments);
                        popularityActionProvider.setNum(storyExtra.popularity);
                    }
                });
    }

    private void getTopStories() {
        unsubscribe();
        subscription = BenFactory.getDailyNewsApi()
                .getDailyNews("latest")
                .map(new Func1<DailyNews, List<SingleDaily>>() {
                    @Override
                    public List<SingleDaily> call(DailyNews dailyNews) {
                        if (dailyNews != null) {
                            return dailyNews.top_stories;
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<SingleDaily>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<SingleDaily> singleDailies) {
                        dailies = singleDailies;
                        mAdapter.setDailyNews(singleDailies);
                        for (int i = 0; i < singleDailies.size(); i++) {
                            if (id.equals(singleDailies.get(i).id)) {
                                mViewPager.setCurrentItem(i);
                                getStoryExtra(id);
                            }
                        }
                    }
                });
    }

    private void getBeforeDailies() {
        unsubscribe();
        subscription = BenFactory.getDailyNewsApi()
                .getBeforeDailyNews(before)
                .map(new Func1<DailyNews, List<SingleDaily>>() {
                    @Override
                    public List<SingleDaily> call(DailyNews dailyNews) {
                        if (dailyNews != null) {
                            return dailyNews.stories;
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<SingleDaily>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<SingleDaily> singleDailies) {
                        dailies = singleDailies;
                        mAdapter.setDailyNews(singleDailies);
                        for (int i = 0; i < singleDailies.size(); i++) {
                            if (id.equals(singleDailies.get(i).id)) {
                                mViewPager.setCurrentItem(i);
                                getStoryExtra(id);
                            }
                        }
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_news, menu);
        MenuItem commentItem = menu.findItem(R.id.commend_item);
        MenuItem praiseItem = menu.findItem(R.id.praise_item);
        commentActionProvider = (DetailDailyActionProvider) MenuItemCompat.getActionProvider(commentItem);
        popularityActionProvider = (DetailDailyActionProvider) MenuItemCompat.getActionProvider(praiseItem);
        commentActionProvider.setOnClickListener(new DetailDailyActionProvider.OnClickListener() {
            @Override
            public void onClick() {

            }
        });
        popularityActionProvider.setOnClickListener(new DetailDailyActionProvider.OnClickListener() {
            @Override
            public void onClick() {

            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.share_item) {

        } else if (id == R.id.collecion_item) {

        }
        return true;
    }
}
