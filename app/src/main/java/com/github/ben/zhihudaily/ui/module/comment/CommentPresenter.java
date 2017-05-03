package com.github.ben.zhihudaily.ui.module.comment;


import com.github.ben.zhihudaily.data.entity.Comment;
import com.github.ben.zhihudaily.data.entity.CommentsResult;
import com.github.ben.zhihudaily.network.BenFactory;
import com.github.ben.zhihudaily.mvpbase.BasePresentImpl;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created on 16/11/22.
 *
 * @author Ben
 */

public class CommentPresenter extends BasePresentImpl<CommentContract.View> implements CommentContract.Presenter {

    private Subscription subscription;

    @Override
    public void requestShortCommentsList() {
        requestShortComments();
    }

    private void unsubscribe() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @Override
    public void requestLongComments() {
        unsubscribe();
        subscription = BenFactory.getStoryApi()
                .getLongComments(mView.getStoryId())
                .map(new Func1<CommentsResult, List<Comment>>() {
                    @Override
                    public List<Comment> call(CommentsResult commentsResult) {
                        if (commentsResult != null) {
                            return commentsResult.comments;
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Comment>>() {
                    @Override
                    public void call(List<Comment> comments) {
                        mView.refreshLongComments(comments);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    //前 20 条
    private void requestShortComments() {
        unsubscribe();
        subscription = BenFactory.getStoryApi()
                .getShortComments(mView.getStoryId())
                .map(new Func1<CommentsResult, List<Comment>>() {
                    @Override
                    public List<Comment> call(CommentsResult commentsResult) {
                        if (commentsResult != null) {
                            return commentsResult.comments;
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Comment>>() {
                    @Override
                    public void call(List<Comment> comments) {
                        mView.refreshShortComments(comments);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }
}