package com.github.ben.zhihudaily.ui.module.story;

import android.annotation.SuppressLint;
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

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created on 16/11/24.
 *
 * @author Ben
 */


public class StoryDetailPresenter extends BasePresentImpl<StoryDetailContract.View> implements StoryDetailContract.Presenter {

    private List<Story> dailies;

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

    @SuppressLint("CheckResult")
    @Override
    public void getStoryExtra(String id) {
        BenFactory.getStoryApi()
                .getStoryExtra(id)
                .compose(mView.<StoryExtra>bindToLife())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<StoryExtra>() {
                    @Override
                    public void accept(@NonNull StoryExtra storyExtra) {
                        mView.setStoryExtra(storyExtra);
                    }
                });
    }

    @SuppressLint("CheckResult")
    @Override
    public void getTopStories() {
        BenFactory.getStoryApi()
                .getDailyNews("latest")
                .compose(mView.<StoriesResult>bindToLife())
                .map(new Function<StoriesResult, List<Story>>() {
                    @Override
                    public List<Story> apply(@NonNull StoriesResult dailyNews) {
                        if (dailyNews != null) {
                            return dailyNews.top_stories;
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Story>>() {
                    @Override
                    public void accept(@NonNull List<Story> singleDailies) {
                        dailies = singleDailies;
                        mView.setStoryList(dailies);
                    }
                });
    }

    @SuppressLint("CheckResult")
    @Override
    public void getBeforeStories(String before) {
        BenFactory.getStoryApi()
                .getBeforeDailyNews(TextUtils.isEmpty(before) ? DateUtils.msToDate(System.currentTimeMillis() + Constant.A_DAY_MS) : before)
                .compose(mView.<StoriesResult>bindToLife())
                .map(new Function<StoriesResult, List<Story>>() {
                    @Override
                    public List<Story> apply(@NonNull StoriesResult dailyNews) {
                        if (dailyNews != null) {
                            return dailyNews.stories;
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Story>>() {
                    @Override
                    public void accept(@NonNull List<Story> singleDailies) {
                        dailies = singleDailies;
                        mView.setStoryList(dailies);
                    }
                });
    }

    @SuppressLint("CheckResult")
    @Override
    public void getThemeStories(String themeId) {
        BenFactory.getStoryThemeApi()
                .getThemeStories(themeId)
                .compose(mView.<ThemeStories>bindToLife())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ThemeStories>() {
                    @Override
                    public void accept(@NonNull ThemeStories themeStories) {
                        dailies = themeStories.stories;
                        mView.setStoryList(dailies);
                    }
                });
    }

    public String getCurrentId(int position) {
        return dailies.get(position).id;
    }

}
