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
 * չʾͼƬ����ͼ GridView
 */
public class PhotoThumbnailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

	private static final String STATE_IS_HIDDEN = "com.example.android.threadsample.STATE_IS_HIDDEN"; // ����״̬

	private int mColumnWidth; // ÿһ�еĿ�

	private Drawable mEmptyDrawable; // һ���յ�ͼ ��Ĭ��ͼ��

	private GridView mGridView; // չʾͼƬ

	private boolean mIsLoaded; // �Ѿ���ʼ����ͼƬ

	private Intent mServiceIntent; // ��IntentService����RSS

	private GridViewAdapter mAdapter; // ������

	// RSS·��
	private static final String PICASA_RSS_URL = "http://picasaweb.google.com/data/feed/base/featured?" + "alt=rss&kind=photo&access=public&slabel=featured&hl=en_US&imgmax=1600";

	// ͶӰ _id ThumbUrl ImageUrl
	private static final String[] PROJECTION = { DataProviderContract._ID, DataProviderContract.IMAGE_THUMBURL_COLUMN, DataProviderContract.IMAGE_URL_COLUMN };

	// Constants that define the order of columns in the returned cursor
	private static final int IMAGE_THUMBURL_CURSOR_INDEX = 1;
	private static final int IMAGE_URL_CURSOR_INDEX = 2;

	private static final int URL_LOADER = 0; // ��CursorLoader ��ʶ

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
			// ����һ���µ�CursorLoader
			return new CursorLoader(getActivity(), // Context
					DataProviderContract.PICTUREURL_TABLE_CONTENTURI, // Table to query ����
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

		// ʵ����������
		mAdapter = new GridViewAdapter(getActivity());

		// ��ÿؼ�
		mGridView = ((GridView) localView.findViewById(android.R.id.list));

		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);

		// dp ת��Ϊ��px ?
		int pixelSize = getResources().getDimensionPixelSize(R.dimen.thumbSize);

		// ���� ͼƬ��ռ��Ļ����?
		int widthScale = localDisplayMetrics.widthPixels / pixelSize;

		// �п�
		mColumnWidth = (localDisplayMetrics.widthPixels / widthScale);

		// �����п�
		mGridView.setColumnWidth(mColumnWidth);

		// ��������Ϊû��һ��Ҳû��
		mGridView.setNumColumns(-1);

		// ����������
		mGridView.setAdapter(mAdapter);

		// ���õ���¼�
		mGridView.setOnItemClickListener(this);

		// ���ÿ�EmptyView
		mGridView.setEmptyView(localView.findViewById(R.id.progressRoot));

		// ����Ĭ��ͼ
		mEmptyDrawable = getResources().getDrawable(R.drawable.imagenotqueued);

		// ��ʼ��CursorLoader
		getLoaderManager().initLoader(URL_LOADER, null, this);

		// ����һ��ͨ��IntentService��Intent������RSS����·��
		mServiceIntent = new Intent(getActivity(), RSSPullService.class).setData(Uri.parse(PICASA_RSS_URL)); // setData Uri

		// �ж��Ƿ�ָ�����
		if (bundle == null) { // Fragement�½���
			// �����û�м���
			if (!this.mIsLoaded) {
				// ����IntentService
				getActivity().startService(mServiceIntent);
			}
		} else if (bundle.getBoolean(STATE_IS_HIDDEN, false)) { // Fragment��ǰ���� �ָ�����
			// ʵ��
			FragmentTransaction localFragmentTransaction = getFragmentManager().beginTransaction();
			// ����fragment
			localFragmentTransaction.hide(this);
			// �ύ����
			localFragmentTransaction.commit();
		}

		return localView;
	}

	/*
	 * Fragment ���ٵ�ʱ�����
	 */
	@Override
	public void onDestroyView() {
		// �ÿ�Ϊ�� ��ֹ�ڴ�й©
		mGridView = null;

		// �ÿ�mEmptyDrawable
		if (mEmptyDrawable != null) {
			this.mEmptyDrawable.setCallback(null);
			this.mEmptyDrawable = null;
		}

		super.onDestroyView();
	}

	// ��onDestroyView��ִ�� �ÿձ������ر�CursorLoader,�ȵ�...
	@Override
	public void onDetach() {
		try {
			// �ر�CursorLoader
			getLoaderManager().destroyLoader(0);
			// ��ձ���
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
	 * ��Fragment�ɼ��Ըı�ʱ����
	 */
	@Override
	public void onHiddenChanged(boolean viewState) {
		super.onHiddenChanged(viewState);
	}

	/*
	 * GridView����¼�
	 */
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int viewId, long rowId) {
		// ��õ��View�������һ��cursor
		Cursor cursor = (Cursor) mAdapter.getItem(viewId);

		// ���ͼƬ·��
		String urlString = cursor.getString(IMAGE_URL_CURSOR_INDEX);

		// ������һ��IntentΪ����ʾһ����ͼ����һ��Fragment ps:Constants.ACTION_VIEW_IMAGE Action com.example.android.threadsample.ACTION_VIEW_IMAGE
		Intent localIntent = new Intent(Constants.ACTION_VIEW_IMAGE).setData(Uri.parse(urlString)); // setData Uri

		// ͨ���㲥����Intent Ŀ����FragmentDisplayer����
		LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(localIntent);
	}

	/*
	 * ��CursorLoader��ɲ�ѯʱ���� , loader��returnCursor��CursorLoader������
	 */
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor returnCursor) {
		/*
		 * �ı���������Cursor, ǿ��View�ػ�
		 */
		mAdapter.changeCursor(returnCursor); // ??
	}

	/*
	 * ��CursorLoader���õ�ʱ�����, �������ݸı����Cursor�����µ�
	 */
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// ������������̨����Ϊ�գ������ڴ�й©
		mAdapter.changeCursor(null);
	}

	/*
	 * ����Fragment״̬
	 */
	@Override
	public void onSaveInstanceState(Bundle bundle) {

		// �����Ƿ�����״̬ isHidden?
		bundle.putBoolean(STATE_IS_HIDDEN, isHidden());

		super.onSaveInstanceState(bundle);
	}

	// �����Ƿ����״̬
	public void setLoaded(boolean loadState) {
		mIsLoaded = loadState;
	}

	/**
	 * �̳� CursorAdapter, ͨ����̨Cursor��ʾͼƬ��ֱ��ͨ��URL��ʾͼƬ��
	 */
	private class GridViewAdapter extends CursorAdapter {

		// ���췽��
		public GridViewAdapter(Context context) {
			super(context, null, false);
		}

		/**
		 * ��һ��View��һ��Cursor�ϣ� Cursor�����ѯ������һ�� Binds a View and a Cursor
		 */
		@Override
		public void bindView(View view, Context context, Cursor cursor) {

			// ���View ת����PhotoView һ���̳���ImageView�Ķ���
			PhotoView localImageDownloaderView = (PhotoView) view.getTag();

			try {
				// ת��ΪURL
				URL localURL = new URL(cursor.getString(IMAGE_THUMBURL_CURSOR_INDEX));

				// ����PhotoView setImageURL ���ͼƬ֮ǰ�����ھ����غͽ���
				localImageDownloaderView.setImageURL(localURL, true, PhotoThumbnailFragment.this.mEmptyDrawable);

			} catch (MalformedURLException localMalformedURLException) { // ������ȷ��URL
				localMalformedURLException.printStackTrace();

			} catch (RejectedExecutionException localRejectedExecutionException) { // �������̳߳������ػ����ͼƬ�쳣

			}
		}

		/**
		 * ����һ���µ�View����ʾCursor����
		 */
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
			LayoutInflater inflater = LayoutInflater.from(context);

			// ���ز���
			View layoutView = inflater.inflate(R.layout.galleryitem, null);

			// ���ͼƬ�ؼ�
			View thumbView = layoutView.findViewById(R.id.thumbImage);

			// ����item��� ..��Ϊû����xml�����ã�������ͨ���ڴ����ж�̬��õģ����ַ�ʽ�܌�
			layoutView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, PhotoThumbnailFragment.this.mColumnWidth));

			// ��Item����Tag
			layoutView.setTag(thumbView);
			return layoutView;
		}

	}
}
