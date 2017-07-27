package com.github.ben.zhihudaily.ui.module.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ben.zhihudaily.R;
import com.github.ben.zhihudaily.adapter.HomeAdapter;
import com.github.ben.zhihudaily.data.entity.Story;
import com.github.ben.zhihudaily.functions.OnBannerItemClickListener;
import com.github.ben.zhihudaily.functions.OnStoryItemClickListener;
import com.github.ben.zhihudaily.ui.App;
import com.github.ben.zhihudaily.ui.module.story.StoryDetailActivity;
import com.github.ben.zhihudaily.mvpbase.MVPBaseFragment;
import com.github.ben.zhihudaily.utils.Constant;
import com.litesuits.orm.db.model.ConflictAlgorithm;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created on 16/10/13.
 *
 * @author Ben
 */


public class HomeFragment extends MVPBaseFragment<HomeContract.View, HomePresenter> implements HomeContract.View {

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.home_view)
    RecyclerView mHomeRecyclerView;

    private HomeAdapter mHomeAdapter;
    private LinearLayoutManager mHomelinearLayoutManager;

    private boolean initialized;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, rootView);
        initSwipeRefreshLayout();
        initHomeList();
        setTitle(R.string.home_page);
        return rootView;
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.appbar_bg);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.refreshHomeList();
            }
        });
    }

    @Override
    public void lataestStoriesLoaded(List<Story> stories, List<Story> topStories) {
        mHomeAdapter.setDailyNews(stories, topStories);
    }

    @Override
    public void beforeStoriesLoaded(List<Story> stories) {
        mHomeAdapter.addDailyNews(stories);
    }

    @Override
    public void isSwipeRefreshing(boolean state) {
        mSwipeRefreshLayout.setRefreshing(state);
    }

    @Override
    public void setTitle(CharSequence title) {
        getActivity().setTitle(title);
    }

    @Override
    public void setTitle(int titleId) {
        getActivity().setTitle(titleId);
    }

    private void initHomeList() {
        mHomelinearLayoutManager = new LinearLayoutManager(mContext);
        mHomeRecyclerView.setLayoutManager(mHomelinearLayoutManager);
        mHomeAdapter = new HomeAdapter(getActivity());
        mHomeRecyclerView.setAdapter(mHomeAdapter);

        mHomeRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                boolean is = mHomelinearLayoutManager.findLastCompletelyVisibleItemPosition() >= mHomeAdapter.getItemCount() - 1;
                if (!mSwipeRefreshLayout.isRefreshing() && is) {
                    if (mHomeAdapter.getItemCount() - 1 > 0) {
                        isSwipeRefreshing(true);
                        mPresenter.loadBeforeStories();
                    }
                }
                int position = mHomelinearLayoutManager.findFirstVisibleItemPosition();
                mPresenter.setCurrentTile(position);
            }
        });

        mHomeAdapter.setOnStoryItemClickListener(new OnStoryItemClickListener() {
            @Override
            public void onClick(Story story) {
                if (!story.isRead) {
                    story.isRead = true;
                    App.mDb.update(story, ConflictAlgorithm.Replace);
                }
                startActivity(new Intent(getActivity(), StoryDetailActivity.class)
                        .putExtra("id", story.id)
                        .putExtra("before", story.before)
                        .putExtra("type", Constant.STORY));
            }
        });

        mHomeAdapter.setOnBannerItemClickListener(new OnBannerItemClickListener() {
            @Override
            public void onClick(Story story) {
                if (!story.isRead) {
                    story.isRead = true;
                    App.mDb.update(story, ConflictAlgorithm.Replace);
                }
                startActivity(new Intent(getActivity(), StoryDetailActivity.class)
                        .putExtra("id", story.id)
                        .putExtra("before", story.before)
                        .putExtra("type", Constant.TOP_STORIES));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!initialized) {
            mPresenter.getHomeList();
            initialized = true;
        } else {
            if (mHomeAdapter != null) mHomeAdapter.notifyDataSetChanged();
        }
    }
}
