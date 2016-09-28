package com.example.ben.zhihudaily.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.ben.zhihudaily.R;
import com.example.ben.zhihudaily.data.entity.StoryTheme;
import com.example.ben.zhihudaily.ui.App;

import java.util.List;

/**
 * Created by Zhou bangquan on 16/9/10.
 */

public class SideAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;

    private List<StoryTheme> dailyThemes;

    private final int VIEW_TYPE = 2;
    private final int TYPE_0 = 0;
    private final int TYPE_1 = 1;

    public boolean isHomePage;

    public SideAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    public void setDailyThemes(List<StoryTheme> dailyThemes) {
        this.dailyThemes = dailyThemes;
        notifyDataSetChanged();
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
        ViewHolder homeItem = null;
        ViewHolder1 item = null;
        int type = getItemViewType(position);
        if (null == cv) {
            switch (type) {
                case TYPE_0:
                    cv = inflater.inflate(R.layout.home_item, parent, false);
                    homeItem = new ViewHolder();
                    homeItem.itemLayout = (LinearLayout) cv.findViewById(R.id.home_item);
                    cv.setTag(R.id.tag_zero, homeItem);
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
                    homeItem = (ViewHolder) cv.getTag(R.id.tag_zero);
                    break;
                case TYPE_1:
                    item = (ViewHolder1) cv.getTag(R.id.tag_first);
                    break;
            }
        }

        switch (type) {
            case TYPE_0:
                if (isHomePage) {
                    homeItem.itemLayout.setBackgroundColor(mContext.getResources().getColor(R.color.md_grey_300));
                } else {
                    homeItem.itemLayout.setBackgroundColor(mContext.getResources().getColor(R.color.md_grey_100));
                }
                break;
            case TYPE_1:
                final StoryTheme dailyTheme = dailyThemes.get(position - 1);
                item.themeTitle.setText(dailyTheme.name);
                item.tipView.setImageResource(dailyTheme.selected ? R.drawable.go_icon : R.drawable.plus_icon);
                item.tipView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateThemeState(dailyTheme);
                    }
                });
                break;
            default:
                break;
        }
        return cv;
    }

    private void updateThemeState(StoryTheme s) {
        if (!s.selected) {
            Toast.makeText(mContext, "关注成功,关注内容会在首页呈现哦~", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mContext, "卧槽,居然取关窝,其他事件宝宝懒得做了!", Toast.LENGTH_LONG).show();
        }
        s.selected = !s.selected;
        App.mDb.update(s);
        notifyDataSetChanged();
    }

    private class ViewHolder {
        LinearLayout itemLayout;
    }

    private class ViewHolder1 {
        RelativeLayout itemLayout;
        TextView themeTitle;
        ImageView tipView;
    }

}
