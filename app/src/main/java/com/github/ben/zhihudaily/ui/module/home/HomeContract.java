package com.github.ben.zhihudaily.ui.module.home;

import com.github.ben.zhihudaily.data.entity.Story;
import com.github.ben.zhihudaily.mvpbase.BasePresenter;
import com.github.ben.zhihudaily.mvpbase.BaseView;

import java.util.List;

/**
 * Created on 16/11/24.
 * @author Ben
 */


public interface HomeContract {

    interface View extends BaseView {

        void latestStoriesLoaded(List<Story> stories, List<Story> topStories);

        void beforeStoriesLoaded(List<Story> stories);

        void isSwipeRefreshing(boolean state);

        void setTitle(CharSequence title);

        void setTitle(int titleId);

    }

    interface Presenter extends BasePresenter<View> {

        void getHomeList();

        void refreshHomeList();

        void loadBeforeStories();

        void setCurrentTile(int position);
    }

}
