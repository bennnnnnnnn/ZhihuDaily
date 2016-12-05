package com.example.ben.zhihudaily.presenter;

import android.text.TextUtils;

import com.example.ben.zhihudaily.R;
import com.example.ben.zhihudaily.data.entity.StoriesResult;
import com.example.ben.zhihudaily.data.entity.Story;
import com.example.ben.zhihudaily.network.BenFactory;
import com.example.ben.zhihudaily.ui.App;
import com.example.ben.zhihudaily.utils.Constant;
import com.example.ben.zhihudaily.utils.DateUtils;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Zhou bangquan on 16/11/24.
 */


public class HomePresenter implements HomeContract.Presenter {

    private HomeContract.View mHomeView;
    private Subscription subscription;
    private List<Story> topStories = new ArrayList<>();
    private List<Story> homeStories = new ArrayList<>();
    private long time;
    private String today;

    public HomePresenter(HomeContract.View homeView) {
        this.mHomeView = homeView;
        mHomeView.setPresenter(this);
    }

    @Override
    public void start() {
        getHomeList();
    }

    @Override
    public void refreshList() {
        getHomeList();
    }

    @Override
    public void loadBeforeStories() {
        time = time - Constant.A_DAY_MS;
        addHomeList(DateUtils.msToDate(time));
    }

    @Override
    public void setCurrentTile(int position) {
        if (position == 0) {
            mHomeView.setTitle(R.string.home_page);
        } else {
            String date = homeStories.get(position - 1).date;
            if (today.equals(date)) {
                mHomeView.setTitle(R.string.today_news);
            } else {
                mHomeView.setTitle(date);
            }
        }
    }

    private void addHomeList(String beforeTime) {
        unsubscribe();
        subscription = BenFactory.getStoryApi()
                .getBeforeDailyNews(TextUtils.isEmpty(beforeTime) ? DateUtils.msToDate(System.currentTimeMillis()) : beforeTime)
                .map(new Func1<StoriesResult, List<Story>>() {
                    @Override
                    public List<Story> call(StoriesResult storiesResult) {
                        if (storiesResult != null) {
                            return storiesResult.stories;
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Story>>() {
                    @Override
                    public void call(List<Story> stories) {
                        for (Story story : stories) {
                            story.date = DateUtils.dateWithWeekday(time - Constant.A_DAY_MS);
                            story.before = DateUtils.msToDate(time);
                        }
                        homeStories.addAll(stories);
                        changeReadState(stories);
                        mHomeView.beforeStoriesLoaded(stories);
                        mHomeView.isSwipeRefreshing(false);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mHomeView.isSwipeRefreshing(false);
                    }
                });
    }

    private void unsubscribe() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    private void getHomeList() {
        unsubscribe();
        subscription = BenFactory.getStoryApi()
                .getDailyNews("latest")
                .map(new Func1<StoriesResult, List<Story>>() {
                    @Override
                    public List<Story> call(StoriesResult storiesResult) {
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
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Story>>() {
                    @Override
                    public void call(List<Story> stories) {
                        for (Story story : stories) {
                            story.date = DateUtils.dateWithWeekday(time - Constant.A_DAY_MS);
                            story.before = DateUtils.msToDate(time);
                        }
                        homeStories = stories;
                        changeReadState(homeStories);
                        mHomeView.lataestStoriesLoaded(stories, topStories);
                        mHomeView.isSwipeRefreshing(false);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
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
