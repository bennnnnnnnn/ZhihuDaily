package com.github.ben.zhihudaily.network;

import com.github.ben.zhihudaily.data.HttpResult;
import com.github.ben.zhihudaily.data.Optional;

import io.reactivex.functions.Function;

/**
 * Created on 2018/11/5.
 *
 * @author Ben
 */
public class HttpResultFunc<T> implements Function<HttpResult<T>, Optional<T>> {
    @Override
    public Optional<T> apply(HttpResult<T> tHttpResult) throws Exception {
        if (null != tHttpResult) {
            // TODO result 异常码处理 throw 对应的 Exception;
            // 没异常 return 正常网络请求所得数据 如下;
            return new Optional<>(tHttpResult.data);
        } else {
            return new Optional<>(null);
        }
    }
}
