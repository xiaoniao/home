package com.example.viewpagerfragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class ScreenSlideActivity extends FragmentActivity {
	/**
	 * The number of pages (wizard steps) to show in this demo.
	 */
	private static final int NUM_PAGES = 5;

	/**
	 * The pager widget, which handles animation and allows swiping horizontally to access previous and next wizard steps.
	 */
	private ViewPager mPager;

	/**
	 * The pager adapter, which provides the pages to the view pager widget.
	 */
	private PagerAdapter mPagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen_slide);

		// Instantiate a ViewPager and a PagerAdapter.
		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);
	}

	/**
	 * A simple pager adapter that represents 5 {@link ScreenSlidePageFragment} objects, in sequence.
	 */
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return ScreenSlidePageFragment.create(position);
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}
	}

	// ViewPager����
	private class ZoomOutPageTransformer implements ViewPager.PageTransformer {
		private static final float MIN_SCALE = 0.85f;
		private static final float MIN_ALPHA = 0.5f;

		@Override
		public void transformPage(View view, float position) {
			// int pageWidth = view.getWidth();
			// int pageHeight = view.getHeight();
			//
			// if (position < -1) { // [-Infinity,-1)
			// // This page is way off-screen to the left.
			// view.setAlpha(0);
			//
			// } else if (position <= 1) { // [-1,1]
			// // Modify the default slide transition to shrink the page as well
			// float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
			// float vertMargin = pageHeight * (1 - scaleFactor) / 2;
			// float horzMargin = pageWidth * (1 - scaleFactor) / 2;
			// if (position < 0) {
			// view.setTranslationX(horzMargin - vertMargin / 2);
			// } else {
			// view.setTranslationX(-horzMargin + vertMargin / 2);
			// }
			//
			// // Scale the page down (between MIN_SCALE and 1)
			// view.setScaleX(scaleFactor);
			// view.setScaleY(scaleFactor);
			//
			// // Fade the page relative to its size.
			// view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
			//
			// } else { // (1,+Infinity]
			// // This page is way off-screen to the right.
			// view.setAlpha(0);
			// }
		}

	}
}
