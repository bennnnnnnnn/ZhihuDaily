package com.github.ben.zhihudaily.mvpbase;

/**
 * Created on 17/5/3.
 *
 * @author Ben
 */


public class BasePresentImpl<V extends BaseView> implements BasePresenter<V> {
    protected V mView;

    @Override
    public void attachView(V view) {
        this.mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
    }
}
