package com.github.ben.zhihudaily.data.entity;

import com.github.ben.zhihudaily.data.BaseBean;

import java.util.List;

/**
 * Created by Zhou bangquan on 16/9/21.
 */


public class ThemeStories extends BaseBean {
    public List<Story> stories;
    public String description;
    public String background;
    public String color;
    public String name;
    public String image;
    public List<Editor> editors;
    public String image_source;
}
