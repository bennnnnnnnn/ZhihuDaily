package com.example.ben.zhihudaily.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ben.zhihudaily.data.entity.Story;
import com.example.ben.zhihudaily.ui.App;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.List;

import rx.Subscription;

/**
 * Created by Zhou bangquan on 16/9/23.
 */


public class BaseFragment extends Fragment {

    protected Subscription subscription;
    protected Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unsubscribe();
    }

    protected void unsubscribe() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
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
