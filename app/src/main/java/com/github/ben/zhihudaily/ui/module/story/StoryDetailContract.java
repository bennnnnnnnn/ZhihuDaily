package com.github.ben.zhihudaily.ui.module.story;

import com.github.ben.zhihudaily.data.entity.Story;
import com.github.ben.zhihudaily.data.entity.StoryExtra;
import com.github.ben.zhihudaily.mvpbase.BasePresenter;
import com.github.ben.zhihudaily.mvpbase.BaseView;

import java.util.List;

/**
 * Created on 16/11/24.
 *
 * @author Ben
 */


public interface StoryDetailContract {

    interface View extends BaseView {

        void setStoryExtra(StoryExtra storyExtra);

        void setStoryList(List<Story> singleDailies);

    }

    interface Presenter extends BasePresenter<View> {

        void getStoryExtra(int position);

        void getStoryExtra(String id);

        void updateStoryState(int position);

        void getTopStories();

        void getBeforeStories(String before);

        void getThemeStories(String themeId);

        String getCurrentId(int position);
    }
}
