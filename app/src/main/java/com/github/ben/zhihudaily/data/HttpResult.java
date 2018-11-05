package com.github.ben.zhihudaily.data;

/**
 * Created on 2018/11/5.
 *
 * @author Ben
 * 状态参数 + 业务数据（T）
 * 统一处理状态参数
 */
public class HttpResult<T> {
    public String 公共参数;

    public T data; // 业务数据
}
