package com.example.ben.zhihudaily.data.entity;


import com.example.ben.zhihudaily.data.BaseBean;

import java.util.List;

/**
 *Created by Zhou bangquan on 16/9/11.
 */

public class DailyNews extends BaseBean {
    public String date;
    public List<SingleDaily> stories;
    public List<SingleDaily> top_stories;

}
