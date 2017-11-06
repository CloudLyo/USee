package com.white.usee.app.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.white.usee.app.R;
import com.yancy.imageselector.ImageLoader;

/**
 * 第三方相册的图片加载器
 * Created by 10037 on 2016/9/22 0022.
 */

public class GlideLoader implements ImageLoader {
    @Override
    public void displayImage(Context context, String path, ImageView imageView) {

        Glide.with(context)
                .load(path)
                .placeholder(com.yancy.imageselector.R.mipmap.imageselector_photo)
                .centerCrop()
                .into(imageView);
    }
}
