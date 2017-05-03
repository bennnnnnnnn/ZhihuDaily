package com.github.ben.zhihudaily.ui.module.story;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;

import com.github.ben.zhihudaily.R;
import com.github.ben.zhihudaily.adapter.DetailAdapter;
import com.github.ben.zhihudaily.data.entity.Story;
import com.github.ben.zhihudaily.data.entity.StoryExtra;
import com.github.ben.zhihudaily.ui.module.comment.CommentActivity;
import com.github.ben.zhihudaily.mvpbase.MVPBaseActivity;
import com.github.ben.zhihudaily.utils.Constant;
import com.github.ben.zhihudaily.utils.DetailStoryActionProvider;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created on 16/9/11.
 *
 * @author Ben
 */

public class StoryDetailActivity extends MVPBaseActivity<StoryDetailContract.View, StoryDetailPresenter> implements StoryDetailContract.View {

    @Bind(R.id.daily_viewPager)
    ViewPager mViewPager;

    private DetailAdapter mAdapter;
    private String currentId;
    private String before;
    private String type;
    private String themeId;
    private DetailStoryActionProvider commentActionProvider;
    private DetailStoryActionProvider praiseActionProvider;
    private int comments;
    private int long_comments;
    private int short_comments;

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_story_detail;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setTitle("");
        currentId = getIntent().getStringExtra("id");
        before = getIntent().getStringExtra("before");
        type = getIntent().getStringExtra("type");
        themeId = getIntent().getStringExtra("themeId");
        initViewPager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constant.TOP_STORIES.equals(type)) {
            mPresenter.getTopStories();
        } else if (Constant.STORY.equals(type)) {
            mPresenter.getBeforeStories(before);
        } else if (Constant.THEME_STORY.equals(type)) {
            mPresenter.getThemeStories(themeId);
        }
    }

    private void initViewPager() {
        mAdapter = new DetailAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(getOnPageChangeListener());
    }

    private OnPageChangeListener getOnPageChangeListener() {
        return new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPresenter.getStoryExtra(position);
                mPresenter.updateStoryState(position);
                currentId = mPresenter.getCurrentId(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
    }

    @Override
    public void setStoryExtra(StoryExtra storyExtra) {
        comments = storyExtra.comments;
        long_comments = storyExtra.long_comments;
        short_comments = storyExtra.short_comments;
        commentActionProvider.setNum(comments + "");
        praiseActionProvider.setNum(storyExtra.popularity);
    }

    @Override
    public void setStoryList(List<Story> singleDailies) {
        mAdapter.setDailyNews(singleDailies);
        for (int i = 0; i < singleDailies.size(); i++) {
            if (currentId.equals(singleDailies.get(i).id)) {
                mViewPager.setCurrentItem(i);
                mPresenter.getStoryExtra(currentId);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_news, menu);
        MenuItem commentItem = menu.findItem(R.id.comment_item);
        MenuItem praiseItem = menu.findItem(R.id.praise_item);
        commentActionProvider = (DetailStoryActionProvider) MenuItemCompat.getActionProvider(commentItem);
        praiseActionProvider = (DetailStoryActionProvider) MenuItemCompat.getActionProvider(praiseItem);
        commentActionProvider.setImageResource(R.drawable.comment_icon);
        praiseActionProvider.setImageResource(R.drawable.praise_icon);
        commentActionProvider.setOnClickListener(new DetailStoryActionProvider.OnClickListener() {
            @Override
            public void onClick() {
                startActivity(new Intent(mContext, CommentActivity.class)
                        .putExtra(Constant.COMMENTS, comments)
                        .putExtra(Constant.LONG_COMMENTS, long_comments)
                        .putExtra(Constant.SHORT_COMMENTS, short_comments)
                        .putExtra("id", currentId));
            }
        });
        praiseActionProvider.setOnClickListener(new DetailStoryActionProvider.OnClickListener() {
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
