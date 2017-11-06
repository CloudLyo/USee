package com.white.usee.app.util;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.*;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.white.usee.app.R;

/**
 * Created by 10037 on 2016/8/10 0010.
 */

public class DialogUtils  {
    /**
     * 得到自定义的progressDialog
     * @param context
     * @return
     */
    public static Dialog createLoadingDialog(Context context,boolean isCancelOutSide) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.layout_loading_dialog, null);        // 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);  // 加载布局
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        Animation hyperspaceJumpAnimation = android.view.animation.AnimationUtils.loadAnimation(context,R.anim.loading_animation); // 加载动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);             // 使用ImageView显示动画
        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog); // 创建自定义样式dialog
        loadingDialog.setCanceledOnTouchOutside(isCancelOutSide);
        loadingDialog.setCancelable(isCancelOutSide);// 不可以用"返回键"取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        return loadingDialog;
    }
}
