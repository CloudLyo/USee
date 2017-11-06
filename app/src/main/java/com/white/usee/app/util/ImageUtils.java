package com.white.usee.app.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 图片工具，主要用于用户上传头像时候剪切头像缓存到本地
 * Created by 10037 on 2016/7/29 0029.
 */

public class ImageUtils {

    public static int TYPE_JPEG = 1000;
    public static int TYPE_PNG = 1001;

    public static File saveBitmap2file(Context context, String path, String filename) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(path, opts);
        Bitmap.CompressFormat format = Bitmap.CompressFormat.PNG;
        int quality = 100;
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(context.getCacheDir() + filename);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (bmp != null) {
            bmp.compress(format, quality, stream);
            return new File(context.getCacheDir() + filename);

        } else {
            return null;
        }


    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri)
            return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri,
                    new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;

    }

    public static File bitmap2Path(Context context, Bitmap tempBitmap) {
        if (tempBitmap == null) {
            return null;
        }
        File tempFile = new File(context.getCacheDir(), "temp.png");
        if (tempFile.exists()) {
            tempFile.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(tempFile);
            tempBitmap.compress(Bitmap.CompressFormat.PNG, 75, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return tempFile;
    }

    /**
     * 压缩图片，便于加载到内存
     *
     * @param path
     * @return
     */
    //手机拍照的图片一般都是几兆，如果直接加载到内存，容易造成OOM,所以需要压缩图片大小
    //这里的代码的从网上找的算法
    public static File compressBitmap(Activity context, String path) {
        //压缩图片
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);

        float hh = dm.heightPixels;
        float ww = dm.widthPixels;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, opts);
        opts.inJustDecodeBounds = false;
        int w = opts.outWidth;
        int h = opts.outHeight;
        int size = 0;
        if (w <= ww && h <= hh) {
            size = 1;
        } else {
            double scale = w >= h ? w / ww : h / hh;
            double log = Math.log(scale) / Math.log(2);
            double logCeil = Math.ceil(log);
            size = (int) Math.pow(2, logCeil);
        }
        opts.inSampleSize = size;
        bitmap = BitmapFactory.decodeFile(path, opts);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(Bitmap.CompressFormat.PNG, quality, baos);
        while (baos.toByteArray().length > 45 * 1024) {
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, baos);
            quality -= 30;
        }
        try {
            baos.writeTo(new FileOutputStream(path));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                baos.flush();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new File(path);
    }

    /**
     * 从相册中选择的图片拷贝到自己的文件夹
     *
     * @param srcPath
     * @param desPath
     */
    public static void copyImage(String srcPath, String desPath) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(srcPath);
            fos = new FileOutputStream(desPath);
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭流
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    //等待垃圾回收器回收
                    fis = null;
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    fos = null;
                }
            }
        }
    }

    public static Bitmap showBitmap(Activity context, String srcPath) {
        try {
            File srcFile = new File(srcPath);
            if (!srcFile.exists()) {
                return null;
            }
            //相当消耗内存资源 根据图片的分辨率而定
            //1.得到屏幕的宽高信息
            WindowManager wm = context.getWindowManager();
            int screenWidth = wm.getDefaultDisplay().getWidth();
            int screenHeight = wm.getDefaultDisplay().getHeight();

            //2.得到图片的宽高
            BitmapFactory.Options opts = new BitmapFactory.Options();//解析位图的附加条件
            opts.inJustDecodeBounds = true;//不去解析真实的位图，只是获取这个位图的头文件信息
            Bitmap bitmap = BitmapFactory.decodeFile(srcPath, opts);
            int bitmapWidth = opts.outWidth;
            int bitmapHeight = opts.outHeight;

            //3.计算缩放比例
            int dx = bitmapWidth / screenWidth;
            int dy = bitmapHeight / screenHeight;
            int scale = 1;
            if (dx > dy && dy > 1) {
                scale = dx;
            }
            if (dy > dx && dx > 1) {
                scale = dy;
            }
            //4.缩放加载图片到内存。
            opts.inSampleSize = scale;
            opts.inJustDecodeBounds = false;//真正的去解析这个位图。
            bitmap = BitmapFactory.decodeFile(srcPath, opts);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File pickImgFromAlbum(Activity context, Intent data, String desPath) {
        Bitmap bm = null;
        //外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
        ContentResolver resolver = context.getContentResolver();
        //此处的用于判断接收的Activity是不是你想要的那个
        Uri originalUri;
        try {
            originalUri = data.getData(); //获得图片的uri
        } catch (Exception e) {
            //用户没有选择图片
            e.printStackTrace();
            return null;
        }
        try {
            bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);//显得到bitmap图片
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.managedQuery(originalUri, proj, null, null, null);
            //这个是获得用户选择的图片的索引值
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            //将光标移至开头
            cursor.moveToFirst();
            //最后根据索引值获取选择的图片的路径
            String srcPath = cursor.getString(column_index);
            //将该图片拷贝一份到缓存文件夹，同时压缩图片
            File file = copyImage(srcPath, context, desPath);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static File copyImage(String srcPath, Activity context, String desPath) {
        ImageUtils.copyImage(srcPath, desPath);
        return ImageUtils.compressBitmap(context, desPath);
    }

    public static Bitmap compressImage(Bitmap image, int type) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (type == TYPE_JPEG) {
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            int options = 100;
            while (baos.toByteArray().length / 1024 > 100) {
                baos.reset();
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);
                options -= 10;
            }
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
            Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
            return bitmap;
        } else if (type == TYPE_PNG) {
            image.compress(Bitmap.CompressFormat.PNG, 100, baos);
            int options = 100;
            while (baos.toByteArray().length / 1024 > 100) {
                baos.reset();
                image.compress(Bitmap.CompressFormat.PNG, options, baos);
                options -= 10;
            }
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
            Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
            return bitmap;
        } else {
            return null;
        }
    }

    public static File compressImage2(Activity context, String path, String outputPath) {
        //压缩图片
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);

        float hh = dm.heightPixels;
        float ww = dm.widthPixels;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, opts);
        opts.inJustDecodeBounds = false;
        int w = opts.outWidth;
        int h = opts.outHeight;
        int size = 0;
        if (w <= ww && h <= hh) {
            size = 1;
        } else {
            double scale = w >= h ? w / ww : h / hh;
            double log = Math.log(scale) / Math.log(2);
            double logCeil = Math.ceil(log);
            size = (int) Math.pow(2, logCeil);
        }
        opts.inSampleSize = size;
        bitmap = BitmapFactory.decodeFile(path, opts);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(Bitmap.CompressFormat.PNG, quality, baos);
        while (baos.toByteArray().length > 45 * 1024) {
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, baos);
            quality -= 30;
        }
        try {
            baos.writeTo(new FileOutputStream(outputPath));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                baos.flush();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new File(outputPath);
    }

    //压缩图片尺寸
    public static Bitmap compressBySize(String pathName, int targetWidth,
                                 int targetHeight) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;// 不去真的解析图片，只是获取图片的头部信息，包含宽高等；
        Bitmap bitmap = BitmapFactory.decodeFile(pathName, opts);
        // 得到图片的宽度、高度；
        float imgWidth = opts.outWidth;
        float imgHeight = opts.outHeight;
        // 分别计算图片宽度、高度与目标宽度、高度的比例；取大于等于该比例的最小整数；
        int widthRatio = (int) Math.ceil(imgWidth / (float) targetWidth);
        int heightRatio = (int) Math.ceil(imgHeight / (float) targetHeight);
        opts.inSampleSize = 1;
        if (widthRatio > 1 || widthRatio > 1) {
            if (widthRatio > heightRatio) {
                opts.inSampleSize = widthRatio;
            } else {
                opts.inSampleSize = heightRatio;
            }
        }
        //设置好缩放比例后，加载图片进内容；
        opts.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(pathName, opts);
        return bitmap;
    }

    //存储进SD卡
    public static  void saveFile(Bitmap bm, String fileName) throws Exception {
        File dirFile = new File(fileName);
        //检测图片是否存在
        if(dirFile.exists()){
            dirFile.delete();  //删除原图片
        }
        File myCaptureFile = new File(fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        //100表示不进行压缩，70表示压缩率为30%
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
    }

}










