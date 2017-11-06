package com.white.usee.app.view;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.white.usee.app.util.Dp2Px;

public class WrapLineLayout extends ViewGroup {

	private int VIEW_MARGIN = 16;
	private int VIEW_MARGIN_WIDTH = 8;
	private int VIEW_MARGIN_HEIGHT = 16;
	private int rows=0;
	private ImageButton ib_controll;
	private boolean isHide = false;

	public boolean isHide() {
		return isHide;
	}

	public void setHide(boolean hide) {
		isHide = hide;
	}

	public ImageButton getIb_controll() {
		return ib_controll;
	}

	public void setIb_controll(ImageButton ib_controll) {
		this.ib_controll = ib_controll;
	}

	public WrapLineLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (isInEditMode())return;
		VIEW_MARGIN = Dp2Px.dip2px(context, VIEW_MARGIN);
		VIEW_MARGIN_WIDTH = Dp2Px.dip2px(context,VIEW_MARGIN_WIDTH);
		VIEW_MARGIN_HEIGHT = Dp2Px.dip2px(context,VIEW_MARGIN_HEIGHT);
	}

	public WrapLineLayout(Context context) {
		super(context);
		if (isInEditMode())return;
		VIEW_MARGIN = Dp2Px.dip2px(context, VIEW_MARGIN);
		VIEW_MARGIN_WIDTH = Dp2Px.dip2px(context,VIEW_MARGIN_WIDTH);
		VIEW_MARGIN_HEIGHT = Dp2Px.dip2px(context,VIEW_MARGIN_HEIGHT);
	}


	private int measureWidth(int pWidthMeasureSpec) {
		int result = 0;
		int widthMode = MeasureSpec.getMode(pWidthMeasureSpec);// 得到模式
		int widthSize = MeasureSpec.getSize(pWidthMeasureSpec);// 得到尺寸

		switch (widthMode) {
			/**
			 * mode共有三种情况，取值分别为MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY,
			 * MeasureSpec.AT_MOST。
			 *
			 *
			 * MeasureSpec.EXACTLY是精确尺寸，
			 * 当我们将控件的layout_width或layout_height指定为具体数值时如andorid
			 * :layout_width="50dip"，或者为FILL_PARENT是，都是控件大小已经确定的情况，都是精确尺寸。
			 *
			 *
			 * MeasureSpec.AT_MOST是最大尺寸，
			 * 当控件的layout_width或layout_height指定为WRAP_CONTENT时
			 * ，控件大小一般随着控件的子空间或内容进行变化，此时控件尺寸只要不超过父控件允许的最大尺寸即可
			 * 。因此，此时的mode是AT_MOST，size给出了父控件允许的最大尺寸。
			 *
			 *
			 * MeasureSpec.UNSPECIFIED是未指定尺寸，这种情况不多，一般都是父控件是AdapterView，
			 * 通过measure方法传入的模式。
			 */
			case MeasureSpec.AT_MOST:
			case MeasureSpec.EXACTLY:
				result = widthSize;
				break;
		}
		return result;
	}

	private int measureHeight(int pHeightMeasureSpec) {
		int result = 0;

		int heightMode = MeasureSpec.getMode(pHeightMeasureSpec);
		int heightSize = MeasureSpec.getSize(pHeightMeasureSpec);

		switch (heightMode) {
			case MeasureSpec.AT_MOST:
			case MeasureSpec.EXACTLY:
				result = heightSize;
				break;
		}
		return result;
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int measureMaxWidth = measureWidth(widthMeasureSpec);
//		int measureHeight = measureHeight(heightMeasureSpec);
		// 计算自定义的ViewGroup中所有子控件的大小
		measureChildren(widthMeasureSpec, heightMeasureSpec);
		RowBean rowBean = new RowBean();
		int maxAvalidWidth = measureMaxWidth - 2 * VIEW_MARGIN_WIDTH;
		int needSumHeight = 0;
		for (int i = 0; i < getChildCount(); i++) {
			final View child = getChildAt(i);
			child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
			needSumHeight = addchild(rowBean, maxAvalidWidth, i, true);
		}
		if (isHide()) needSumHeight = Dp2Px.dip2px(48)+VIEW_MARGIN_HEIGHT;
		else needSumHeight+=VIEW_MARGIN_HEIGHT;
		setMeasuredDimension(measureMaxWidth, needSumHeight);

	}


	@Override
	protected void onLayout(boolean arg0, int l, int t, int r, int b) {
		final int count = getChildCount();
		RowBean rowBean = new RowBean();
		int maxAvalidWidth = r - 2 * VIEW_MARGIN_WIDTH;
		for (int i = 0; i < count; i++) {
			addchild(rowBean, maxAvalidWidth, i, false);
		}
		rows = rowBean.row;
		if (ib_controll!=null){
			if (rows>1){
				ib_controll.setVisibility(VISIBLE);
			}else {
				ib_controll.setVisibility(GONE);
			}
		}

	}



	private int addchild(RowBean rowBean, int maxAvalidWidth, int i, boolean isGetHeight) {
		int leftX = 0;
		int rightX = 0;
		int leftY = 0;
		int rightY = 0;
		final View child = this.getChildAt(i);
		int childWidth = child.getMeasuredWidth();
		int childHeight = child.getMeasuredHeight();
		int currentX = rowBean.lengthX;
		if (rowBean.num > 0) {
			currentX += VIEW_MARGIN_WIDTH + childWidth;
		} else {
			currentX += childWidth;
		}
		if (currentX > maxAvalidWidth) {//超过了最大可用长度
			if (rowBean.num == 0) {//此行第一个超过长度
				rowBean.row++;
				rowBean.num = 1;
				leftX = VIEW_MARGIN_WIDTH;
				rightX = maxAvalidWidth - VIEW_MARGIN_WIDTH;
				leftY = rowBean.row * VIEW_MARGIN_HEIGHT + (rowBean.row - 1) * childHeight;
				rightY = leftY + childHeight;
				currentX = rightX;
			} else {//第X个需要换行 X>1
				rowBean.num = 1;
				rowBean.row++;
				leftX = VIEW_MARGIN_WIDTH;
				if (childWidth > maxAvalidWidth) {
					childWidth = maxAvalidWidth;
				}
				rightX = leftX + childWidth;
				leftY = rowBean.row * VIEW_MARGIN_HEIGHT + (rowBean.row - 1) * childHeight;
				rightY = leftY + childHeight;
				currentX = rightX - VIEW_MARGIN_WIDTH;
			}
		} else {//直接添加
			if (rowBean.row == 0) {
				rowBean.row++;
			}
			leftX = currentX - childWidth + VIEW_MARGIN_WIDTH;
			leftY = rowBean.row * VIEW_MARGIN_HEIGHT + (rowBean.row - 1) * childHeight;
			rightX = currentX + VIEW_MARGIN_WIDTH;
			rightY = leftY + childHeight;
			rowBean.num++;
		}
		if (!isGetHeight) {
			child.layout(leftX, leftY, rightX, rightY);
		}
		rowBean.lengthX = currentX;
		return rightY;
	}

	//获取行数
	public int getRows(){
		return rows;
	}

}

class RowBean {
	int row = 0;
	int num = 0;
	int lengthX = 0;
}
