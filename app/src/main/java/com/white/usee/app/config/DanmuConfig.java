package com.white.usee.app.config;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.WindowManager;

import com.white.usee.app.BaseActivity;
import com.white.usee.app.BaseApplication;
import com.white.usee.app.util.BackgroudCacheStuffer;
import com.white.usee.app.util.Dp2Px;

import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.android.AcFunDanmakuParser;

import java.util.HashMap;
import java.util.Random;

/**
 * 设置弹幕的基本属性，长宽密度等
 * Created by white on 15-10-27.
 */
public class DanmuConfig {
    public static int maxLine = 11;//最大行数
    public static boolean isShowFps = false;//是否显示fps数据
    public static boolean isCache = false;//弹幕是否缓存
    public static float mSpeed = 1.1f;//滚动速度
    public static float mHPISpeed = 0.5f; //高分辨率的手机速度两倍（如2k手机）
    public static float textScale = 1f;//字体缩放大小
    public static int textColor = Color.BLACK;//字体颜色
    public static float textSize = Dp2Px.dip2px(BaseApplication.getInstance(), 16f);//字体大小
    public static int maxDanmuNumber = 0;//0为无限制
    public static int danmuSpeedSeekbarProgress = 50;//弹幕数度的控制条的进度
    public static float danmuSpeed = 1f;

    //设置弹幕view的基本属性
    public static void setDanmakuViewConfig(final IDanmakuView danmakuView, float speed, BackgroudCacheStuffer backgroudCacheStuffer) {
        //弹幕回收缓存池
        BaseCacheStuffer.Proxy mCacheStufferAdapter = new BaseCacheStuffer.Proxy() {

            @Override
            public void prepareDrawing(final BaseDanmaku danmaku, boolean fromWorkerThread) {
            }

            @Override
            public void releaseResource(BaseDanmaku danmaku) {
                if (danmaku.text instanceof Spanned) {
                    danmaku.text = "";
                }
                // TODO 重要:清理含有ImageSpan的text中的一些占用内存的资源 例如drawable
            }
        };
        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, DanmuConfig.maxLine); // 滚动弹幕最大显示行数
        // 设置是否禁止重叠

        DanmakuContext mContext = DanmakuContext.create();

        mContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_NONE, null)
                .setDuplicateMergingEnabled(false)
                .setCacheStuffer(backgroudCacheStuffer, mCacheStufferAdapter)  // 绘制背景使用BackgroundCacheStuffer
                .setMaximumLines(maxLinesPair)
                .setScaleTextSize(DanmuConfig.textScale)
                .setScrollSpeedFactor(speed)//设置为全局速度参数
                .setMaximumVisibleSizeInScreen(maxDanmuNumber)
                .setR2LDanmakuVisibility(true)

        ;
        if (danmakuView != null) {
            ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_ACFUN);
            BaseDanmakuParser mParser = new AcFunDanmakuParser();
            mParser.load(loader.getDataSource());
//            danmakuView.getView().setLayerType(View.LAYER_TYPE_SOFTWARE,null);
            danmakuView.showFPS(DanmuConfig.isShowFps);
            danmakuView.enableDanmakuDrawingCache(DanmuConfig.isCache);
            danmakuView.prepare(mParser, mContext);
        }
    }


}
