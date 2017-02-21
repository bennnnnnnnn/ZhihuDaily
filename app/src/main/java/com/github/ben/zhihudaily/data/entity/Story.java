package com.github.ben.zhihudaily.data.entity;


import com.github.ben.zhihudaily.data.BaseBean;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

/**
 * Created on 16/9/11.
 * @author Ben
 */


@Table("storyDetail")
public class Story extends BaseBean {
    public static final String COL_IMAGES = "images";
    public static final String COL_IMAGE = "image";
    public static final String COL_TYPE = "type";
    public static final String COL_ID = "id";
    public static final String COL_GA_PREFIX = "ga_prefix";
    public static final String COL_TITLE = "title";
    public static final String COL_DATE = "date";
    public static final String COL_BEFORE = "before";
    public static final String COL_MULTIPIC = "multipic";
    public static final String COL_ISREAD = "isRead";

    @Column(COL_IMAGES)
    public String[] images;
    @Column(COL_IMAGE)
    public String image;
    @Column(COL_TYPE)
    public String type;
    @Column(COL_ID)
    public String id;
    @Column(COL_GA_PREFIX)
    public String ga_prefix;
    @Column(COL_TITLE)
    public String title;
    @Column(COL_DATE)
    public String date;
    @Column(COL_BEFORE)
    public String before;
    @Column(COL_MULTIPIC)
    public boolean multipic;
    @Column(COL_ISREAD)
    public boolean isRead;
}
