package com.example.ben.zhihudaily.data.entity;


import com.example.ben.zhihudaily.data.BaseBean;

import java.util.List;

/**
 * Created by Zhou bangquan on 16/9/10.
 */

public class StoryThemeResult extends BaseBean {
    public String limit;
    public String[] subscribed;
    public List<StoryTheme> others;
}
