package com.github.ben.zhihudaily.ui;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.github.ben.zhihudaily.R;
import com.github.ben.zhihudaily.adapter.SideAdapter;
import com.github.ben.zhihudaily.data.entity.StoryTheme;
import com.github.ben.zhihudaily.data.entity.StoryThemeResult;
import com.github.ben.zhihudaily.network.BenFactory;
import com.github.ben.zhihudaily.ui.base.ToolBarActivity;
import com.github.ben.zhihudaily.ui.module.home.HomeFragment;
import com.github.ben.zhihudaily.ui.fragment.ThemeFragment;
import com.github.ben.zhihudaily.utils.SharePreUtils;
import com.github.ben.zhihudaily.utils.ToastUtils;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.model.ConflictAlgorithm;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created on 16/10/8.
 *
 * @author Ben
 */

public class MainActivity extends ToolBarActivity {

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_main;
    }

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.side_listview)
    ListView mListView;

    private ActionBarDrawerToggle mDrawerToggle;
    private SideAdapter mSideAdapter;
    private List<StoryTheme> storyThemes = new ArrayList<>();
    private List<StoryTheme> requestStoryThemes = new ArrayList<>();
    private Subscription mSideSub;
    private String storyThemeId;
    private String storyThemeName;
    private Fragment currentFragment;
    private HomeFragment homeFragment;
    private ThemeFragment themeFragment;
    private boolean isHomePage;
    private MenuItem item0;
    private MenuItem item1;
    private MenuItem item2;
    private MenuItem item3;
    private MenuItem item4;
    private StoryTheme selectedTheme;
    private boolean request;
    private boolean updateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initDrawerLayout();
        showHomePage(true);
        initSideList();
        getSideList();
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
                    afterDrawerOpened();
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                if (mDrawerToggle != null) {
                    mDrawerToggle.onDrawerClosed(drawerView);
                    afterDrawerClosed();
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

    private void afterDrawerOpened() {
        if (updateList) {
            reorderAndSaveThemes(requestStoryThemes);
            if (mSideAdapter != null) {
                mSideAdapter.notifyDataSetChanged();
            }
            updateList = false;
        }
    }

    private void afterDrawerClosed() {
        if (request) {
            setTitle(storyThemeName);
            mSideAdapter.notifyDataSetChanged();
            showHomePage(isHomePage);
            request = false;
        }
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
        item0 = menu.findItem(R.id.remind_item);
        item1 = menu.findItem(R.id.follow_item);
        item2 = menu.findItem(R.id.mode_item);
        item3 = menu.findItem(R.id.setting_item);
        item4 = menu.findItem(R.id.about_item);
        item2.setTitle(isNightMode() ? "日间模式" : "夜间模式");
        changeMenuItems(isHomePage);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mode_item) {
            if (isNightMode()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            SharePreUtils.put(App.NIGHT_MODE, !isNightMode());
            recreate();
            return true;
        } else if (id == R.id.setting_item) {
            return true;
        } else if (id == R.id.follow_item) {
            boolean follow = selectedTheme.followed;
            if (follow) {
                ToastUtils.shortToast(mContext, "卧槽,竟敢取关我!");
            } else {
                ToastUtils.shortToast(mContext, "哇哦,谢谢大大的关注!");
            }
            selectedTheme.followed = !follow;
            setFollowIcon(selectedTheme);
            App.mDb.update(selectedTheme, ConflictAlgorithm.Replace);
            updateList = true;
            return true;
        } else if (id == R.id.about_item) {
            startActivity(new Intent(mContext, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initSideList() {
        View headView = getLayoutInflater().inflate(R.layout.side_headerview, mListView, false);
        mListView.addHeaderView(headView);
        mSideAdapter = new SideAdapter(this);
        mListView.setAdapter(mSideAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // headView homeItem takes two position
                if (position > 1) {
                    selectedTheme = storyThemes.get(position - 2);
                    selectedTheme.selected = true;
                    storyThemeId = selectedTheme.id;
                    storyThemeName = selectedTheme.name;
                    for (int i = 0; i < storyThemes.size(); i++) {
                        if (i != position - 2) {
                            storyThemes.get(i).selected = false;
                        }
                    }
                    isHomePage = false;
                    request = true;
                } else if (position == 1) {
                    storyThemeName = "首页";
                    isHomePage = true;
                    request = true;
                    selectedTheme.selected = false;
                }
                showOrCloseSideView();
            }
        });
        mListView.getLayoutParams().width = (int) (App.screenWidth * 0.8);
    }

    private void showHomePage(boolean showHomePage) {
        isHomePage = showHomePage;
        changeMenuItems(showHomePage);
        if (showHomePage) {
            homeFragment = HomeFragment.newInstance();
            currentFragment = homeFragment;
        } else {
            themeFragment = ThemeFragment.newInstance(storyThemeId);
            currentFragment = themeFragment;
        }
        if (null == mSideAdapter) {
            mSideAdapter = new SideAdapter(this);
        }
        mSideAdapter.isHomePage = showHomePage;
        getSupportFragmentManager().beginTransaction().replace(R.id.container_layout, currentFragment).commit();
    }

    private void changeMenuItems(boolean isHomePage) {
        if (null != item0 && null != item1 && null != item2 && null != item3) {
            if (isHomePage) {
                item0.setVisible(true);
                item1.setVisible(false);
                item2.setVisible(true);
                item3.setVisible(true);
                item4.setVisible(true);
            } else {
                setFollowIcon(selectedTheme);
                item0.setVisible(false);
                item1.setVisible(true);
                item2.setVisible(false);
                item3.setVisible(false);
                item4.setVisible(false);
            }
        }
    }

    private void setFollowIcon(StoryTheme followedTheme) {
        item1.setIcon(followedTheme.followed ? R.drawable.minus_icon : R.drawable.plus_icon);
    }

    private void getSideList() {
        mSideSub = BenFactory.getStoryThemeApi()
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
                .doOnNext(new Action1<List<StoryTheme>>() {
                    @Override
                    public void call(List<StoryTheme> themes) {
                        requestStoryThemes = themes;
                        reorderAndSaveThemes(requestStoryThemes);
                        mSideAdapter.isHomePage = true;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<StoryTheme>>() {
                    @Override
                    public void call(List<StoryTheme> themes) {
                        mSideAdapter.setDailyThemes(storyThemes, themes);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    private void reorderAndSaveThemes(List<StoryTheme> themes) {
        if (storyThemes != null) {
            storyThemes.clear();
            storyThemes.addAll(themes);
        }
        int size = themes.size();
        int amount = 0;
        for (int i = size - 1; i > -1; i--) {
            StoryTheme theme = themes.get(i);
            QueryBuilder<StoryTheme> qb = new QueryBuilder<>(StoryTheme.class)
                    .whereEquals(StoryTheme.COL_NAME, theme.name)
                    .whereAppendAnd()
                    .whereEquals(StoryTheme.COL_ID, theme.id)
                    .whereAppendAnd()
                    .whereEquals(StoryTheme.COL_FOLLOWED, true);
            if (App.mDb.query(qb) != null && App.mDb.query(qb).size() > 0) {
                theme.followed = true;
                storyThemes.remove(i + (amount++));
                storyThemes.add(0, theme);
            } else {
                theme.followed = false;
            }
            App.mDb.save(theme);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mSideSub && mSideSub.isUnsubscribed()) mSideSub.unsubscribe();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (isHomePage) {
                android.os.Process.killProcess(android.os.Process.myPid());
            } else {
                showHomePage(true);
            }
        }
    }
}
