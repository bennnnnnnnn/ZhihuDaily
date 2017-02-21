package com.github.ben.zhihudaily.presenter;

import com.github.ben.zhihudaily.data.entity.Comment;

import java.util.List;

/**
 * Created on 16/11/22.
 * @author Ben
 */


public interface CommentContract {

    interface View extends BaseView<Presenter> {

        void refreshLongComments(List<Comment> comments);

        void refreshShortComments(List<Comment> comments);

        void clearShortComments();

        void showDialog(Comment comment);
    }

    interface Presenter extends BasePresenter {

        void requestShortCommentsList();
    }

}
