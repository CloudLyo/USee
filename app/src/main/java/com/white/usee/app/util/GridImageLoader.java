package com.white.usee.app.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lzy.ninegrid.NineGridView;
import com.white.usee.app.R;

/**
 * Created by 10037 on 2016/9/25 0025.
 */

public class GridImageLoader implements NineGridView.ImageLoader {
    @Override
    public void onDisplayImage(Context context, ImageView imageView, String url) {
        int padding = context.getResources().getDimensionPixelSize(R.dimen.px_four);
        imageView.setPadding(padding,padding,padding,padding);
        imageView.setBackgroundResource(R.drawable.add);
        Glide.with(context).load(url)
                .placeholder(com.yancy.imageselector.R.mipmap.imageselector_photo)
                .fitCenter()
                .crossFade()
                .into(imageView);
    }

    @Override
    public Bitmap getCacheImage(String url) {
        return null;
    }
}
