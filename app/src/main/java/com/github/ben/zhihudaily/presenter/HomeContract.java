package com.github.ben.zhihudaily.presenter;

import com.github.ben.zhihudaily.data.entity.Story;

import java.util.List;

/**
 * Created by Zhou bangquan on 16/11/24.
 */


public interface HomeContract {

    interface View extends BaseView<Presenter> {

        void lataestStoriesLoaded(List<Story> stories, List<Story> topStories);

        void beforeStoriesLoaded(List<Story> stories);

        void isSwipeRefreshing(boolean state);

        void setTitle(CharSequence title);

        void setTitle(int titleId);

    }

    interface Presenter extends BasePresenter {

        void refreshList();

        void loadBeforeStories();

        void setCurrentTile(int position);
    }

}
