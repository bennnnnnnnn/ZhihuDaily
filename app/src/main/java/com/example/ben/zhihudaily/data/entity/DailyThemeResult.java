package com.example.ben.zhihudaily.data.entity;


import com.example.ben.zhihudaily.data.BaseBean;

import java.util.List;

/**
 * Created by Zhou bangquan on 16/9/10.
 */

public class DailyThemeResult extends BaseBean {
    public String limit;
    public String[] subscribed;
    public List<DailyTheme> others;
}
