package com.github.ben.zhihudaily.data.entity;

import com.github.ben.zhihudaily.data.BaseBean;

import java.io.Serializable;

/**
 * Created by Zhou bangquan on 16/11/1.
 */


public class Comment extends BaseBean implements Serializable {
    public String author;
    public String content;
    public String avatar;
    public long time;
    public String replier;
    public String id;
    public String likes;
    public boolean isPraised;
}
