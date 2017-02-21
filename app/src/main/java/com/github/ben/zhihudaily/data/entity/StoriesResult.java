package com.github.ben.zhihudaily.data.entity;


import com.github.ben.zhihudaily.data.BaseBean;

import java.util.List;

/**
 * Created on 16/9/11.
 * @author Ben
 */
public class StoriesResult extends BaseBean {
    public String date;
    public List<Story> stories;
    public List<Story> top_stories;
}
