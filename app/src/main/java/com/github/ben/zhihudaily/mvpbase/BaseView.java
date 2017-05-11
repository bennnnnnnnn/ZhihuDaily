package com.github.ben.zhihudaily.mvpbase;

import android.content.Context;

import com.trello.rxlifecycle2.LifecycleTransformer;

/**
 * Created on 16/11/22.
 *
 * @author Ben
 */


public interface BaseView {
    Context getContext();

    <T> LifecycleTransformer<T> bindToLife();
}
