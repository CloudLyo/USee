package com.white.usee.app.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;

import java.lang.ref.WeakReference;

/**
 * 图文混排使图片文字基于中线对齐
 * Created by feiyang on 16/2/18.
 * 参考：http://stackoverflow.com/questions/25628258/align-text-around-imagespan-center-vertical
 */
public class CenteredImageSpan extends ImageSpan {

    // Extra variables used to redefine the Font Metrics when an ImageSpan is added

    public CenteredImageSpan(final Drawable drawable) {
        this(drawable, DynamicDrawableSpan.ALIGN_BOTTOM);
    }

    public CenteredImageSpan(final Drawable drawable, final int verticalAlignment) {
        super(drawable, verticalAlignment);
    }



    public int getSize(Paint paint, CharSequence text, int start, int end,
                       Paint.FontMetricsInt fm) {
        Drawable d = getDrawable();
        Rect rect = d.getBounds();
        if (fm != null) {
            Paint.FontMetricsInt fmPaint=paint.getFontMetricsInt();
            int fontHeight = fmPaint.bottom - fmPaint.top;
            int drHeight=rect.bottom-rect.top;

            int top= drHeight/2 - fontHeight/4;
            int bottom=drHeight/2 + fontHeight/4;

            fm.ascent=-bottom;
            fm.top=-bottom;
            fm.bottom=top;
            fm.descent=top;
        }
        return rect.right;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint) {
        Drawable b = getDrawable();
        canvas.save();
        int transY = 0;
        transY = ((bottom-top) - b.getBounds().bottom)/2+top;
        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
    }

}