package com.example.ben.zhihudaily.ui;

import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.ben.zhihudaily.R;
import com.example.ben.zhihudaily.adapter.HomeAdapter;
import com.example.ben.zhihudaily.adapter.SideAdapter;
import com.example.ben.zhihudaily.data.entity.DailyNews;
import com.example.ben.zhihudaily.data.entity.DailyTheme;
import com.example.ben.zhihudaily.data.entity.DailyThemeResult;
import com.example.ben.zhihudaily.data.entity.SingleDaily;
import com.example.ben.zhihudaily.network.BenFactory;
import com.example.ben.zhihudaily.ui.base.BaseActivity;
import com.example.ben.zhihudaily.ui.base.StableToolBarActivity;
import com.example.ben.zhihudaily.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends StableToolBarActivity {

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_main;
    }

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.news_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.side_listview)
    ListView mListView;

    private ActionBarDrawerToggle mDrawerToggle;
    private SideAdapter mSideAdapter;
    private HomeAdapter mHomeAdapter;
    private List<SingleDaily> topStories = new ArrayList<>();
    private List<SingleDaily> dailies = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private Subscription mSideSub;
    private long time;
    private String today;
    private static long A_DAY_MS = 24L * 60 * 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setTitle(R.string.home_page);

        initDrawerLayout();
        initSideList();
        initHomeList();

        getSideList();
        getHomeList();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (null != mDrawerToggle) mDrawerToggle.syncState();
    }

    private void initDrawerLayout() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {

                if (mDrawerToggle != null) {
                    mDrawerToggle.onDrawerOpened(drawerView);
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {

                if (mDrawerToggle != null) {
                    mDrawerToggle.onDrawerClosed(drawerView);
                }
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                if (mDrawerToggle != null) {
                    mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
                }

            }

            @Override
            public void onDrawerStateChanged(int newState) {

                if (mDrawerToggle != null) {
                    mDrawerToggle.onDrawerStateChanged(newState);
                }
            }
        });
    }

    private void showOrCloseSideView() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            showOrCloseSideView();
        } else if (id == R.id.action_settings) {

        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initHomeList() {
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mHomeAdapter = new HomeAdapter(this);
        mRecyclerView.setAdapter(mHomeAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getHomeList();
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                boolean is = linearLayoutManager.findLastCompletelyVisibleItemPosition() >= mHomeAdapter.getItemCount() - 1;
                if (!mSwipeRefreshLayout.isRefreshing() && is) {
                    if (mHomeAdapter.getItemCount() - 1 > 0) {
                        mSwipeRefreshLayout.setRefreshing(true);
                        time = time - A_DAY_MS;
                        addHomeList(DateUtils.msToDate(time));
                    }
                }

                int position = linearLayoutManager.findFirstVisibleItemPosition();
                if (position == 0) {
                    setTitle(R.string.home_page);
                } else {
                    String date = dailies.get(position - 1).date;
                    if (today.equals(date)) {
                        setTitle(R.string.today_news);
                    } else {
                        setTitle(date);
                    }
                }
            }
        });
    }

    private void initSideList() {
        View headView = getLayoutInflater().inflate(R.layout.side_headerview, mListView, false);
        mListView.addHeaderView(headView);
        mSideAdapter = new SideAdapter(this, mDrawerLayout);
        mListView.setAdapter(mSideAdapter);
    }

    private void getHomeList() {
        unsubscribe();
        subscription = BenFactory.getDailyNewsApi()
                .getDailyNews("latest")
                .map(new Func1<DailyNews, List<SingleDaily>>() {
                    @Override
                    public List<SingleDaily> call(DailyNews dailyNews) {
                        if (dailyNews != null) {
                            topStories = dailyNews.top_stories;
                            today = DateUtils.dateWithWeekday(System.currentTimeMillis());
                            time = System.currentTimeMillis() + A_DAY_MS;
                            for (SingleDaily daily : topStories) {
                                daily.date = DateUtils.dateWithWeekday(time - A_DAY_MS);
                                daily.before = DateUtils.msToDate(time);
                            }
                            return dailyNews.stories;
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<SingleDaily>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<SingleDaily> singleDailies) {
                        for (SingleDaily daily : singleDailies) {
                            daily.date = DateUtils.dateWithWeekday(time - A_DAY_MS);
                            daily.before = DateUtils.msToDate(time);
                        }
                        dailies = singleDailies;
                        mHomeAdapter.setDailyNews(singleDailies, topStories);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void addHomeList(String beforeTime) {
        unsubscribe();
        subscription = BenFactory.getDailyNewsApi()
                .getBeforeDailyNews(beforeTime)
                .map(new Func1<DailyNews, List<SingleDaily>>() {
                    @Override
                    public List<SingleDaily> call(DailyNews dailyNews) {
                        if (dailyNews != null) {
                            return dailyNews.stories;
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<SingleDaily>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<SingleDaily> singleDailies) {
                        for (SingleDaily daily : singleDailies) {
                            daily.date = DateUtils.dateWithWeekday(time - A_DAY_MS);
                            daily.before = DateUtils.msToDate(time);
                        }
                        dailies.addAll(singleDailies);
                        mHomeAdapter.addDailyNews(singleDailies);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void getSideList() {
        mSideSub = BenFactory.getDailyThemeApi()
                .getDailyThemes()
                .map(new Func1<DailyThemeResult, List<DailyTheme>>() {
                    @Override
                    public List<DailyTheme> call(DailyThemeResult dailyThemeResult) {
                        if (null != dailyThemeResult) {
                            return dailyThemeResult.others;
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<DailyTheme>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<DailyTheme> dailyThemes) {
                        mSideAdapter.setDailyThemes(dailyThemes);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mSideSub && mSideSub.isUnsubscribed()) mSideSub.unsubscribe();
    }
}