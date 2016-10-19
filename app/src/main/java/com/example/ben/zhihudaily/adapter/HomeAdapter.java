package com.example.ben.zhihudaily.adapter;

import android.content.Context;
import android.content.Intent;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.example.ben.zhihudaily.R;
import com.example.ben.zhihudaily.data.entity.Story;

import com.example.ben.zhihudaily.ui.App;
import com.example.ben.zhihudaily.ui.activity.StoryDetailActivity;
import com.example.ben.zhihudaily.utils.Constant;
import com.example.ben.zhihudaily.utils.GlideUtils;
import com.litesuits.orm.db.model.ConflictAlgorithm;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhou bangquan on 16/9/11.
 */

public class HomeAdapter extends RecyclerView.Adapter {
    List<Story> mDailyNews;
    List<Story> mBannerThemes;
    Context context;
    static final int TYPE_HEAD = 0;
    static final int TYPE_CONTENT = 1;
    boolean initialized = false;

    public HomeAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEAD) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.banner_header_layout, parent, false);
            return new HeadViewHolder(view);
        } else if (viewType == TYPE_CONTENT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.homedaily_item_layout, parent, false);
            return new HomeViewHolder(view);
        }
        return null;
    }

    public void setDailyNews(List<Story> mDailyNews, List<Story> mBannerThemes) {
        this.mDailyNews = mDailyNews;
        this.mBannerThemes = mBannerThemes;
        notifyDataSetChanged();
    }

    public void addDailyNews(List<Story> mDailyNews) {
        this.mDailyNews.addAll(mDailyNews);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDailyNews == null ? 1 : mDailyNews.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return TYPE_HEAD;
        return TYPE_CONTENT;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        switch (type) {
            case TYPE_HEAD:
                HeadViewHolder headViewHolder = (HeadViewHolder) holder;
                if (!initialized) {
                    initialized = true;
                    initConvenientBanner(headViewHolder, mBannerThemes);
                    headViewHolder.mBannerViewPager.startTurning(5000);
                } else {
                    initConvenientBanner(headViewHolder, mBannerThemes);
                }
                if (null == mBannerThemes || mBannerThemes.size() <= 0) {
                    headViewHolder.mBannerViewPager.setVisibility(View.GONE);
                } else {
                    headViewHolder.mBannerViewPager.setVisibility(View.VISIBLE);
                }
                headViewHolder.mBannerViewPager.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Story daily = mBannerThemes.get(position);
                        context.startActivity(new Intent(context, StoryDetailActivity.class).putExtra("id", daily.id)
                                .putExtra("before", daily.before).putExtra("type", Constant.TOP_STORIES));
                    }
                });
                break;
            case TYPE_CONTENT:
                HomeViewHolder homeViewHolder = (HomeViewHolder) holder;
                final Story story = mDailyNews.get(position - 1);
                if (position == 1) {
                    homeViewHolder.dateTextView.setText(R.string.today_news);
                    homeViewHolder.dateTextView.setVisibility(View.VISIBLE);
                } else if (!(story.date).equals(mDailyNews.get(position - 2).date)) {
                    homeViewHolder.dateTextView.setText(story.date);
                    homeViewHolder.dateTextView.setVisibility(View.VISIBLE);
                } else {
                    homeViewHolder.dateTextView.setVisibility(View.GONE);
                }
                homeViewHolder.titleTextView.setText(story.title);
                if (story.isRead) {
                    homeViewHolder.titleTextView.setTextColor(context.getResources().getColor(R.color.textReadColor));
                } else {
                    homeViewHolder.titleTextView.setTextColor(context.getResources().getColor(R.color.textColor));
                }
                GlideUtils.loadingImage(context, homeViewHolder.newsImageView, story.images[0]);
                if (story.multipic) {
                    homeViewHolder.multipLayout.setVisibility(View.VISIBLE);
                } else {
                    homeViewHolder.multipLayout.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    class HomeViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_cardview)
        CardView mCardView;
        @Bind(R.id.news_image)
        ImageView newsImageView;
        @Bind(R.id.news_title)
        TextView titleTextView;
        @Bind(R.id.date_textView)
        TextView dateTextView;
        @Bind(R.id.multip_layout)
        RelativeLayout multipLayout;

        HomeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.item_cardview)
        void onDetail(View v) {
            Story story = mDailyNews.get(getLayoutPosition() - 1);
            if (!story.isRead) {
                story.isRead = true;
                App.mDb.update(story, ConflictAlgorithm.Replace);
//                titleTextView.setTextColor(context.getResources().getColor(R.color.textReadColor));
            }
            v.getContext().startActivity(new Intent(context, StoryDetailActivity.class).putExtra("id", story.id)
                    .putExtra("before", story.before).putExtra("type", Constant.STORY));
        }
    }

    class HeadViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.banner_viewpager)
        ConvenientBanner mBannerViewPager;

        HeadViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private void initConvenientBanner(HeadViewHolder headViewHolder, List<Story> singleDailies) {
        headViewHolder.mBannerViewPager.setPages(
                new CBViewHolderCreator<LocalImageHolderView>() {
                    @Override
                    public LocalImageHolderView createHolder() {
                        return new LocalImageHolderView();
                    }
                }, singleDailies)
                .setPageIndicator(new int[]{R.drawable.icon_banner_normal, R.drawable.icon_banner_selected})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);

        headViewHolder.mBannerViewPager.setcurrentitem(0);

    }

    private class LocalImageHolderView implements Holder<Story> {
        private ImageView imageView;
        private TextView textView;

        @Override
        public View createView(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.banner_layout, null);
            imageView = (ImageView) view.findViewById(R.id.banner_image);
            textView = (TextView) view.findViewById(R.id.banner_title);
            return view;
        }

        @Override
        public void UpdateUI(Context context, final int position, Story data) {
            GlideUtils.loadingImage(context, imageView, data.image);
            textView.setText(data.title);
        }
    }

}
