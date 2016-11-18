package com.example.ben.zhihudaily.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.ben.zhihudaily.R;
import com.example.ben.zhihudaily.adapter.CommentAdapter;
import com.example.ben.zhihudaily.data.entity.Comment;
import com.example.ben.zhihudaily.data.entity.CommentsResult;
import com.example.ben.zhihudaily.functions.OnCommentClickListener;
import com.example.ben.zhihudaily.functions.OnCommentCountClickListener;
import com.example.ben.zhihudaily.network.BenFactory;
import com.example.ben.zhihudaily.ui.App;
import com.example.ben.zhihudaily.ui.base.StableToolBarActivity;
import com.example.ben.zhihudaily.utils.Constant;
import com.example.ben.zhihudaily.utils.ToastUtils;
import com.example.ben.zhihudaily.views.TipDialog;
import com.example.ben.zhihudaily.views.TipDialogInterface;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Zhou bangquan on 16/10/31.
 */


public class CommentActivity extends StableToolBarActivity {

    @Override
    protected int provideContentViewId() {
        return R.layout.layout_comment_activity;
    }

    @Bind(R.id.comment_recyclerView)
    RecyclerView mCommentRecyclerView;

    private CommentAdapter mCommentAdapter;
    private String id;
    private int comments;
    private int long_comments;
    private int short_comments;

    private boolean isShowShortComments = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initViews();
        initRecyclerView();
        requestLongCommentsList();
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
        if (mCommentAdapter == null ) {
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
            public void onClick() {
                showDialog();
            }
        };
    }

    private OnCommentCountClickListener getOnCommentCountClickListener() {
        return new OnCommentCountClickListener() {
            @Override
            public void onClick() {
                if (!isShowShortComments) {
                    requestShortCommentsList();
                } else {
                    mCommentAdapter.clearShortComments();
                    mCommentRecyclerView.smoothScrollToPosition(0);
                }
                isShowShortComments = !isShowShortComments;
            }
        };
    }

    private void requestLongCommentsList() {
        unsubscribe();
        subscription = BenFactory.getStoryApi()
                .getLongComments(id)
                .map(new Func1<CommentsResult, List<Comment>>() {
                    @Override
                    public List<Comment> call(CommentsResult commentsResult) {
                        if (commentsResult != null) {
                            return commentsResult.comments;
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Comment>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(List<Comment> comments) {
                        if (mCommentAdapter == null) {
                            initAdapter(comments);
                        } else {
                            mCommentAdapter.setLongComments(comments, long_comments, short_comments);
                        }
                    }
                });
    }

    //前 20 条
    private void requestShortCommentsList() {
        unsubscribe();
        subscription = BenFactory.getStoryApi()
                .getShortComments(id)
                .map(new Func1<CommentsResult, List<Comment>>() {
                    @Override
                    public List<Comment> call(CommentsResult commentsResult) {
                        if (commentsResult != null) {
                            return commentsResult.comments;
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Comment>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Comment> comments) {
                        mCommentAdapter.setShortComments(comments);
                        smoothShortCommentTitleToTop();
                    }
                });
    }

    private void smoothShortCommentTitleToTop() {
        int[] location = new int[2];
        mCommentRecyclerView.getChildAt(mCommentRecyclerView.getChildCount() - 1).getLocationOnScreen(location);
        mCommentRecyclerView.smoothScrollBy(0, location[1] - App.statusBarHeight - mActionbar.getHeight());
    }

    private void showDialog() {
        TipDialog tipDialog = new TipDialog(mContext, R.layout.comment_dialog_layout, new TipDialogInterface() {
            @Override
            public void init(@NonNull final TipDialog tipDialog, Window window) {
                TextView agreeTextView = (TextView) window.findViewById(R.id.agree_textView);
                TextView reportTextView = (TextView) window.findViewById(R.id.report_textView);
                TextView copyTextView = (TextView) window.findViewById(R.id.copy_textView);
                TextView replyTextView = (TextView) window.findViewById(R.id.reply_textView);

                agreeTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                reportTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                copyTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText(Constant.COPY, "1234,2234");
                        clipboardManager.setPrimaryClip(clipData);
                        tipDialog.cancel();
                    }
                });

                replyTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtils.shortToast(mContext, "想回复我 没门~");
                        tipDialog.cancel();
                    }
                });

            }
        });
        tipDialog.showDialog();
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
