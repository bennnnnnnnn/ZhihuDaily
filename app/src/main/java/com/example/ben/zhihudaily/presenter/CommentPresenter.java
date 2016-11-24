package com.example.ben.zhihudaily.presenter;


import com.example.ben.zhihudaily.data.entity.Comment;
import com.example.ben.zhihudaily.data.entity.CommentsResult;
import com.example.ben.zhihudaily.network.BenFactory;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Zhou bangquan on 16/11/22.
 */

public class CommentPresenter implements CommentContract.Presenter {

    private CommentContract.View mCommentView;
    private String id;
    private Subscription subscription;

    public CommentPresenter(CommentContract.View commentView, String id) {
        this.mCommentView = commentView;
        this.id = id;
        mCommentView.setPresenter(this);
    }

    @Override
    public void requestShortCommentsList() {
        requestShortComments();
    }

    private void unsubscribe() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    private void requestLongComments() {
        unsubscribe();
        subscription = BenFactory.getStoryApi()
                .getLongComments(id)
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
                        mCommentView.refreshLongComments(comments);
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
                .getShortComments(id)
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
                        mCommentView.refreshShortComments(comments);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    @Override
    public void start() {
        requestLongComments();
    }
}