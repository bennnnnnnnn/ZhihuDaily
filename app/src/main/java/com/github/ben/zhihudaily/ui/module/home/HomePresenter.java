package com.github.ben.zhihudaily.ui.module.home;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.github.ben.zhihudaily.R;
import com.github.ben.zhihudaily.data.entity.Story;
import com.github.ben.zhihudaily.mvpbase.BasePresentImpl;
import com.github.ben.zhihudaily.network.BenFactory;
import com.github.ben.zhihudaily.ui.App;
import com.github.ben.zhihudaily.utils.Constant;
import com.github.ben.zhihudaily.utils.DateUtils;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created on 16/11/24.
 *
 * @author Ben
 */


public class HomePresenter extends BasePresentImpl<HomeContract.View> implements HomeContract.Presenter {

    private List<Story> topStories = new ArrayList<>();
    private List<Story> homeStories = new ArrayList<>();
    private long time;
    private String today;

    @Override
    public void getHomeList() {
        getHomeListMsg();
    }

    @Override
    public void refreshHomeList() {
        getHomeListMsg();
    }

    @Override
    public void loadBeforeStories() {
        time = time - Constant.A_DAY_MS;
        addHomeList(DateUtils.msToDate(time));
    }

    @Override
    public void setCurrentTile(int position) {
        if (position == 0) {
            mView.setTitle(R.string.home_page);
        } else {
            String date = homeStories.get(position - 1).date;
            if (today.equals(date)) {
                mView.setTitle(R.string.today_news);
            } else {
                mView.setTitle(date);
            }
        }
    }

    @SuppressLint("CheckResult")
    private void addHomeList(String beforeTime) {
        BenFactory.getStoryApi()
                .getBeforeDailyNews(TextUtils.isEmpty(beforeTime) ? DateUtils.msToDate(System.currentTimeMillis()) : beforeTime)
                .compose(mView.bindToLife())
                .map(storiesResult -> {
                    if (storiesResult != null) {
                        return storiesResult.stories;
                    }
                    return null;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stories -> {
                    for (Story story : stories) {
                        story.date = DateUtils.dateWithWeekday(time - Constant.A_DAY_MS);
                        story.before = DateUtils.msToDate(time);
                    }
                    homeStories.addAll(stories);
                    changeReadState(stories);
                    mView.beforeStoriesLoaded(stories);
                    mView.isSwipeRefreshing(false);
                }, throwable -> mView.isSwipeRefreshing(false));
    }

    @SuppressLint("CheckResult")
    private void getHomeListMsg() {
        BenFactory.getStoryApi()
                .getDailyNews("latest")
                .compose(mView.bindToLife())
                .map(storiesResult -> {
                    if (storiesResult != null) {
                        topStories = storiesResult.top_stories;
                        today = DateUtils.dateWithWeekday(System.currentTimeMillis());
                        time = System.currentTimeMillis() + Constant.A_DAY_MS;
                        for (Story daily : topStories) {
                            daily.date = DateUtils.dateWithWeekday(time - Constant.A_DAY_MS);
                            daily.before = DateUtils.msToDate(time);
                        }
                        return storiesResult.stories;
                    }
                    return null;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stories -> {
                    for (Story story : stories) {
                        story.date = DateUtils.dateWithWeekday(time - Constant.A_DAY_MS);
                        story.before = DateUtils.msToDate(time);
                    }
                    homeStories = stories;
                    changeReadState(homeStories);
                    mView.latestStoriesLoaded(stories, topStories);
                    mView.isSwipeRefreshing(false);
                });
    }

    private void changeReadState(List<Story> stories) {
        for (Story story : stories) {
            changeState(story);
        }
    }

    private void changeState(Story story) {
        QueryBuilder<Story> qb = new QueryBuilder<>(Story.class)
                .whereEquals(Story.COL_ID, story.id)
                .whereAppendAnd()
                .whereEquals(Story.COL_TITLE, story.title);
        List<Story> stories = App.mDb.query(qb);
        if (stories != null && stories.size() > 0) {
            story.isRead = stories.get(0).isRead;
        } else {
            App.mDb.save(story);
        }
    }
}
