package com.example.ben.zhihudaily.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ben.zhihudaily.R;
import com.example.ben.zhihudaily.adapter.HomeAdapter;
import com.example.ben.zhihudaily.data.entity.StoriesResult;
import com.example.ben.zhihudaily.data.entity.Story;
import com.example.ben.zhihudaily.network.BenFactory;
import com.example.ben.zhihudaily.ui.base.BaseFragment;
import com.example.ben.zhihudaily.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Zhou bangquan on 16/10/13.
 */


public class HomeFragment extends BaseFragment {

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.home_view)
    RecyclerView mHomeRecyclerView;

    private HomeAdapter mHomeAdapter;
    private List<Story> topStories = new ArrayList<>();
    private List<Story> homeStories = new ArrayList<>();
    private LinearLayoutManager mHomelinearLayoutManager;
    private long time;
    private String today;
    private static long A_DAY_MS = 24L * 60 * 60 * 1000;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_fragment_layout, container, false);
        ButterKnife.bind(this, rootView);
        initSwipeRefreshLayout();
        initHomeList();
        getActivity().setTitle(R.string.home_page);
        getHomeList();
        return rootView;
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.appbar_bg);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getHomeList();
            }
        });
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
                        mSwipeRefreshLayout.setRefreshing(true);
                        time = time - A_DAY_MS;
                        addHomeList(DateUtils.msToDate(time));
                    }
                }
                int position = mHomelinearLayoutManager.findFirstVisibleItemPosition();
                if (position == 0) {
                    getActivity().setTitle(R.string.home_page);
                } else {
                    String date = homeStories.get(position - 1).date;
                    if (today.equals(date)) {
                        getActivity().setTitle(R.string.today_news);
                    } else {
                        getActivity().setTitle(date);
                    }
                }
            }
        });
    }

    private void getHomeList() {
        unsubscribe();
        subscription = BenFactory.getDailyNewsApi()
                .getDailyNews("latest")
                .map(new Func1<StoriesResult, List<Story>>() {
                    @Override
                    public List<Story> call(StoriesResult storiesResult) {
                        if (storiesResult != null) {
                            topStories = storiesResult.top_stories;
                            today = DateUtils.dateWithWeekday(System.currentTimeMillis());
                            time = System.currentTimeMillis() + A_DAY_MS;
                            for (Story daily : topStories) {
                                daily.date = DateUtils.dateWithWeekday(time - A_DAY_MS);
                                daily.before = DateUtils.msToDate(time);
                            }
                            return storiesResult.stories;
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Story>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Story> stories) {
                        for (Story story : stories) {
                            story.date = DateUtils.dateWithWeekday(time - A_DAY_MS);
                            story.before = DateUtils.msToDate(time);
                        }
                        homeStories = stories;
                        mHomeAdapter.setDailyNews(stories, topStories);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void addHomeList(String beforeTime) {
        unsubscribe();
        subscription = BenFactory.getDailyNewsApi()
                .getBeforeDailyNews(beforeTime)
                .map(new Func1<StoriesResult, List<Story>>() {
                    @Override
                    public List<Story> call(StoriesResult storiesResult) {
                        if (storiesResult != null) {
                            return storiesResult.stories;
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Story>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Story> stories) {
                        for (Story story : stories) {
                            story.date = DateUtils.dateWithWeekday(time - A_DAY_MS);
                            story.before = DateUtils.msToDate(time);
                        }
                        homeStories.addAll(stories);
                        mHomeAdapter.addDailyNews(stories);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

}
