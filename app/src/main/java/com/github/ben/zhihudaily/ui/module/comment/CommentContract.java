package com.github.ben.zhihudaily.ui.module.comment;

import com.github.ben.zhihudaily.data.entity.Comment;
import com.github.ben.zhihudaily.mvpbase.BasePresenter;
import com.github.ben.zhihudaily.mvpbase.BaseView;

import java.util.List;

/**
 * Created on 16/11/22.
 * @author Ben
 */


public interface CommentContract {

    interface View extends BaseView {
        String getStoryId();

        void refreshLongComments(List<Comment> comments);

        void refreshShortComments(List<Comment> comments);

        void clearShortComments();

        void showDialog(Comment comment);
    }

    interface Presenter extends BasePresenter<View> {

        void requestShortCommentsList();

        void requestLongComments();
    }

}
