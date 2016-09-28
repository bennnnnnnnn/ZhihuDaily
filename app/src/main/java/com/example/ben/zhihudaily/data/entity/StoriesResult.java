package com.example.ben.zhihudaily.data.entity;


import com.example.ben.zhihudaily.data.BaseBean;

import java.util.List;

/**
 * Created by Zhou bangquan on 16/9/11.
 */
public class StoriesResult extends BaseBean {
    public String date;
    public List<Story> stories;
    public List<Story> top_stories;
}
