package com.example.ben.zhihudaily.presenter;

import com.example.ben.zhihudaily.data.entity.Story;
import com.example.ben.zhihudaily.data.entity.StoryExtra;

import java.util.List;

/**
 * Created by Zhou bangquan on 16/11/24.
 */


public interface StoryDetailContract {

    interface View extends BaseView<Presenter> {

        void setStoryExtra(StoryExtra storyExtra);

        void setStoryList(List<Story> singleDailies);
    }

    interface Presenter extends BasePresenter {

        void getStoryExtra(String id);

        void getTopStories();

        void getBeforeStories(String before);
    }
}
