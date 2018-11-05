package com.github.ben.zhihudaily.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ben.zhihudaily.R;
import com.github.ben.zhihudaily.adapter.ThemeAdapter;
import com.github.ben.zhihudaily.data.entity.Story;
import com.github.ben.zhihudaily.network.BenFactory;
import com.github.ben.zhihudaily.ui.App;
import com.github.ben.zhihudaily.ui.module.story.StoryDetailActivity;
import com.github.ben.zhihudaily.ui.base.BaseFragment;
import com.github.ben.zhihudaily.utils.Constant;
import com.github.ben.zhihudaily.utils.GlideUtils;
import com.litesuits.orm.db.model.ConflictAlgorithm;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created on 16/10/13.
 *
 * @author Ben
 */

//该页面没有 MVP (比较用,个人感觉一些简单的页面完全没有必要使用 MVP, 本 Application 使用仅用作个人学习)
public class ThemeFragment extends BaseFragment {

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.theme_imageView)
    ImageView mThemeImageView;
    @BindView(R.id.description_textView)
    TextView mDescriptionTextView;
    @BindView(R.id.theme_stories_recyclerView)
    RecyclerView mThemeRecyclerView;
    @BindView(R.id.theme_image_layout)
    RelativeLayout mThemeImageLayout;

    private ThemeAdapter mThemeAdapter;
    private LinearLayoutManager mThemelinearLayoutManager;
    private String storyThemeId;
    private List<Story> mThemeStories;

    public static final String THEME_ID = "THEME_ID";


    public static ThemeFragment newInstance(String storyThemeId) {
        Bundle bundle = new Bundle();
        bundle.putString(THEME_ID, storyThemeId);
        ThemeFragment fragment = new ThemeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storyThemeId = getArguments().getString(THEME_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_theme, container, false);
        ButterKnife.bind(this, rootView);
        initSwipeRefreshLayout();
        initThemeList();
        getThemeStoryList(storyThemeId);
        return rootView;
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.appbar_bg);
        mSwipeRefreshLayout.setOnRefreshListener(() -> getThemeStoryList(storyThemeId));
    }

    private void initThemeList() {
        mThemelinearLayoutManager = new LinearLayoutManager(mContext);
        mThemeRecyclerView.setLayoutManager(mThemelinearLayoutManager);
        mThemeAdapter = new ThemeAdapter(getActivity());
        mThemeRecyclerView.setAdapter(mThemeAdapter);

        //title image scrolls
        mThemeRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mThemeImageLayout.scrollBy(0, dy);
            }
        });

        mThemeAdapter.setOnStoryItemClickListener(story -> {
            if (!story.isRead) {
                story.isRead = true;
                App.mDb.update(story, ConflictAlgorithm.Replace);
            }
            startActivity(new Intent(getActivity(), StoryDetailActivity.class)
                    .putExtra("id", story.id)
                    .putExtra("themeId", storyThemeId)
                    .putExtra("type", Constant.THEME_STORY));
        });
    }

    @SuppressLint("CheckResult")
    private void getThemeStoryList(String id) {
        BenFactory.getStoryThemeApi()
                .getThemeStories(id)
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(themeStories -> {
                    GlideUtils.loadingImage(getActivity(), mThemeImageView, themeStories.image);
                    mDescriptionTextView.setText(themeStories.description);
                    mThemeStories = themeStories.stories;
                    changeReadState(mThemeStories);
                    mThemeAdapter.setStoriesAndEditors(themeStories.stories, themeStories.editors);
                    mSwipeRefreshLayout.setRefreshing(false);
                }, throwable -> mSwipeRefreshLayout.setRefreshing(false));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mThemeAdapter != null) mThemeAdapter.notifyDataSetChanged();
    }
}
