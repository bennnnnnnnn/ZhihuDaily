package com.example.ben.zhihudaily.utils;

import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ben.zhihudaily.R;

/**
 * Created by Zhou bangquan on 16/9/19.
 */


public class DetailDailyActionProvider extends ActionProvider {

    ImageView imageView;
    TextView textView;
    Context context;
    DetailDailyActionProvider.OnClickListener listener;

    public DetailDailyActionProvider(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public View onCreateActionView() {
        int size = getContext().getResources().getDimensionPixelSize(
                android.support.design.R.dimen.abc_action_bar_default_height_material);

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(size, size);
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.collection_layout, null, false);
        view.setLayoutParams(layoutParams);
        imageView = (ImageView) view.findViewById(R.id.commend_image);
        textView = (TextView) view.findViewById(R.id.commend_number);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onClick();
                }
            }
        });
        return view;
    }

    public void setNum(String num) {
        textView.setText(num);
    }

    public void setImageResource(int ResInt) {
        imageView.setImageResource(ResInt);
    }

    public void setOnClickListener(DetailDailyActionProvider.OnClickListener listener) {
        this.listener = listener;
    }

    public interface OnClickListener {
        void onClick();
    }

}
