package com.github.ben.zhihudaily.mvpbase;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.ben.zhihudaily.mvpbase.BasePresentImpl;
import com.github.ben.zhihudaily.mvpbase.BaseView;
import com.github.ben.zhihudaily.ui.base.ToolBarActivity;

import java.lang.reflect.ParameterizedType;

/**
 * Created on 17/5/3.
 *
 * @author Ben
 */


public abstract class MVPBaseActivity<V extends BaseView, T extends BasePresentImpl<V>> extends ToolBarActivity implements BaseView {
    protected T mPresenter;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mPresenter = getInstance(this, 1);
        mPresenter.attachView((V) this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        super.onDestroy();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance(Object o, int i) {
        try {
            return ((Class<T>) ((ParameterizedType) (o.getClass().getGenericSuperclass())).getActualTypeArguments()[i]).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }
}
