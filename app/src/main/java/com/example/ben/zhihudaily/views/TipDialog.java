package com.example.ben.zhihudaily.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.example.ben.zhihudaily.ui.App;


/**
 * Created by Zhou bangquan on 16/11/4.
 */


public class TipDialog extends AlertDialog implements View.OnClickListener{

    private int layoutResId;
    private Context context;
    private TipDialogInterface tipDialogInterface;

    public TipDialog(@NonNull Context context, int layoutResId, TipDialogInterface tipDialogInterface) {
        super(context);
        this.context = context;
        this.layoutResId = layoutResId;
        this.tipDialogInterface = tipDialogInterface;
    }

    public void showDialog() {
        this.setView(getLayoutInflater().inflate(layoutResId, null));
        this.show();
        this.setCanceledOnTouchOutside(true);
        Window window = this.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
//            window.setGravity(Gravity.CENTER);
//            lp.height = (ViewGroup.LayoutParams.WRAP_CONTENT); // 设置高度
//            lp.width = (ViewGroup.LayoutParams.MATCH_PARENT);
//            window.setAttributes(lp);
            window.setContentView(layoutResId);
            if (tipDialogInterface != null) {
                tipDialogInterface.init(this, window);
            }
        }
    }

    @Override
    public void onClick(View v) {

    }
}
