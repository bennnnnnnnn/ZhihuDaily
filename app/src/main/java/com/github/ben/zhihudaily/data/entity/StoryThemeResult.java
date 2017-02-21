package com.github.ben.zhihudaily.data.entity;


import com.github.ben.zhihudaily.data.BaseBean;

import java.util.List;

/**
 * Created on 16/9/10.
 * @author Ben
 */

public class StoryThemeResult extends BaseBean {
    public String limit;
    public String[] subscribed;
    public List<StoryTheme> others;
}
