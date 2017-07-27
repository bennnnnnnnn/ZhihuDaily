package com.github.ben.zhihudaily.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.ben.zhihudaily.R;
import com.github.ben.zhihudaily.data.entity.Comment;
import com.github.ben.zhihudaily.utils.Clipboard;
import com.github.ben.zhihudaily.utils.Constant;
import com.github.ben.zhihudaily.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created on 17/2/20.
 *
 * @author Ben
 */


public class CommentDialogFragment extends DialogFragment {

    @BindView(R.id.agree_textView)
    TextView agreeTextView;
    @BindView(R.id.report_textView)
    TextView reportTextView;
    @BindView(R.id.copy_textView)
    TextView copyTextView;
    @BindView(R.id.reply_textView)
    TextView replyTextView;

    private Context mContext;
    private Comment mComment;

    public static CommentDialogFragment newInstance(Comment comment) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.COMMENT, comment);
        CommentDialogFragment fragment = new CommentDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mComment = (Comment) getArguments().getSerializable(Constant.COMMENT);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getContext();
        return new AlertDialog.Builder(mContext)
                .setView(onCreateDialogContentView(savedInstanceState))
                .create();
    }

    @SuppressLint("InflateParams")
    private View onCreateDialogContentView(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View root = inflater.inflate(R.layout.dialog_comment, null);
        ButterKnife.bind(this, root);
        return root;
    }

    @OnClick(R.id.agree_textView)
    void onAgree(View v) {
        ToastUtils.shortToast(mContext, "不让点赞 嘿~");
        getDialog().dismiss();
    }

    @OnClick(R.id.report_textView)
    void onReport(View v) {
        ToastUtils.shortToast(mContext, "不让举报 哼~");
        getDialog().dismiss();
    }

    @OnClick(R.id.copy_textView)
    void onCopy(View v) {
        Clipboard.copyMessage(mContext, mComment.content);
        ToastUtils.shortToast(mContext, "已复制该条评论~");
        getDialog().dismiss();
    }

    @OnClick(R.id.reply_textView)
    void onReply(View v) {
        ToastUtils.shortToast(mContext, "想回复我 没门的呢~");
        getDialog().dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("----------onDestroyView", "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("----------onDestroy", "onDestroy");
    }
}
