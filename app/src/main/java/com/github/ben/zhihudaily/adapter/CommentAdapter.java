package com.github.ben.zhihudaily.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ben.zhihudaily.R;
import com.github.ben.zhihudaily.data.entity.Comment;
import com.github.ben.zhihudaily.functions.OnCommentClickListener;
import com.github.ben.zhihudaily.functions.OnCommentCountClickListener;
import com.github.ben.zhihudaily.ui.App;
import com.github.ben.zhihudaily.utils.DateUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created on 16/11/4.
 * @author Ben
 */


public class CommentAdapter extends RecyclerView.Adapter {
    private Context context;
    private int long_comments;
    private int short_comments;
    private List<Comment> longComments = new ArrayList<>();
    private List<Comment> shortComments = new ArrayList<>();
    static final int TYPE_LONG_COUNT = 0;
    static final int TYPE_LONG_COMMENT = 1;
    static final int TYPE_SHORT_COUNT = 2;
    static final int TYPE_SHORT_COMMENT = 3;
    private OnCommentClickListener onCommentClickListener;
    private OnCommentCountClickListener onCommentCountClickListener;

    public CommentAdapter(Context context) {
        this.context = context;
    }

    public void setLongComments(List<Comment> longComments, int long_comments, int short_comments) {
        this.longComments = longComments;
        this.long_comments = long_comments;
        this.short_comments = short_comments;
        notifyDataSetChanged();
    }

    public void setShortComments(List<Comment> shortComments) {
        this.shortComments = shortComments;
        notifyDataSetChanged();
    }

    public void clearShortComments() {
        this.shortComments.clear();
        notifyDataSetChanged();
    }

    public void setOnCommentClickListener(OnCommentClickListener onCommentClickListener) {
        this.onCommentClickListener = onCommentClickListener;
    }

    public void setOnCommentCountClickListener(OnCommentCountClickListener onCommentCountClickListener) {
        this.onCommentCountClickListener = onCommentCountClickListener;
    }

