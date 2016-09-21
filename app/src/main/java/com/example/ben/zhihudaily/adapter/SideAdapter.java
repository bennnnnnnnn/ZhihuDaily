package com.example.ben.zhihudaily.adapter;

import android.content.Context;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.ben.zhihudaily.R;
import com.example.ben.zhihudaily.data.entity.DailyTheme;
import com.example.ben.zhihudaily.ui.MainActivity;

import java.util.List;

/**
 * Created by Zhou bangquan on 16/9/10.
 */

public class SideAdapter extends BaseAdapter {

    private Context mContext;
    private DrawerLayout mDrawerLayout;
    private LayoutInflater inflater;

    private List<DailyTheme> dailyThemes;

    private final int VIEW_TYPE = 2;
    private final int TYPE_0 = 0;
    private final int TYPE_1 = 1;

    public SideAdapter(Context context, DrawerLayout drawerLayout) {
        this.mContext = context;
        this.mDrawerLayout = drawerLayout;
        inflater = LayoutInflater.from(mContext);
    }

    public void setDailyThemes(List<DailyTheme> dailyThemes) {
        this.dailyThemes = dailyThemes;
        notifyDataSetChanged();
    }

    private void showOrCloseSideView() {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            this.mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_0;
        } else {
            return TYPE_1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE;
    }

    @Override
    public int getCount() {
        return dailyThemes == null ? 0 : dailyThemes.size();
    }

    @Override
    public Object getItem(int position) {
        return dailyThemes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View cv, ViewGroup parent) {
        ViewHolder v = null;
        ViewHolder1 item = null;
        int type = getItemViewType(position);
        if (null == cv) {
            switch (type) {
                case TYPE_0:
                    cv = inflater.inflate(R.layout.home_item, parent, false);
                    v = new ViewHolder();
                    cv.setTag(R.id.tag_zero, v);
                    break;
                case TYPE_1:
                    cv = inflater.inflate(R.layout.slide_item, parent, false);
                    item = new ViewHolder1();
                    item.itemLayout = (RelativeLayout) cv.findViewById(R.id.item_layout);
                    item.themeTitle = (TextView) cv.findViewById(R.id.theme_title_textview);
                    item.tipView = (ImageView) cv.findViewById(R.id.theme_state_image);
                    cv.setTag(R.id.tag_first, item);
                    break;
            }
        } else {
            switch (type) {
                case TYPE_0:
                    v = (ViewHolder) cv.getTag(R.id.tag_zero);
                    break;
                case TYPE_1:
                    item = (ViewHolder1) cv.getTag(R.id.tag_first);
                    break;
            }
        }

        switch (type) {
            case TYPE_1:
                DailyTheme dailyTheme = dailyThemes.get(position);
                item.themeTitle.setText(dailyTheme.name);
                item.itemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showOrCloseSideView();
                    }
                });
        }
        return cv;
    }

    private class ViewHolder {

    }

    private class ViewHolder1 {
        RelativeLayout itemLayout;
        TextView themeTitle;
        ImageView tipView;
    }

}
