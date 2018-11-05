package com.github.ben.zhihudaily.ui.module.comment;

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
import com.github.ben.zhihudaily.ui.App;
import com.github.ben.zhihudaily.mvpbase.MVPBaseActivity;
import com.github.ben.zhihudaily.ui.fragment.CommentDialogFragment;
import com.github.ben.zhihudaily.utils.Constant;
import com.github.ben.zhihudaily.utils.ToastUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created on 16/10/31.
 *
 * @author Ben
 */


public class CommentActivity extends MVPBaseActivity<CommentContract.View, CommentPresenter> implements CommentContract.View {

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_comment;
    }

    @BindView(R.id.comment_recyclerView)
    RecyclerView mCommentRecyclerView;

    private CommentAdapter mCommentAdapter;
    private int long_comments;
    private int short_comments;

    private boolean isShowShortComments = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initViews();
        initRecyclerView();
    }

    @Override
    public String getStoryId() {
        return getIntent().getStringExtra("id");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.requestLongComments();
    }

    private void initViews() {
        long_comments = getIntent().getIntExtra(Constant.LONG_COMMENTS, 0);
        short_comments = getIntent().getIntExtra(Constant.SHORT_COMMENTS, 0);
        setTitle(getIntent().getIntExtra(Constant.COMMENTS, 0) + "条点评");
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
        return comment -> showDialog(comment);
    }

    private OnCommentCountClickListener getOnCommentCountClickListener() {
        return () -> {
            if (!isShowShortComments) {
                mPresenter.requestShortCommentsList();
            } else {
                clearShortComments();
            }
            isShowShortComments = !isShowShortComments;
        };
    }

    @Override
    public void clearShortComments() {
        mCommentAdapter.clearShortComments();
        mCommentRecyclerView.smoothScrollToPosition(0);
    }

    private void smoothShortCommentTitleToTop() {
        int[] location = new int[2];
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
}
