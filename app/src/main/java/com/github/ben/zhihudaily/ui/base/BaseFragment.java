package com.github.ben.zhihudaily.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ben.zhihudaily.data.entity.Story;
import com.github.ben.zhihudaily.ui.App;
import com.github.ben.zhihudaily.utils.SharePreUtils;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.trello.rxlifecycle2.components.support.RxFragment;

import java.util.List;

/**
 * Created on 16/9/23.
 *
 * @author Ben
 */


public class BaseFragment extends RxFragment {

    protected Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected boolean isNight() {
        return SharePreUtils.isNight();
    }

    public void changeReadState(List<Story> stories) {
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
