package com.github.ben.zhihudaily.data.entity;

import com.github.ben.zhihudaily.data.BaseBean;

import java.io.Serializable;

/**
 * Created on 16/11/1.
 * @author Ben
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
