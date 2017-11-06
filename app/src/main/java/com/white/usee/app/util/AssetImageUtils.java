package com.white.usee.app.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.animation.*;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.signature.StringSignature;
import com.white.usee.app.BaseApplication;
import com.white.usee.app.R;
import com.white.usee.app.config.HttpUrlConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

/**
 * Created by 10037 on 2016/8/3 0003.
 */

public class AssetImageUtils {
    public static Drawable getHeadImageFromAssetsFile(Context context, String fileName) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open("head/"+fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            image =  BitmapFactory.decodeResource(context.getResources(),R.drawable.default_head);
        }
        return new BitmapDrawable(image);
    }

    /**
     * 加载用户匿名头像 格式为44_FFFFFF
     * 先将其区分为44和FFFF，然后获取44.png，绘制成ffffff色的BitmapDrawable
     * */
    public static Drawable getHeadImageByColor(Context context,String fileName,int width,int height){
        if (fileName.equals("0.png")){
            return getHeadImageFromAssetsFile(context,fileName);
        }
        Bitmap image = null;
        try {
            String [] iconAndColor = fileName.split("_");
            String iconString = iconAndColor[0]+".png";
            int color = Color.parseColor("#"+iconAndColor[1]);
            AssetManager am = context.getResources().getAssets();
            InputStream is = am.open("head/"+iconString);
            image = BitmapFactory.decodeStream(is);
            is.close();
            Bitmap mutableBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(context.getResources().getColor(R.color.icon_bg));
            Canvas canvas = new Canvas(mutableBitmap);
            float radius = width/2;
            canvas.drawCircle(radius,radius,radius,paint);
            Matrix matrix = new Matrix();
            int scaleBord = Math.max(image.getWidth(),image.getHeight());
            float scaleFactor = (height*0.5f/scaleBord);
            matrix.postScale(scaleFactor,scaleFactor);
            Bitmap secondBitmap = Bitmap.createBitmap(image,0,0,image.getWidth(),image.getHeight(),matrix,true);
            for (int i=0;i<secondBitmap.getHeight();i++){
                for (int j=0;j<secondBitmap.getWidth();j++){
                    int pixcolor = secondBitmap.getPixel(j,i);
                    if (pixcolor == Color.TRANSPARENT){
                    }else {
                        secondBitmap.setPixel(j,i,color);
                    }

                }
            }
            canvas.drawBitmap(secondBitmap,radius-secondBitmap.getWidth()/2,radius-secondBitmap.getHeight()/2,paint);

            image = mutableBitmap;
        }
        catch (Exception e) {
            image =  BitmapFactory.decodeResource(context.getResources(),R.drawable.default_head);
            LogUtil.i("加载本地随机头像失败"+e.getMessage());
        }
        return new BitmapDrawable(image);
    }

    public static Drawable getDanmuHead(Context context ,String fileName,int radius){
        if (fileName==null||fileName.isEmpty()){
           return context.getResources().getDrawable(R.drawable.default_head);
        }
        if (fileName.length()>0&&fileName.length()<15){
            return getHeadImageByColor(context,fileName,radius,radius);
        }else if (fileName.length()>15){
            final Bitmap[] usericonBit = new Bitmap[1];
            try {
                Glide.with(context).load(HttpUrlConfig.iconUrl+ fileName).asBitmap().into(new SimpleTarget<Bitmap>(radius,radius) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        usericonBit[0] = resource;
                    }
                });
            } catch (Exception  e) {
                e.printStackTrace();
                //--------------
                usericonBit[0] = BitmapFactory.decodeResource(context.getResources(),R.drawable.default_head);
            }
            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(BaseApplication.getInstance().getResources(), usericonBit[0]);
            roundedBitmapDrawable.setCircular(true);
            return roundedBitmapDrawable;
        } else {
            return context.getResources().getDrawable(R.drawable.default_head);
        }
    }
    public static void loadUserHeadByNoCache(Context context, String userIcon, final ImageView iv_user_head){
        if (userIcon==null||userIcon.isEmpty()||userIcon.equals("0.png")) {
            iv_user_head.setImageDrawable(context.getResources().getDrawable(R.drawable.default_head));
        }else if (userIcon.length()>15){
            Glide.with(context).load(HttpUrlConfig.iconUrl+ userIcon).asBitmap().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(new BitmapImageViewTarget(iv_user_head){
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(BaseApplication.getInstance().getResources(),resource);
                    roundedBitmapDrawable.setCircular(true);
                    iv_user_head.setImageDrawable(roundedBitmapDrawable);
                }
            });
        }else if (userIcon.length()>0&&userIcon.length()<15){
            iv_user_head.setImageDrawable(getHeadImageByColor(context,userIcon,iv_user_head.getWidth(),iv_user_head.getHeight()));
        }
    }

    /**
     * 加载用户头像，会更新url中是否含有png或jpg为标准，如果不包含，说明为匿名头像，调用getHeadImageByColor绘制匿名头像，如果有，使用Glide加载
     * @param context
     * @param userIcon 用户头像名，若是匿名头像则是22_FFFFFF,若是实名头像则是****.png
     * @param iv_user_head
     *
     * */
    public static void loadUserHead(Context context, String userIcon, final ImageView iv_user_head){
        loadUserHead(context,userIcon,iv_user_head,iv_user_head.getWidth(),iv_user_head.getHeight());
    }
    public static void loadUserHead(final Context context, String userIcon, final ImageView iv_user_head, int width, int height){
        if (userIcon==null) {
            iv_user_head.setImageDrawable(context.getResources().getDrawable(R.drawable.default_head));
            return;
        }
        if (userIcon.isEmpty()||userIcon.equals("0.png")) {
            iv_user_head.setImageDrawable(context.getResources().getDrawable(R.drawable.default_head));
        }else if (userIcon.contains(".png")||userIcon.contains(".jpg")){
            Glide.with(context).load(HttpUrlConfig.iconUrl+ userIcon).asBitmap().dontAnimate().error(R.drawable.default_head).placeholder(R.drawable.default_head).signature(new StringSignature(BaseApplication.getInstance().getHeadsignature())).into(new BitmapImageViewTarget(iv_user_head){
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(BaseApplication.getInstance().getResources(),resource);
                    roundedBitmapDrawable.setCircular(true);
                    iv_user_head.setImageDrawable(roundedBitmapDrawable);
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    iv_user_head.setImageDrawable(context.getResources().getDrawable(R.drawable.default_head));
                }
            });
        }else if (userIcon.length()>0&&userIcon.length()<15){
            iv_user_head.setImageDrawable(getHeadImageByColor(context,userIcon,width==0?Dp2Px.px_24:width,height==0?Dp2Px.px_24:height));
        }
    }
}
