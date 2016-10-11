package com.example.ben.zhihudaily.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ben.zhihudaily.R;
import com.example.ben.zhihudaily.adapter.HomeAdapter;
import com.example.ben.zhihudaily.adapter.SideAdapter;
import com.example.ben.zhihudaily.adapter.ThemeAdapter;
import com.example.ben.zhihudaily.data.ResponseError;
import com.example.ben.zhihudaily.data.entity.StoriesResult;
import com.example.ben.zhihudaily.data.entity.StoryTheme;
import com.example.ben.zhihudaily.data.entity.StoryThemeResult;
import com.example.ben.zhihudaily.data.entity.Story;
import com.example.ben.zhihudaily.data.entity.ThemeStories;
import com.example.ben.zhihudaily.network.BenFactory;
import com.example.ben.zhihudaily.ui.base.StableToolBarActivity;
import com.example.ben.zhihudaily.utils.DateUtils;
import com.example.ben.zhihudaily.utils.GlideUtils;
import com.example.ben.zhihudaily.utils.SharePreUtils;
import com.litesuits.orm.db.assit.QueryBuilder;

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
    @Bind(R.id.home_view)
    RecyclerView mHomeRecyclerView;
    @Bind(R.id.side_listview)
    ListView mListView;
    @Bind(R.id.theme_layout)
    RelativeLayout mThemeLayout;
    @Bind(R.id.theme_imageView)
    ImageView mThemeImageView;
    @Bind(R.id.description_textView)
    TextView mDescriptionTextView;
    @Bind(R.id.theme_stories_recyclerView)
    RecyclerView mThemeRecyclerView;
    @Bind(R.id.theme_image_layout)
    RelativeLayout mThemeImageLayout;

    private ActionBarDrawerToggle mDrawerToggle;
    private SideAdapter mSideAdapter;
    private HomeAdapter mHomeAdapter;
    private ThemeAdapter mThemeAdapter;
    private List<Story> topStories = new ArrayList<>();
    private List<Story> dailies = new ArrayList<>();
    private List<StoryTheme> storyThemes = new ArrayList<>();
    private LinearLayoutManager mHomelinearLayoutManager;
    private LinearLayoutManager mThemelinearLayoutManager;
    private Subscription mSideSub;
    private long time;
    private String today;
    private String storyThemeId;
    private String storyThemeName;
    private static long A_DAY_MS = 24L * 60 * 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setTitle(R.string.home_page);

        initDrawerLayout();
        initSwipeRefreshLayout();
        initSideList();
        initHomeList();
        initThemeList();

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
        MenuItem item0 = menu.findItem(R.id.collecion_item);
        MenuItem item1 = menu.findItem(R.id.favourate_item);
        MenuItem item2 = menu.findItem(R.id.mode_item);
        MenuItem item3 = menu.findItem(R.id.setting_item);
        if (isNightMode()) {
            item2.setTitle("日间模式");
        } else {
            item2.setTitle("夜间模式");
        }
        if (isHomePageVisible()) {
            item0.setVisible(true);
            item1.setVisible(false);
            item2.setVisible(true);
            item3.setVisible(true);
        } else {
            item0.setVisible(false);
            item1.setVisible(true);
            item2.setVisible(false);
            item3.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            showOrCloseSideView();
            return true;
        } else if (id == R.id.mode_item) {
            if (isNightMode()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            SharePreUtils.put(mContext, App.ZHIHU_MODE, !isNightMode());
            recreate();
            return true;
        } else if (id == R.id.setting_item) {
            return true;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return super.onOptionsItemSelected(item);
    }

    //init Homepage
    private void initHomeList() {
        mHomelinearLayoutManager = new LinearLayoutManager(this);
        mHomeRecyclerView.setLayoutManager(mHomelinearLayoutManager);
        mHomeAdapter = new HomeAdapter(this);
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

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isHomePageVisible()) {
                    getHomeList();
                } else {
                    getThemeStoryList(storyThemeId);
                }
            }
        });
    }

    private void initSideList() {
        View headView = getLayoutInflater().inflate(R.layout.side_headerview, mListView, false);
        mListView.addHeaderView(headView);
        mSideAdapter = new SideAdapter(this);
        mListView.setAdapter(mSideAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showOrCloseSideView();
                if (position > 1) {
                    StoryTheme s = storyThemes.get(position - 2);
                    storyThemeId = s.id;
                    storyThemeName = s.name;
                    showHomePage(false);
                } else if (position == 1) {
                    storyThemeName = "首页";
                    showHomePage(true);
                }
                setTitle(storyThemeName);
                mSideAdapter.notifyDataSetChanged();
            }
        });

        mListView.getLayoutParams().width = (int) (App.screenWidth * 0.8);
    }


    private void showHomePage(boolean showHomePage) {
        if (showHomePage) {
            mSideAdapter.isHomePage = true;
            getHomeList();
            mThemeLayout.setVisibility(View.GONE);
            mHomeRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mSideAdapter.isHomePage = false;
            getThemeStoryList(storyThemeId);
            mThemeLayout.setVisibility(View.VISIBLE);
            mHomeRecyclerView.setVisibility(View.GONE);
        }
    }

    private boolean isHomePageVisible() {
        return mHomeRecyclerView.getVisibility() == View.VISIBLE;
    }

    private void initThemeList() {
        mThemelinearLayoutManager = new LinearLayoutManager(this);
        mThemeRecyclerView.setLayoutManager(mThemelinearLayoutManager);
        mThemeAdapter = new ThemeAdapter(this);
        mThemeRecyclerView.setAdapter(mThemeAdapter);

        //title image scrolls
        mThemeRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mThemeImageLayout.scrollBy(0, dy);
            }
        });
    }

    //theme type content
    private void getThemeStoryList(String id) {
        unsubscribe();
        subscription = BenFactory.getDailyThemeApi()
                .getThemeStories(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ThemeStories>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        new ResponseError(e).handle(new ResponseError.OnResult() {
                            @Override
                            public void onResult(ResponseError error) {
                                Toast.makeText(mContext, error.error_msg, Toast.LENGTH_SHORT).show();
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }

                    @Override
                    public void onNext(ThemeStories themeStories) {
                        GlideUtils.loadingImage(mContext, mThemeImageView, themeStories.image);
                        mDescriptionTextView.setText(themeStories.description);
                        mThemeAdapter.setStoriesAndEditors(themeStories.stories, themeStories.editors);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void getHomeList() {
        unsubscribe();
        subscription = BenFactory.getDailyNewsApi()
                .getDailyNews("latest")
                .map(new Func1<StoriesResult, List<Story>>() {
                    @Override
                    public List<Story> call(StoriesResult dailyNews) {
                        if (dailyNews != null) {
                            topStories = dailyNews.top_stories;
                            today = DateUtils.dateWithWeekday(System.currentTimeMillis());
                            time = System.currentTimeMillis() + A_DAY_MS;
                            for (Story daily : topStories) {
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
                .subscribe(new Observer<List<Story>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Story> singleDailies) {
                        for (Story daily : singleDailies) {
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
                .map(new Func1<StoriesResult, List<Story>>() {
                    @Override
                    public List<Story> call(StoriesResult dailyNews) {
                        if (dailyNews != null) {
                            return dailyNews.stories;
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
                    public void onNext(List<Story> singleDailies) {
                        for (Story daily : singleDailies) {
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
                .map(new Func1<StoryThemeResult, List<StoryTheme>>() {
                    @Override
                    public List<StoryTheme> call(StoryThemeResult dailyThemeResult) {
                        if (null != dailyThemeResult) {
                            return dailyThemeResult.others;
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<StoryTheme>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<StoryTheme> dailyThemes) {
                        storyThemes = dailyThemes;
                        for (StoryTheme theme : dailyThemes) {
                            QueryBuilder<StoryTheme> qb = new QueryBuilder<>(StoryTheme.class)
                                    .whereEquals(StoryTheme.COL_NAME, theme.name);
                            wheatherSelected(theme, App.mDb.query(qb));
                        }
                        mSideAdapter.isHomePage = true;
                        mSideAdapter.setDailyThemes(dailyThemes);
                    }
                });
    }

    private void wheatherSelected(StoryTheme theme, List<StoryTheme> stortThemes) {
        for (StoryTheme stortTheme : stortThemes) {
            if (stortTheme.selected) {
                theme.selected = true;
                return;
            }
        }
        theme.selected = false;
        App.mDb.save(theme);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mSideSub && mSideSub.isUnsubscribed()) mSideSub.unsubscribe();
    }
}