    @Override
    public int getItemCount() {
        return longComments.size() + shortComments.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_LONG_COUNT;
        } else if (position > 0 && position < longComments.size() + 1) {
            return TYPE_LONG_COMMENT;
        } else if (position == longComments.size() + 1) {
            return TYPE_SHORT_COUNT;
        } else {
            return TYPE_SHORT_COMMENT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_LONG_COUNT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_long_comment_count, parent, false);
            return new LongCountViewHolder(view);
        } else if (viewType == TYPE_LONG_COMMENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_comment, parent, false);
            return new CommentViewHolder(view);
        } else if (viewType == TYPE_SHORT_COUNT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_short_comment_count, parent, false);
            return new ShortCountViewHolder(view);
        } else if (viewType == TYPE_SHORT_COMMENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_comment, parent, false);
            return new CommentViewHolder(view);
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        GenericDraweeHierarchy hierarchy = GenericDraweeHierarchyBuilder.newInstance(context.getResources())
                .setRoundingParams(RoundingParams.asCircle()).build();
        switch (type) {
            case TYPE_LONG_COUNT:
                LongCountViewHolder longCountViewHolder = (LongCountViewHolder) holder;

                longCountViewHolder.mCommentCountTextView.setText("" + long_comments + "条长评");

                longCountViewHolder.mEmptyLayout.getLayoutParams().height
                        = (int) (App.screenHeight - App.statusBarHeight - longCountViewHolder.mCommentCountTextView.getLayoutParams().height * 2 - 50 * App.screenDensity);
                longCountViewHolder.mEmptyLayout.setVisibility(longComments != null && longComments.size() > 0 ? View.GONE : View.VISIBLE);
                break;
            case TYPE_LONG_COMMENT:
                CommentViewHolder longCommentViewHolder = (CommentViewHolder) holder;
                Comment longComment = longComments.get(position - 1);
                longCommentViewHolder.mComment = longComment;
                longCommentViewHolder.mAuthorDraweeView.setHierarchy(hierarchy);
                longCommentViewHolder.mAuthorDraweeView.setImageURI(longComment.avatar);

                longCommentViewHolder.mAuthorTextView.setText(longComment.author);
                longCommentViewHolder.mPraiseNumber.setText(longComment.likes);
                longCommentViewHolder.mCommentContent.setText(longComment.content);
                longCommentViewHolder.mCommentTime.setText(DateUtils.msToSecond(longComment.time * 1000));

                if (longComment.isPraised) {
                    longCommentViewHolder.mPraiseIcon.setImageResource(R.drawable.praise_yes_icon);
                } else {
                    longCommentViewHolder.mPraiseIcon.setImageResource(R.drawable.praise_no_icon);
                }
                break;
            case TYPE_SHORT_COUNT:
                ShortCountViewHolder shortCountViewHolder = (ShortCountViewHolder) holder;
                shortCountViewHolder.mCommentCountTextView.setText("" + short_comments + "条短评");
                if (shortComments.size() > 0) {
                    shortCountViewHolder.mBottimLine.setVisibility(View.GONE);
                    shortCountViewHolder.mShowButton.setSelected(true);
                } else {
                    shortCountViewHolder.mBottimLine.setVisibility(View.VISIBLE);
                    shortCountViewHolder.mShowButton.setSelected(false);
                }
                break;
            case TYPE_SHORT_COMMENT:
                CommentViewHolder shortCommentViewHolder = (CommentViewHolder) holder;
                Comment shortComment = shortComments.get(position - longComments.size() - 2);
                shortCommentViewHolder.mComment = shortComment;
                shortCommentViewHolder.mAuthorDraweeView.setHierarchy(hierarchy);
                shortCommentViewHolder.mAuthorDraweeView.setImageURI(shortComment.avatar);

                shortCommentViewHolder.mAuthorTextView.setText(shortComment.author);
                shortCommentViewHolder.mPraiseNumber.setText(shortComment.likes);
                shortCommentViewHolder.mCommentContent.setText(shortComment.content);
                shortCommentViewHolder.mCommentTime.setText(DateUtils.msToSecond(shortComment.time * 1000));

                if (shortComment.isPraised) {
                    shortCommentViewHolder.mPraiseIcon.setImageResource(R.drawable.praise_yes_icon);
                } else {
                    shortCommentViewHolder.mPraiseIcon.setImageResource(R.drawable.praise_no_icon);
                }
                break;
            default:
                break;
        }
    }

    class LongCountViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.comment_count_textView)
        TextView mCommentCountTextView;
        @BindView(R.id.empty_layout)
        LinearLayout mEmptyLayout;

        public LongCountViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class ShortCountViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.comment_count_textView)
        TextView mCommentCountTextView;
        @BindView(R.id.short_comment_button)
        ImageButton mShowButton;
        @BindView(R.id.short_comment_layout)
        LinearLayout mShowLayout;
        @BindView(R.id.bottom_line_view)
        RelativeLayout mBottimLine;

        public ShortCountViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.short_comment_layout)
        void onClick(View v) {
            if (onCommentCountClickListener != null) {
                onCommentCountClickListener.onClick();
            }
        }

        @OnClick(R.id.short_comment_button)
        void onButtonClick(View v) {
            if (onCommentCountClickListener != null) {
                onCommentCountClickListener.onClick();
            }
        }
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.comment_cardView)
        CardView mCommentCardView;
        @BindView(R.id.author_image)
        SimpleDraweeView mAuthorDraweeView;
        @BindView(R.id.author_name_textView)
        TextView mAuthorTextView;
        @BindView(R.id.praise_icon)
        ImageView mPraiseIcon;
        @BindView(R.id.praise_number)
        TextView mPraiseNumber;
        @BindView(R.id.comment_content_textview)
        TextView mCommentContent;
        @BindView(R.id.comment_time_textView)
        TextView mCommentTime;
        Comment mComment;

        public CommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.comment_cardView)
        void onItemClick(View v) {
            if (onCommentClickListener != null) {
                onCommentClickListener.onClick(mComment);
            }
        }
    }
}
