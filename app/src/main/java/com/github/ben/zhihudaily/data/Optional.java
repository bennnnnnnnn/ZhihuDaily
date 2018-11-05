package com.github.ben.zhihudaily.data;

import io.reactivex.annotations.Nullable;

/**
 * Created on 2018/11/5.
 *
 * @author Ben
 */
public class Optional<T> {
    private final T optional; // 网络请求接收到的返回结果

    public Optional(@Nullable T optional) {
        this.optional = optional;
    }

    public T get() {
        return optional;
    }
}
