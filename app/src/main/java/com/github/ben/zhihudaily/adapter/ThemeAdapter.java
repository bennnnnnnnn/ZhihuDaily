package com.github.ben.zhihudaily.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.ben.zhihudaily.R;
import com.github.ben.zhihudaily.data.entity.Editor;
import com.github.ben.zhihudaily.data.entity.Story;
import com.github.ben.zhihudaily.functions.OnStoryItemClickListener;
import com.github.ben.zhihudaily.ui.App;
import com.github.ben.zhihudaily.utils.GlideUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.model.ConflictAlgorithm;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created on 16/9/22.
 * @author Ben
 */


public class ThemeAdapter extends RecyclerView.Adapter {

    List<Story> stories;
    List<Editor> editors;
    private Context context;
    static final int TYPE_HEAD = 0;
    static final int TYPE_CONTENT = 1;
    private OnStoryItemClickListener onStoryItemClickListener;

    public ThemeAdapter(Context context) {
        this.context = context;
    }

    public void setStoriesAndEditors(List<Story> stories, List<Editor> editors) {
        if (this.stories == null) {
            this.stories = new ArrayList<>();
        }
        this.stories.clear();
        this.stories.addAll(stories);
        this.editors = editors;
        notifyDataSetChanged();
    }

    public void addStories(List<Story> stories) {
        this.stories.addAll(stories);
        notifyDataSetChanged();
    }

    public void setOnStoryItemClickListener(OnStoryItemClickListener onStoryItemClickListener) {
        this.onStoryItemClickListener = onStoryItemClickListener;
    }

    @Override
    public int getItemCount() {
        return stories == null ? 1 : stories.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return TYPE_HEAD;
        return TYPE_CONTENT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEAD) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_item_editor, parent, false);
            return new EditorViewHolder(view);
        } else if (viewType == TYPE_CONTENT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_item_theme_story, parent, false);
            return new ThemeViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        switch (type) {
            case TYPE_HEAD:
                EditorViewHolder editorViewHolder = (EditorViewHolder) holder;
                if (getItemCount() == 1) {
                    editorViewHolder.editosLayout.setVisibility(View.GONE);
                } else {
                    editorViewHolder.editosLayout.setVisibility(View.VISIBLE);
                }
                if (null != editors && editors.size() > 0) {
                    editorViewHolder.editosImage.removeAllViews();
                    int length = (int) (30 * App.screenDensity);
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(length, length);
                    for (Editor editor : editors) {
                        SimpleDraweeView editorImage = new SimpleDraweeView(context);
                        editorImage.setLayoutParams(params);
                        editorImage.setPadding(10, 10, 10, 10);
                        editorImage.setScaleType(ImageView.ScaleType.FIT_XY);
                        GenericDraweeHierarchy hierarchy = GenericDraweeHierarchyBuilder.newInstance(context.getResources())
                                .setRoundingParams(RoundingParams.asCircle()).build();
                        editorImage.setHierarchy(hierarchy);
                        editorImage.setImageURI(editor.avatar);
                        editorViewHolder.editosImage.addView(editorImage);
                    }
                }
                break;
            case TYPE_CONTENT:
                ThemeViewHolder themeViewHolder = (ThemeViewHolder) holder;
                Story story = stories.get(position - 1);
                updateState(story);
                themeViewHolder.mTitleTxtView.setText(story.title);
                if (null != story.images) {
                    GlideUtils.loadingImage(context, themeViewHolder.mImageView, story.images[0]);
                    themeViewHolder.mImageView.setVisibility(View.VISIBLE);
                } else {
                    themeViewHolder.mImageView.setVisibility(View.GONE);
                }
                if (story.isRead) {
                    themeViewHolder.mTitleTxtView.setTextColor(ContextCompat.getColor(context, R.color.textReadColor));
                } else {
                    themeViewHolder.mTitleTxtView.setTextColor(ContextCompat.getColor(context, R.color.textColor));
                }
                break;
            default:
                break;

        }
    }

    private void updateState(Story story) {
        QueryBuilder<Story> qb = new QueryBuilder<>(Story.class)
                .whereEquals(Story.COL_TITLE, story.title)
                .whereAppendAnd()
                .whereEquals(Story.COL_ID, story.id);
        List<Story> stories = App.mDb.query(qb);
        for (Story mStory : stories) {
            if (mStory.isRead) {
                story.isRead = true;
                App.mDb.update(story, ConflictAlgorithm.Replace);
            }
        }
    }

    class EditorViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.editor_layout)
        LinearLayout editosLayout;
        @Bind(R.id.editor_images)
        LinearLayout editosImage;

        public EditorViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class ThemeViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.theme_story_cardView)
        CardView mCardView;
        @Bind(R.id.title_textView)
        TextView mTitleTxtView;
        @Bind(R.id.description_imageView)
        ImageView mImageView;

        public ThemeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.theme_story_cardView)
        void onDetail(View v) {
            Story story = stories.get(getLayoutPosition() - 1);
            if (onStoryItemClickListener != null) {
                onStoryItemClickListener.onClick(story);
            }
        }
    }
}
