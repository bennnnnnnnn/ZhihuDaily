package com.github.ben.zhihudaily.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.github.ben.zhihudaily.data.entity.Story;

import com.github.ben.zhihudaily.ui.fragment.StoryDetailFragment;

import java.util.List;


/**
 * Created on 16/9/20.
 *
 * @author Ben
 */


public class DetailAdapter extends FragmentPagerAdapter {

    private List<Story> dailies;

    public DetailAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setDailyNews(List<Story> mDailyNews) {
        this.dailies = mDailyNews;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return null == dailies ? 0 : dailies.size();
    }

    @Override
    public Fragment getItem(int position) {
        return null == dailies ? null : StoryDetailFragment.newInstance(dailies.get(position).id);
    }

}
