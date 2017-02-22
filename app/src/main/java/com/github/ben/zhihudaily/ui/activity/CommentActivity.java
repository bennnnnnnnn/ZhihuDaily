package com.github.ben.zhihudaily.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.github.ben.zhihudaily.R;
import com.github.ben.zhihudaily.adapter.CommentAdapter;
import com.github.ben.zhihudaily.data.entity.Comment;
import com.github.ben.zhihudaily.functions.OnCommentClickListener;
import com.github.ben.zhihudaily.functions.OnCommentCountClickListener;
import com.github.ben.zhihudaily.presenter.CommentContract;
import com.github.ben.zhihudaily.presenter.CommentPresenter;
import com.github.ben.zhihudaily.ui.App;
import com.github.ben.zhihudaily.ui.base.BaseActivity;
import com.github.ben.zhihudaily.ui.fragment.CommentDialogFragment;
import com.github.ben.zhihudaily.utils.Constant;
import com.github.ben.zhihudaily.utils.ToastUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created on 16/10/31.
 * @author Ben
 */


public class CommentActivity extends BaseActivity implements CommentContract.View {

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_comment;
    }

    @Bind(R.id.comment_recyclerView)
    RecyclerView mCommentRecyclerView;

    private CommentAdapter mCommentAdapter;
    private String id;
    private int comments;
    private int long_comments;
    private int short_comments;

    private boolean isShowShortComments = false;

    private CommentContract.Presenter mPresenter;
    private CommentPresenter mCommentPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initViews();
        initRecyclerView();
        if (mCommentPresenter == null) mCommentPresenter = new CommentPresenter(this, id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null) mPresenter.start();
    }

    private void initViews() {
        id = getIntent().getStringExtra("id");
        comments = getIntent().getIntExtra(Constant.COMMENTS, 0);
        long_comments = getIntent().getIntExtra(Constant.LONG_COMMENTS, 0);
        short_comments = getIntent().getIntExtra(Constant.SHORT_COMMENTS, 0);
        setTitle(comments + "条点评");
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mCommentRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void initAdapter(List<Comment> comments) {
        if (mCommentAdapter == null) {
            mCommentAdapter = new CommentAdapter(mContext);
            mCommentAdapter.setLongComments(comments, long_comments, short_comments);
            mCommentAdapter.setOnCommentClickListener(getOnCommentClickListener());
            mCommentAdapter.setOnCommentCountClickListener(getOnCommentCountClickListener());
            mCommentRecyclerView.setAdapter(mCommentAdapter);
        }
    }

    private OnCommentClickListener getOnCommentClickListener() {
        return new OnCommentClickListener() {
            @Override
            public void onClick(Comment comment) {
                showDialog(comment);
            }
        };
    }

    private OnCommentCountClickListener getOnCommentCountClickListener() {
        return new OnCommentCountClickListener() {
            @Override
            public void onClick() {
                if (!isShowShortComments) {
                    mPresenter.requestShortCommentsList();
                } else {
                    clearShortComments();
                }
                isShowShortComments = !isShowShortComments;
            }
        };
    }

    @Override
    public void clearShortComments() {
        mCommentAdapter.clearShortComments();
        mCommentRecyclerView.smoothScrollToPosition(0);
    }

    private void smoothShortCommentTitleToTop() {
        int[] location = new int[2];
        System.out.println("-------"+mCommentRecyclerView.getChildCount());
        mCommentRecyclerView.getChildAt(mCommentRecyclerView.getChildCount() - 1).getLocationOnScreen(location);
        mCommentRecyclerView.smoothScrollBy(0, location[1] - App.statusBarHeight - mActionbar.getHeight());
    }

    @Override
    public void showDialog(final Comment comment) {
        CommentDialogFragment.newInstance(comment).show(getSupportFragmentManager(), Constant.COMMENT);
    }

    @Override
    public void refreshLongComments(List<Comment> comments) {
        if (mCommentAdapter == null) {
            initAdapter(comments);
        } else {
            mCommentAdapter.setLongComments(comments, long_comments, short_comments);
        }
    }

    @Override
    public void refreshShortComments(List<Comment> comments) {
        mCommentAdapter.setShortComments(comments);
        smoothShortCommentTitleToTop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_comment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.comment_item) {
            ToastUtils.shortToast(mContext, "亲 想参与评论请使用官方的哇~");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setPresenter(CommentContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
