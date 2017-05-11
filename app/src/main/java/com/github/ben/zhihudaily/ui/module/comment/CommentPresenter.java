package com.github.ben.zhihudaily.ui.module.comment;


import com.github.ben.zhihudaily.data.entity.Comment;
import com.github.ben.zhihudaily.data.entity.CommentsResult;
import com.github.ben.zhihudaily.network.BenFactory;
import com.github.ben.zhihudaily.mvpbase.BasePresentImpl;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
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

    @Override
    public void requestLongComments() {
        BenFactory.getStoryApi()
                .getLongComments(mView.getStoryId())
                .compose(mView.<CommentsResult>bindToLife())
                .map(new Function<CommentsResult, List<Comment>>() {
                    @Override
                    public List<Comment> apply(CommentsResult commentsResult) {
                        if (commentsResult != null) {
                            return commentsResult.comments;
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Comment>>() {
                    @Override
                    public void accept(List<Comment> comments) {
                        mView.refreshLongComments(comments);
                    }
                });
    }

    //前 20 条
    private void requestShortComments() {
        BenFactory.getStoryApi()
                .getShortComments(mView.getStoryId())
                .compose(mView.<CommentsResult>bindToLife())
                .map(new Function<CommentsResult, List<Comment>>() {
                    @Override
                    public List<Comment> apply(CommentsResult commentsResult) {
                        if (commentsResult != null) {
                            return commentsResult.comments;
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Comment>>() {
                    @Override
                    public void accept(List<Comment> comments) {
                        mView.refreshShortComments(comments);
                    }
                });
    }
}