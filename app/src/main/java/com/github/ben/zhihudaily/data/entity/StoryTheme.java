package com.github.ben.zhihudaily.data.entity;

import com.github.ben.zhihudaily.data.BaseBean;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

/**
 * Created on 16/9/10.
 * @author Ben
 */

@Table("zhihuthemes")
public class StoryTheme extends BaseBean {
    public static final String COL_COLOR = "color";
    public static final String COL_THUMBNAIL = "thumbnail";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_FOLLOWED = "followed";
    public static final String COL_SELECTED = "selected";

    @Column(COL_COLOR)
    public String color;
    @Column(COL_THUMBNAIL)
    public String thumbnail;
    @Column(COL_DESCRIPTION)
    public String description;
    @Column(COL_ID)
    public String id;
    @Column(COL_NAME)
    public String name;
    @Column(COL_FOLLOWED)
    public boolean followed;

    public boolean selected;
}