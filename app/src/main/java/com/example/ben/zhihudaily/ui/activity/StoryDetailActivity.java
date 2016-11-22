package com.example.ben.zhihudaily.ui.activity;

import android.content.Intent;
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
import com.example.ben.zhihudaily.data.entity.StoriesResult;
import com.example.ben.zhihudaily.data.entity.Story;
import com.example.ben.zhihudaily.data.entity.StoryExtra;
import com.example.ben.zhihudaily.network.BenFactory;
import com.example.ben.zhihudaily.ui.base.BaseActivity;
import com.example.ben.zhihudaily.utils.Constant;
import com.example.ben.zhihudaily.utils.DetailStoryActionProvider;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Zhou bangquan on 16/9/11.
 */

public class StoryDetailActivity extends BaseActivity {

    @Bind(R.id.daily_viewPager)
    ViewPager mViewPager;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    public ActionBar mActionbar;

    private DetailAdapter mAdapter;
    private String id;
    private String before;
    private String type;
    private List<Story> dailies;
    private DetailStoryActionProvider commentActionProvider;
    private DetailStoryActionProvider popularityActionProvider;
    private int comments;
    private int long_comments;
    private int short_comments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_detail_layout);
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
        subscription = BenFactory.getStoryApi()
                .getStoryExtra(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<StoryExtra>() {
                    @Override
                    public void call(StoryExtra storyExtra) {
                        comments = storyExtra.comments;
                        long_comments = storyExtra.long_comments;
                        short_comments = storyExtra.short_comments;
                        commentActionProvider.setNum(comments + "");
                        popularityActionProvider.setNum(storyExtra.popularity);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    private void getTopStories() {
        unsubscribe();
        subscription = BenFactory.getStoryApi()
                .getDailyNews("latest")
                .map(new Func1<StoriesResult, List<Story>>() {
                    @Override
                    public List<Story> call(StoriesResult dailyNews) {
                        if (dailyNews != null) {
                            return dailyNews.top_stories;
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Story>>() {
                    @Override
                    public void call(List<Story> singleDailies) {
                        dailies = singleDailies;
                        mAdapter.setDailyNews(singleDailies);
                        for (int i = 0; i < singleDailies.size(); i++) {
                            if (id.equals(singleDailies.get(i).id)) {
                                mViewPager.setCurrentItem(i);
                                getStoryExtra(id);
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    private void getBeforeDailies() {
        unsubscribe();
        subscription = BenFactory.getStoryApi()
                .getBeforeDailyNews(before)
                .map(new Func1<StoriesResult, List<Story>>() {
                    @Override
                    public List<Story> call(StoriesResult dailyNews) {
                        if (dailyNews != null) {
                            return dailyNews.stories;
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Story>>() {
                    @Override
                    public void call(List<Story> singleDailies) {
                        dailies = singleDailies;
                        mAdapter.setDailyNews(singleDailies);
                        for (int i = 0; i < singleDailies.size(); i++) {
                            if (id.equals(singleDailies.get(i).id)) {
                                mViewPager.setCurrentItem(i);
                                getStoryExtra(id);
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_news, menu);
        MenuItem commentItem = menu.findItem(R.id.comment_item);
        MenuItem praiseItem = menu.findItem(R.id.praise_item);
        commentActionProvider = (DetailStoryActionProvider) MenuItemCompat.getActionProvider(commentItem);
        popularityActionProvider = (DetailStoryActionProvider) MenuItemCompat.getActionProvider(praiseItem);
        commentActionProvider.setImageResource(R.drawable.comment_icon);
        popularityActionProvider.setImageResource(R.drawable.praise_icon);
        commentActionProvider.setOnClickListener(new DetailStoryActionProvider.OnClickListener() {
            @Override
            public void onClick() {
                startActivity(new Intent(mContext, CommentActivity.class)
                        .putExtra(Constant.COMMENTS, comments)
                        .putExtra(Constant.LONG_COMMENTS, long_comments)
                        .putExtra(Constant.SHORT_COMMENTS, short_comments).putExtra("id", id));
            }
        });
        popularityActionProvider.setOnClickListener(new DetailStoryActionProvider.OnClickListener() {
            @Override
            public void onClick() {

            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.share_item) {
            return true;
        } else if (id == R.id.collecion_item) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
