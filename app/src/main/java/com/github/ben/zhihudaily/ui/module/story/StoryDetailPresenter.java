package com.github.ben.zhihudaily.ui.module.story;

import android.text.TextUtils;

import com.github.ben.zhihudaily.data.entity.StoriesResult;
import com.github.ben.zhihudaily.data.entity.Story;
import com.github.ben.zhihudaily.data.entity.StoryExtra;
import com.github.ben.zhihudaily.data.entity.ThemeStories;
import com.github.ben.zhihudaily.network.BenFactory;
import com.github.ben.zhihudaily.mvpbase.BasePresentImpl;
import com.github.ben.zhihudaily.ui.App;
import com.github.ben.zhihudaily.utils.Constant;
import com.github.ben.zhihudaily.utils.DateUtils;
import com.litesuits.orm.db.model.ConflictAlgorithm;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created on 16/11/24.
 *
 * @author Ben
 */


public class StoryDetailPresenter extends BasePresentImpl<StoryDetailContract.View> implements StoryDetailContract.Presenter {

    private Subscription subscription;
    private List<Story> dailies;

    private void unsubscribe() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @Override
    public void getStoryExtra(int position) {
        Story story = dailies.get(position);
        getStoryExtra(story.id);
    }

    @Override
    public void updateStoryState(int position) {
        Story story = dailies.get(position);
        if (!story.isRead) {
            story.isRead = true;
            App.mDb.update(story, ConflictAlgorithm.Replace);
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
                        mView.setStoryExtra(storyExtra);
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
                        dailies = singleDailies;
                        mView.setStoryList(dailies);
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
                .getBeforeDailyNews(TextUtils.isEmpty(before) ? DateUtils.msToDate(System.currentTimeMillis() + Constant.A_DAY_MS) : before)
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
                        mView.setStoryList(dailies);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    @Override
    public void getThemeStories(String themeId) {
        unsubscribe();
        subscription = BenFactory.getStoryThemeApi()
                .getThemeStories(themeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ThemeStories>() {
                    @Override
                    public void call(ThemeStories themeStories) {
                        dailies = themeStories.stories;
                        mView.setStoryList(dailies);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    public String getCurrentId(int position) {
        return dailies.get(position).id;
    }

}
