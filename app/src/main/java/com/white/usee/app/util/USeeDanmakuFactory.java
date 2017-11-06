package com.white.usee.app.util;

import com.white.usee.app.bean.USeeDanmu;
import master.flame.danmaku.danmaku.model.*;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.DanmakuFactory;
import master.flame.danmaku.danmaku.model.android.Danmakus;

/**
 * Created by 10037 on 2016/7/12 0012.
 */
public class USeeDanmakuFactory extends DanmakuFactory{
    public final static float BILI_PLAYER_WIDTH = 682;



    public final static long COMMON_DANMAKU_DURATION = 2500; // B站原始分辨率下弹幕存活时间


    public final static long MIN_DANMAKU_DURATION = 4000;

    public final static long MAX_DANMAKU_DURATION_HIGH_DENSITY = 9000;

    public int CURRENT_DISP_WIDTH = 0, CURRENT_DISP_HEIGHT = 0;

    private float CURRENT_DISP_SIZE_FACTOR = 1.0f;

    public long REAL_DANMAKU_DURATION = COMMON_DANMAKU_DURATION;

    public long MAX_DANMAKU_DURATION = MIN_DANMAKU_DURATION;

    public Duration MAX_Duration_Scroll_Danmaku;

    public Duration MAX_Duration_Fix_Danmaku;

    public Duration MAX_Duration_Special_Danmaku;

    public IDanmakus sSpecialDanmakus = new Danmakus();

    public IDisplayer sLastDisp;
    private DanmakuContext sLastConfig;

    public static USeeDanmakuFactory create() {
        return new USeeDanmakuFactory();
    }

    protected USeeDanmakuFactory() {

    }

    public void resetDurationsData() {
        sLastDisp = null;
        CURRENT_DISP_WIDTH = CURRENT_DISP_HEIGHT = 0;
        sSpecialDanmakus.clear();
        MAX_Duration_Scroll_Danmaku = null;
        MAX_Duration_Fix_Danmaku = null;
        MAX_Duration_Special_Danmaku = null;
        MAX_DANMAKU_DURATION = MIN_DANMAKU_DURATION;
    }


    public USeeDanmu createDanmaku(int line) {
        return createDanmaku( sLastConfig,line);
    }

    public USeeDanmu createDanmaku(DanmakuContext context,int line) {
        if (context == null)
            return null;
        sLastConfig = context;
        sLastDisp = context.getDisplayer();
        return createDanmaku((sLastDisp.getWidth()*1.2f), sLastDisp.getHeight()*1.2f, CURRENT_DISP_SIZE_FACTOR, context.scrollSpeedFactor,line);    }


    /**
     * 创建弹幕数据请尽量使用此方法,参考BiliDanmakuParser或AcfunDanmakuParser
     * @param viewportWidth danmakuview宽度,会影响滚动弹幕的存活时间(duration)
     * @param viewportHeight danmakuview高度
     * @param viewportScale 缩放比例,会影响滚动弹幕的存活时间(duration)
     * @return
     */
    public USeeDanmu createDanmaku( int viewportWidth, int viewportHeight,
                                     float viewportScale, float scrollSpeedFactor,int line) {
        return createDanmaku( (float) viewportWidth, (float) viewportHeight, viewportScale, scrollSpeedFactor,line);
    }

    /**
     * 创建弹幕数据请尽量使用此方法,参考BiliDanmakuParser或AcfunDanmakuParser
     * @param viewportWidth danmakuview宽度,会影响滚动弹幕的存活时间(duration)
     * @param viewportHeight danmakuview高度
     * @param viewportSizeFactor 会影响滚动弹幕的速度/存活时间(duration)
     * @return
     */
    public USeeDanmu createDanmaku(float viewportWidth, float viewportHeight,
                                     float viewportSizeFactor, float scrollSpeedFactor,int line) {
        boolean sizeChanged = updateViewportState(viewportWidth, viewportHeight, viewportSizeFactor);
        if (MAX_Duration_Scroll_Danmaku == null) {
            MAX_Duration_Scroll_Danmaku = new Duration(REAL_DANMAKU_DURATION);
            MAX_Duration_Scroll_Danmaku.setFactor(scrollSpeedFactor);
        } else if (sizeChanged) {
            MAX_Duration_Scroll_Danmaku.setValue(REAL_DANMAKU_DURATION);
        }

        if (MAX_Duration_Fix_Danmaku == null) {
            MAX_Duration_Fix_Danmaku = new Duration(COMMON_DANMAKU_DURATION);
        }

        if (sizeChanged && viewportWidth > 0) {
            updateMaxDanmakuDuration();
        }

        USeeDanmu instance = new USeeDanmu(MAX_Duration_Scroll_Danmaku,line);

        return instance;
    }

    public boolean updateViewportState(float viewportWidth, float viewportHeight,
                                       float viewportSizeFactor) {
        boolean sizeChanged = false;
        if (CURRENT_DISP_WIDTH != (int) viewportWidth
                || CURRENT_DISP_HEIGHT != (int) viewportHeight
                || CURRENT_DISP_SIZE_FACTOR != viewportSizeFactor) {
            sizeChanged = true;
            REAL_DANMAKU_DURATION = (long) (COMMON_DANMAKU_DURATION * (viewportSizeFactor
                    * viewportWidth / BILI_PLAYER_WIDTH));
            REAL_DANMAKU_DURATION = Math.min(MAX_DANMAKU_DURATION_HIGH_DENSITY,
                    REAL_DANMAKU_DURATION);
            REAL_DANMAKU_DURATION = Math.max(MIN_DANMAKU_DURATION, REAL_DANMAKU_DURATION);

            CURRENT_DISP_WIDTH = (int) viewportWidth;
            CURRENT_DISP_HEIGHT = (int) viewportHeight;
            CURRENT_DISP_SIZE_FACTOR = viewportSizeFactor;
        }
        return sizeChanged;
    }


    public void updateMaxDanmakuDuration() {
        long maxScrollDuration = (MAX_Duration_Scroll_Danmaku == null ? 0: MAX_Duration_Scroll_Danmaku.value),
                maxFixDuration = (MAX_Duration_Fix_Danmaku == null ? 0 : MAX_Duration_Fix_Danmaku.value),
                maxSpecialDuration = (MAX_Duration_Special_Danmaku == null ? 0: MAX_Duration_Special_Danmaku.value);

        MAX_DANMAKU_DURATION = Math.max(maxScrollDuration, maxFixDuration);
        MAX_DANMAKU_DURATION = Math.max(MAX_DANMAKU_DURATION, maxSpecialDuration);

        MAX_DANMAKU_DURATION = Math.max(COMMON_DANMAKU_DURATION, MAX_DANMAKU_DURATION);
        MAX_DANMAKU_DURATION = Math.max(REAL_DANMAKU_DURATION, MAX_DANMAKU_DURATION);
    }
    public void updateDurationFactor(float f) {
        if (MAX_Duration_Scroll_Danmaku == null || MAX_Duration_Fix_Danmaku == null)
            return;
        MAX_Duration_Scroll_Danmaku.setFactor(f);
        updateMaxDanmakuDuration();
    }





}
