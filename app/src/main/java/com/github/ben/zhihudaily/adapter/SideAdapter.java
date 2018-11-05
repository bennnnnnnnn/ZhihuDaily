package com.github.ben.zhihudaily.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ben.zhihudaily.R;
import com.github.ben.zhihudaily.data.entity.StoryTheme;
import com.github.ben.zhihudaily.ui.App;
import com.github.ben.zhihudaily.utils.ToastUtils;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.model.ConflictAlgorithm;

import java.util.List;

/**
 * Created on 16/9/10.
 *
 * @author Ben
 */

public class SideAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;

    private List<StoryTheme> dailyThemes;
    private List<StoryTheme> requestThemes;

    private final int VIEW_TYPE = 2;
    private final int TYPE_0 = 0;
    private final int TYPE_1 = 1;

    public boolean isHomePage;

    public SideAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    public void setDailyThemes(List<StoryTheme> dailyThemes, List<StoryTheme> requestThemes) {
        this.dailyThemes = dailyThemes;
        this.requestThemes = requestThemes;
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
        return dailyThemes == null ? 1 : dailyThemes.size() + 1;
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
    public View getView(final int position, View cv, ViewGroup parent) {
        ViewHolder homeItem = null;
        ViewHolder1 item = null;
        int type = getItemViewType(position);
        if (null == cv) {
            switch (type) {
                case TYPE_0:
                    cv = inflater.inflate(R.layout.adapter_item_home_theme, parent, false);
                    homeItem = new ViewHolder();
                    homeItem.itemLayout = cv.findViewById(R.id.home_item);
                    cv.setTag(R.id.tag_zero, homeItem);
                    break;
                case TYPE_1:
                    cv = inflater.inflate(R.layout.adapter_item_story_theme, parent, false);
                    item = new ViewHolder1();
                    item.itemLayout = cv.findViewById(R.id.item_layout);
                    item.themeTitle = cv.findViewById(R.id.theme_title_textview);
                    item.tipView = cv.findViewById(R.id.theme_state_image);
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
                homeItem.itemLayout.setBackgroundColor(ContextCompat.getColor(mContext, isHomePage ? R.color.side_item_selected : R.color.side_bg));
                break;
            case TYPE_1:
                final StoryTheme dailyTheme = dailyThemes.get(position - 1);
                item.itemLayout.setBackgroundColor(ContextCompat.getColor(mContext, dailyTheme.selected ? R.color.side_item_selected : R.color.side_bg));
                item.themeTitle.setText(dailyTheme.name);
                item.tipView.setImageResource(dailyTheme.followed ? R.drawable.right_icon : R.drawable.plus_list_icon);
                item.tipView.setOnClickListener(v -> updateThemeState(dailyTheme));
                break;
            default:
                break;
        }
        return cv;
    }

    private void updateThemeState(StoryTheme storyTheme) {
        if (!storyTheme.followed) {
            ToastUtils.shortToast(mContext, "关注成功,关注内容会在首页呈现哦~");
        } else {
            ToastUtils.shortToast(mContext, "卧槽,居然取关窝,其他事件宝宝懒得做了!");
        }
        storyTheme.followed = !storyTheme.followed;
        App.mDb.update(storyTheme, ConflictAlgorithm.Replace);
        resetAndReorderThemes();
        notifyDataSetChanged();
    }

    private void resetAndReorderThemes() {
        if (dailyThemes != null) {
            dailyThemes.clear();
            dailyThemes.addAll(requestThemes);
        }
        int size = requestThemes.size();
        int amount = 0;
        for (int i = size - 1; i > -1; i--) {
            StoryTheme theme = requestThemes.get(i);
            QueryBuilder<StoryTheme> qb = new QueryBuilder<>(StoryTheme.class)
                    .whereEquals(StoryTheme.COL_NAME, theme.name)
                    .whereAppendAnd()
                    .whereEquals(StoryTheme.COL_ID, theme.id)
                    .whereAppendAnd()
                    .whereEquals(StoryTheme.COL_FOLLOWED, true);
            if (App.mDb.query(qb) != null && App.mDb.query(qb).size() > 0) {
                theme.followed = true;
                dailyThemes.remove(i + (amount++));
                dailyThemes.add(0, theme);
            } else {
                theme.followed = false;
            }
        }
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
