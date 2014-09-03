package com.example.android.threadsample;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.RejectedExecutionException;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.CursorAdapter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

/**
 * 展示图片缩略图 GridView
 */
public class PhotoThumbnailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

	private static final String STATE_IS_HIDDEN = "com.example.android.threadsample.STATE_IS_HIDDEN"; // 隐藏状态

	private int mColumnWidth; // 每一列的宽

	private Drawable mEmptyDrawable; // 一个空的图 （默认图）

	private GridView mGridView; // 展示图片

	private boolean mIsLoaded; // 已经开始加载图片

	private Intent mServiceIntent; // 打开IntentService下载RSS

	private GridViewAdapter mAdapter; // 适配器

	// RSS路径
	private static final String PICASA_RSS_URL = "http://picasaweb.google.com/data/feed/base/featured?" + "alt=rss&kind=photo&access=public&slabel=featured&hl=en_US&imgmax=1600";

	// 投影 _id ThumbUrl ImageUrl
	private static final String[] PROJECTION = { DataProviderContract._ID, DataProviderContract.IMAGE_THUMBURL_COLUMN, DataProviderContract.IMAGE_URL_COLUMN };

	// Constants that define the order of columns in the returned cursor
	private static final int IMAGE_THUMBURL_CURSOR_INDEX = 1;
	private static final int IMAGE_URL_CURSOR_INDEX = 2;

	private static final int URL_LOADER = 0; // 该CursorLoader 标识

	/*
	 * This callback is invoked when the framework is starting or re-starting the Loader.
	 * 
	 * It returns a CursorLoader object containing the desired query
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
		/*
		 * Takes action based on the ID of the Loader that's being created
		 */
		switch (loaderID) {
		case URL_LOADER:
			// 返回一个新的CursorLoader
			return new CursorLoader(getActivity(), // Context
					DataProviderContract.PICTUREURL_TABLE_CONTENTURI, // Table to query 表名
					PROJECTION, // Projection to return
					null, // No selection clause
					null, // No selection arguments
					null // Default sort order
			);
		default:
			return null;

		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
		super.onCreateView(inflater, viewGroup, bundle);

		View localView = inflater.inflate(R.layout.gridlist, viewGroup, false);

		// 实例化适配器
		mAdapter = new GridViewAdapter(getActivity());

		// 获得控件
		mGridView = ((GridView) localView.findViewById(android.R.id.list));

		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);

		// dp 转换为了px ?
		int pixelSize = getResources().getDimensionPixelSize(R.dimen.thumbSize);

		// 几列 图片宽占屏幕宽几列?
		int widthScale = localDisplayMetrics.widthPixels / pixelSize;

		// 列宽
		mColumnWidth = (localDisplayMetrics.widthPixels / widthScale);

		// 设置列宽
		mGridView.setColumnWidth(mColumnWidth);

		// 首先设置为没有一列也没有
		mGridView.setNumColumns(-1);

		// 设置适配器
		mGridView.setAdapter(mAdapter);

		// 设置点击事件
		mGridView.setOnItemClickListener(this);

		// 设置空EmptyView
		mGridView.setEmptyView(localView.findViewById(R.id.progressRoot));

		// 设置默认图
		mEmptyDrawable = getResources().getDrawable(R.drawable.imagenotqueued);

		// 初始化CursorLoader
		getLoaderManager().initLoader(URL_LOADER, null, this);

		// 创建一个通向IntentService的Intent并包含RSS下载路径
		mServiceIntent = new Intent(getActivity(), RSSPullService.class).setData(Uri.parse(PICASA_RSS_URL)); // setData Uri

		// 判断是否恢复数据
		if (bundle == null) { // Fragement新建的
			// 如果还没有加载
			if (!this.mIsLoaded) {
				// 启动IntentService
				getActivity().startService(mServiceIntent);
			}
		} else if (bundle.getBoolean(STATE_IS_HIDDEN, false)) { // Fragment先前存在 恢复数据
			// 实务
			FragmentTransaction localFragmentTransaction = getFragmentManager().beginTransaction();
			// 隐藏fragment
			localFragmentTransaction.hide(this);
			// 提交事务
			localFragmentTransaction.commit();
		}

		return localView;
	}

	/*
	 * Fragment 销毁的时候调用
	 */
	@Override
	public void onDestroyView() {
		// 置空为了 防止内存泄漏
		mGridView = null;

		// 置空mEmptyDrawable
		if (mEmptyDrawable != null) {
			this.mEmptyDrawable.setCallback(null);
			this.mEmptyDrawable = null;
		}

		super.onDestroyView();
	}

	// 在onDestroyView后执行 置空变量，关闭CursorLoader,等等...
	@Override
	public void onDetach() {
		try {
			// 关闭CursorLoader
			getLoaderManager().destroyLoader(0);
			// 清空变量
			if (mAdapter != null) {
				mAdapter.changeCursor(null);
				mAdapter = null;
			}
		} catch (Throwable localThrowable) {

		}
		super.onDetach();
		return;
	}

	/*
	 * 当Fragment可见性改变时调用
	 */
	@Override
	public void onHiddenChanged(boolean viewState) {
		super.onHiddenChanged(viewState);
	}

	/*
	 * GridView点击事件
	 */
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int viewId, long rowId) {
		// 获得点击View代表的那一行cursor
		Cursor cursor = (Cursor) mAdapter.getItem(viewId);

		// 获得图片路径
		String urlString = cursor.getString(IMAGE_URL_CURSOR_INDEX);

		// 创建另一个Intent为了显示一个大图在另一个Fragment ps:Constants.ACTION_VIEW_IMAGE Action com.example.android.threadsample.ACTION_VIEW_IMAGE
		Intent localIntent = new Intent(Constants.ACTION_VIEW_IMAGE).setData(Uri.parse(urlString)); // setData Uri

		// 通过广播发送Intent 目标是FragmentDisplayer接收
		LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(localIntent);
	}

	/*
	 * 当CursorLoader完成查询时调用 , loader和returnCursor是CursorLoader的引用
	 */
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor returnCursor) {
		/*
		 * 改变适配器的Cursor, 强制View重绘
		 */
		mAdapter.changeCursor(returnCursor); // ??
	}

	/*
	 * 当CursorLoader重置的时候调用, 例如数据改变或者Cursor不是新的
	 */
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// 设置适配器后台数据为空，避免内存泄漏
		mAdapter.changeCursor(null);
	}

	/*
	 * 保存Fragment状态
	 */
	@Override
	public void onSaveInstanceState(Bundle bundle) {

		// 保存是否隐藏状态 isHidden?
		bundle.putBoolean(STATE_IS_HIDDEN, isHidden());

		super.onSaveInstanceState(bundle);
	}

	// 设置是否加载状态
	public void setLoaded(boolean loadState) {
		mIsLoaded = loadState;
	}

	/**
	 * 继承 CursorAdapter, 通过后台Cursor显示图片比直接通过URL显示图片好
	 */
	private class GridViewAdapter extends CursorAdapter {

		// 构造方法
		public GridViewAdapter(Context context) {
			super(context, null, false);
		}

		/**
		 * 绑定一个View到一个Cursor上， Cursor代表查询出来的一行 Binds a View and a Cursor
		 */
		@Override
		public void bindView(View view, Context context, Cursor cursor) {

			// 获得View 转换成PhotoView 一个继承自ImageView的对象
			PhotoView localImageDownloaderView = (PhotoView) view.getTag();

			try {
				// 转换为URL
				URL localURL = new URL(cursor.getString(IMAGE_THUMBURL_CURSOR_INDEX));

				// 设置PhotoView setImageURL 如果图片之前不存在就下载和解析
				localImageDownloaderView.setImageURL(localURL, true, PhotoThumbnailFragment.this.mEmptyDrawable);

			} catch (MalformedURLException localMalformedURLException) { // 捕获不正确的URL
				localMalformedURLException.printStackTrace();

			} catch (RejectedExecutionException localRejectedExecutionException) { // 捕获在线程池中下载或解析图片异常

			}
		}

		/**
		 * 创建一个新的View，显示Cursor内容
		 */
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
			LayoutInflater inflater = LayoutInflater.from(context);

			// 加载布局
			View layoutView = inflater.inflate(R.layout.galleryitem, null);

			// 获得图片控件
			View thumbView = layoutView.findViewById(R.id.thumbImage);

			// 设置item宽高 ..因为没有在xml中设置，而宽是通过在代码中动态获得的，这种方式很屌
			layoutView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, PhotoThumbnailFragment.this.mColumnWidth));

			// 给Item设置Tag
			layoutView.setTag(thumbView);
			return layoutView;
		}

	}
}
