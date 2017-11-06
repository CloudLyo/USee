package com.white.usee.app.util;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.white.usee.app.R;
import com.yancy.imageselector.ImageConfig;
import com.yancy.imageselector.ImageSelector;

import java.util.ArrayList;

/**
 * 用来管理多选图片
 * Created by 10037 on 2016/9/22 0022.
 */

public class PhotoUtils {
    /**
     * 跳转到选择图片
     * */
    public static void toPickPhoto(Activity activity){
        ImageConfig imageConfig
                = new ImageConfig.Builder(new GlideLoader())
                .steepToolBarColor(activity.getResources().getColor(R.color.title))
                .titleBgColor(activity.getResources().getColor(R.color.title))
                .titleSubmitTextColor(activity.getResources().getColor(R.color.white))
                .titleTextColor(activity.getResources().getColor(R.color.white))
                // 开启多选   （默认为多选）
                .mutiSelect()
                // 多选时的最大数量   （默认 3 张）
                .mutiSelectMaxSize(3)
                // 开启拍照功能 （默认关闭）
                // 拍照后存放的图片路径（默认 /temp/picture） （会自动创建）
//                .filePath("/ImageSelector/Pictures")
                .build();
        ImageSelector.open(activity,imageConfig);
    }
    /**
     * 跳转到选择图片
     * */
    public static void toPickPhoto(Activity activity, ArrayList path,int maxPhotoNum){
        ImageConfig imageConfig
                = new ImageConfig.Builder(new GlideLoader())
                .steepToolBarColor(activity.getResources().getColor(R.color.title))
                .titleBgColor(activity.getResources().getColor(R.color.title))
                .titleSubmitTextColor(activity.getResources().getColor(R.color.white))
                .titleTextColor(activity.getResources().getColor(R.color.white))
                // 开启多选   （默认为多选）
                .mutiSelect()
                // 多选时的最大数量   （默认 3 张）
                .mutiSelectMaxSize(maxPhotoNum)
                // 开启拍照功能 （默认关闭）
//                .showCamera()
                // 已选择的图片路径
                .pathList(path)
                // 拍照后存放的图片路径（默认 /temp/picture） （会自动创建）
//                .filePath("/ImageSelector/Pictures")
                .build();
        ImageSelector.open(activity,imageConfig);
    }

    /**
     * 添加一个imageview到linearLaoyut中
     * @param linearLayout 容器linearlayout
     * @param path 图片的路径
     * @param onClickListener  图片点击的监听器
     * */
    public static void addPhotoToLinearLayout(Activity activity, LinearLayout linearLayout, String path, View.OnClickListener onClickListener){
        ImageView imageView = new ImageView(activity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(activity.getResources().getDimensionPixelSize(R.dimen.px_one_hundred_ninety_six),activity.getResources().getDimensionPixelSize(R.dimen.px_one_hundred_ninety_six));
        layoutParams.rightMargin = activity.getResources().getDimensionPixelSize(R.dimen.px_thirdty_two);
        imageView.setLayoutParams(layoutParams);
        int padding = activity.getResources().getDimensionPixelSize(R.dimen.px_four);
        imageView.setPadding(padding,padding,padding,padding);
        imageView.setBackgroundResource(R.drawable.add);
        linearLayout.addView(imageView);
        imageView.setOnClickListener(onClickListener);
        Glide.with(activity).load(path).centerCrop().placeholder(R.drawable.default_head).crossFade().into(imageView);
        LogUtil.i("加载图片"+path);
    }
}
