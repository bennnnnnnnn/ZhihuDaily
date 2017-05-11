package com.github.ben.zhihudaily.mvpbase;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.ben.zhihudaily.ui.base.BaseFragment;
import com.trello.rxlifecycle2.LifecycleTransformer;

import java.lang.reflect.ParameterizedType;

/**
 * Created on 17/5/3.
 *
 * @author Ben
 */


public abstract class MVPBaseFragment<V extends BaseView, T extends BasePresentImpl<V>> extends BaseFragment implements BaseView {
    protected T mPresenter;

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mPresenter = getInstance(this, 1);
        mPresenter.attachView((V) this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        super.onDestroyView();
    }

    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Override
    public <T> LifecycleTransformer<T> bindToLife() {
        return this.<T>bindToLifecycle();
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance(Object o, int i) {
        try {
            return ((Class<T>) ((ParameterizedType) (o.getClass().getGenericSuperclass())).getActualTypeArguments()[i]).newInstance();
        } catch (InstantiationException | IllegalAccessException | java.lang.InstantiationException | ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }
}
