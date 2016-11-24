package com.example.ben.zhihudaily.presenter;

import com.example.ben.zhihudaily.data.entity.StoriesResult;
import com.example.ben.zhihudaily.data.entity.Story;
import com.example.ben.zhihudaily.data.entity.StoryExtra;
import com.example.ben.zhihudaily.network.BenFactory;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Zhou bangquan on 16/11/24.
 */


public class StoryDetailPresenter implements StoryDetailContract.Presenter {

    private StoryDetailContract.View mStoryDetailView;
    private Subscription subscription;

    public StoryDetailPresenter(StoryDetailContract.View storyDetailView) {
        this.mStoryDetailView = storyDetailView;
        mStoryDetailView.setPresenter(this);
    }

    private void unsubscribe() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @Override
    public void getStoryExtra(String id) {
        unsubscribe();
        subscription = BenFactory.getStoryApi()
                .getStoryExtra(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<StoryExtra>() {
                    @Override
                    public void call(StoryExtra storyExtra) {
                        mStoryDetailView.setStoryExtra(storyExtra);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    @Override
    public void getTopStories() {
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
                       mStoryDetailView.setStoryList(singleDailies);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    @Override
    public void getBeforeStories(String before) {
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
                        mStoryDetailView.setStoryList(singleDailies);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    @Override
    public void start() {

    }

}
