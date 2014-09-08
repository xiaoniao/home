package com.example.layout_view3;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class SimpleLayout extends ViewGroup {

	public SimpleLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// ���ô�С
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		System.out.println("SimpleLayout onMeasure");
		System.out.println(getChildCount());
		System.out.println("widthMeasureSpec : " + widthMeasureSpec);
		System.out.println("heightMeasureSpec : " + heightMeasureSpec);

		if (getChildCount() > 0) {
			View childView = getChildAt(0);
			measureChild(childView, widthMeasureSpec, widthMeasureSpec);
		}
	}

	// ���ò���λ��
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		System.out.println("SimpleLayout onLayout");
		System.out.println(getChildCount());
		System.out.println("changed : " + changed);
		System.out.println("l t r b " + l + " " + t + " " + r + " " + b);

		if (getChildCount() > 0) {
			View childView = getChildAt(0);
			childView.layout(l, t, r, b);
		}

	}

}
