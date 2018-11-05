package com.github.ben.zhihudaily.ui.module.comment;


import android.annotation.SuppressLint;

import com.github.ben.zhihudaily.network.BenFactory;
import com.github.ben.zhihudaily.mvpbase.BasePresentImpl;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created on 16/11/22.
 *
 * @author Ben
 */

public class CommentPresenter extends BasePresentImpl<CommentContract.View> implements CommentContract.Presenter {

    @Override
    public void requestShortCommentsList() {
        requestShortComments();
    }

    @SuppressLint("CheckResult")
    @Override
    public void requestLongComments() {
        Disposable subscribe = BenFactory.getStoryApi()
                .getLongComments(mView.getStoryId())
                .compose(mView.bindToLife())
                .map(commentsResult -> {
                    if (commentsResult != null) {
                        return commentsResult.comments;
                    }
                    return null;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(comments -> mView.refreshLongComments(comments));
    }

    //前 20 条
    @SuppressLint("CheckResult")
    private void requestShortComments() {
        BenFactory.getStoryApi()
                .getShortComments(mView.getStoryId())
                .compose(mView.bindToLife())
                .map(commentsResult -> {
                    if (commentsResult != null) {
                        return commentsResult.comments;
                    }
                    return null;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(comments -> mView.refreshShortComments(comments));
    }
}