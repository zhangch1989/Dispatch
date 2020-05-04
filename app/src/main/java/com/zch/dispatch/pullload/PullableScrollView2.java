package com.zch.dispatch.pullload;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class PullableScrollView2 extends ScrollView implements Pullable
{
	float mDownPosX = 0;
	float mDownPosY = 0;

	public PullableScrollView2(Context context)
	{
		super(context);
	}

	public PullableScrollView2(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public PullableScrollView2(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	public boolean canPullDown()
	{
		if (getScrollY() == 0)
			return true;
		else
			return false;
//		return true;
	}

	@Override
	public boolean canPullUp()
	{
		if (getScrollY() >= (getChildAt(0).getHeight() - getMeasuredHeight()))
			return false;
		else
			return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final float x = ev.getX();
		final float y = ev.getY();
		final int action = ev.getAction();
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				mDownPosX = x;
				mDownPosY = y;
				break;
			case MotionEvent.ACTION_MOVE:
				float deltaX = Math.abs(x - mDownPosX);
				float deltaY = Math.abs(y - mDownPosY);
				// 这里是够拦截的判断依据是左右滑动，读者可根据自己的逻辑进行是否拦截
				if (deltaX > deltaY) {
					return false;
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
//		requestDisallowInterceptTouchEvent(true);
		return super.dispatchTouchEvent(ev);
	}
}
