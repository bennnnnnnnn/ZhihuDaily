package com.github.ben.zhihudaily.adapter;

import android.content.Context;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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
import com.github.ben.zhihudaily.R;
import com.github.ben.zhihudaily.data.entity.Story;

import com.github.ben.zhihudaily.functions.OnBannerItemClickListener;
import com.github.ben.zhihudaily.functions.OnStoryItemClickListener;
import com.github.ben.zhihudaily.ui.App;
import com.github.ben.zhihudaily.utils.GlideUtils;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.model.ConflictAlgorithm;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created on 16/9/11.
 *
 * @author Ben
 */

public class HomeAdapter extends RecyclerView.Adapter {
    private List<Story> mDailyNews;
    private List<Story> mBannerThemes;
    private Context context;
    private static final int TYPE_HEAD = 0;
    private static final int TYPE_CONTENT = 1;
    private boolean initialized = false;
    private OnStoryItemClickListener onStoryItemClickListener;
    private OnBannerItemClickListener onBannerItemClickListener;

    public HomeAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEAD) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_item_header_banner, parent, false);
            return new HeadViewHolder(view);
        } else if (viewType == TYPE_CONTENT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_item_homestory, parent, false);
            return new HomeViewHolder(view);
        }
        return null;
    }

    public void setDailyNews(List<Story> mDailyNews, List<Story> mBannerThemes) {
        if (this.mDailyNews == null) {
            this.mDailyNews = new ArrayList<>();
        }
        this.mDailyNews.clear();
        this.mDailyNews.addAll(mDailyNews);
        this.mBannerThemes = mBannerThemes;
        notifyDataSetChanged();
    }

    public void addDailyNews(List<Story> mDailyNews) {
        this.mDailyNews.addAll(mDailyNews);
        notifyDataSetChanged();
    }

    public void setOnStoryItemClickListener(OnStoryItemClickListener onStoryItemClickListener) {
        this.onStoryItemClickListener = onStoryItemClickListener;
    }

    public void setOnBannerItemClickListener(OnBannerItemClickListener onBannerItemClickListener) {
        this.onBannerItemClickListener = onBannerItemClickListener;
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
                headViewHolder.mBannerViewPager.setOnItemClickListener(position1 -> {
                    Story story = mBannerThemes.get(position1);
                    if (onBannerItemClickListener != null) {
                        onBannerItemClickListener.onClick(story);
                    }
                });
                break;
            case TYPE_CONTENT:
                HomeViewHolder homeViewHolder = (HomeViewHolder) holder;
                final Story story = mDailyNews.get(position - 1);
                updateState(story);
                homeViewHolder.story = story;
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
                    homeViewHolder.titleTextView.setTextColor(ContextCompat.getColor(context, R.color.textReadColor));
                } else {
                    homeViewHolder.titleTextView.setTextColor(ContextCompat.getColor(context, R.color.textColor));
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

    private void updateState(Story story) {
        QueryBuilder<Story> qb = new QueryBuilder<>(Story.class)
                .whereEquals(Story.COL_TITLE, story.title)
                .whereAppendAnd()
                .whereEquals(Story.COL_ID, story.id);
        List<Story> stories = App.mDb.query(qb);
        for (Story mStory : stories) {
            if (mStory.isRead) {
                story.isRead = true;
                App.mDb.update(story, ConflictAlgorithm.Replace);
            }
        }
    }

    class HomeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_cardview)
        CardView mCardView;
        @BindView(R.id.news_image)
        ImageView newsImageView;
        @BindView(R.id.news_title)
        TextView titleTextView;
        @BindView(R.id.date_textView)
        TextView dateTextView;
        @BindView(R.id.multip_layout)
        RelativeLayout multipLayout;

        Story story;

        HomeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.item_cardview)
        void onDetail(View v) {
            if (onStoryItemClickListener != null) {
                onStoryItemClickListener.onClick(story);
            }
        }
    }

    class HeadViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.banner_viewpager)
        ConvenientBanner<Story> mBannerViewPager;

        HeadViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private void initConvenientBanner(HeadViewHolder headViewHolder, List<Story> singleDailies) {
        headViewHolder.mBannerViewPager.setPages(
                (CBViewHolderCreator<LocalImageHolderView>) LocalImageHolderView::new, singleDailies)
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
            imageView = view.findViewById(R.id.banner_image);
            textView = view.findViewById(R.id.banner_title);
            return view;
        }

        @Override
        public void UpdateUI(Context context, final int position, Story data) {
            GlideUtils.loadingImage(context, imageView, data.image);
            textView.setText(data.title);
        }
    }

}
