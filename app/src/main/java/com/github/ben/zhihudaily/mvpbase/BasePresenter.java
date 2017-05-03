package com.github.ben.zhihudaily.mvpbase;

/**
 * Created on 16/11/22.
 * @author Ben
 */


public interface BasePresenter<V extends BaseView> {
    void attachView(V view);

    void detachView();
}
