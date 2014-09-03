package com.example.android.threadsample;

import java.net.URL;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;

/**
 * �̳߳أ��ƶ����̳߳ش�С�ͻ����С,���������Щ���룬����Ҫ����������Щ���ԣ���������������
 * �����ʹ���������̳߳�
 * ������һ��Handler���ں�UI����
 */
public class PhotoManager {

	// ״̬��
	static final int DOWNLOAD_FAILED   = -1;    // ����ʧ��
	static final int DOWNLOAD_STARTED  =  1;    // ���ؿ�ʼ
	static final int DOWNLOAD_COMPLETE =  2;    // �������
	static final int DECODE_STARTED    =  3;    // ��ʼ����
	static final int TASK_COMPLETE     =  4;    // �������

	// ����ͼƬ��С 4M
	private static final int              IMAGE_CACHE_SIZE = 1024 * 1024 * 4;
	// �ȴ�����ʱ��
	private static final int              KEEP_ALIVE_TIME = 1;
	// ���õ�λ
	private static final TimeUnit         KEEP_ALIVE_TIME_UNIT;
	
	// �̳߳ش�С
	private static final int              CORE_POOL_SIZE = 8;
	// ����̳߳ش�С
	private static final int              MAXIMUM_POOL_SIZE = 8;
	// �ɼ��Ĵ�������
	private static int                    NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

	// Lru ����
	private final LruCache<URL, byte[]>   mPhotoCache;

	// �����̳߳� �̶߳���
	private final BlockingQueue<Runnable> mDownloadWorkQueue;
	// �����̳߳� �̶߳���
	private final BlockingQueue<Runnable> mDecodeWorkQueue;

	// PhotoTask ���� ���ڲ������غͽ�������
	private final Queue<PhotoTask>        mPhotoTaskWorkQueue;

	// �����̳߳�
	private final ThreadPoolExecutor      mDownloadThreadPool;
	// �����̳߳�
	private final ThreadPoolExecutor      mDecodeThreadPool;

	// Handler��UI����
	private Handler mHandler;

	// ����PhotoManagerʵ��
	private static PhotoManager sInstance = null;

	static {
		KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;  // ��λ����
		sInstance = new PhotoManager();           // ʵ����PhotoManager ����
	}
	
	// ������ʽ���PhotoManagerʵ��
    public static PhotoManager getInstance() {
        return sInstance;
    }


	// ���췽���� ʵ�������ַ���
	private PhotoManager() {
	    // ʵ��������
		mDownloadWorkQueue  = new LinkedBlockingQueue<Runnable>();
		mDecodeWorkQueue    = new LinkedBlockingQueue<Runnable>();
		mPhotoTaskWorkQueue = new LinkedBlockingQueue<PhotoTask>();
		
		// ʵ�����̳߳�
		mDownloadThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mDownloadWorkQueue);
		mDecodeThreadPool   = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mDecodeWorkQueue);
		
		// ʵ����LurCache
		mPhotoCache         = new LruCache<URL, byte[]>(IMAGE_CACHE_SIZE) {
		    
		    // This overrides the default sizeOf() implementation to return the correct size of each cache entry.
			@Override
			protected int sizeOf(URL paramURL, byte[] paramArrayOfByte) {
				return paramArrayOfByte.length;
			}
		};
		
		// ʵ����Handler ���������̷߳���Bitmap  ps.���⻰ �����ͨ���㲥�Ļ�����Ͳ���Ҫ��
		mHandler = new Handler(Looper.getMainLooper()) {

		    // ��Handler���յ�һ����Ϣִ��
			@Override
			public void handleMessage(Message inputMessage) {

				// ��Message�л�����趫��
				PhotoTask photoTask = (PhotoTask) inputMessage.obj;

				// ��PhotoTask�л��PhotoView,ͨ��������
				PhotoView localView = photoTask.getPhotoView();

				if (localView != null) {

					// �ӻ��PhotoView�л��URL
					URL localURL = localView.getLocation();

					// ��PhotoTask�л��URL
					if (photoTask.getImageURL() == localURL)

						switch (inputMessage.what) {

						case DOWNLOAD_STARTED:  // ��ʼ����
							localView.setStatusResource(R.drawable.imagedownloading);
							break;

						case DOWNLOAD_COMPLETE: // �������
							localView.setStatusResource(R.drawable.decodequeued);
							break;
							
						case DECODE_STARTED:   // ��ʼ����
							localView.setStatusResource(R.drawable.decodedecoding);
							break;
							
						case TASK_COMPLETE:    // ���ؽ������
							localView.setImageBitmap(photoTask.getImage()); // ��photoTask�л��ͼƬ
							recycleTask(photoTask);                         // ��������ʹ��photoTask
							break;
							
						case DOWNLOAD_FAILED:  // ����ʧ��
							localView.setStatusResource(R.drawable.imagedownloadfailed);
							recycleTask(photoTask);                         // ��������ʹ��photoTask
							break;
						default:
							// ����ֱ��ʹ�ø���
							super.handleMessage(inputMessage);              // �������ʵ���Ա������Ϣ
						}
				}
			}
		};
	}

	/** ����PhotoTask��״̬  ps.���⻰ �����ͨ���㲥 ����Ͳ���Ҫ�� ֱ�ӵ���BroadcastNotifier2 ���Ӧ��Action���͹㲥��OK�� (�����ֱ�Ӱ����ݷ��͹�����ô��) **/
	public void handleState(PhotoTask photoTask, int state) {
	    
		switch (state) {

		case TASK_COMPLETE:                    // ���ؽ������
		    
			if (photoTask.isCacheEnabled()) {  // �Ƿ���������
			    
			    // ��ͼƬ�Ž�Lru������
				mPhotoCache.put(photoTask.getImageURL(), photoTask.getByteBuffer());
			}

			// ��handler������Ϣ
			Message completeMessage = mHandler.obtainMessage(state, photoTask);
			completeMessage.sendToTarget();
			
			break;

		case DOWNLOAD_COMPLETE:               // �������
		    
		    // ��photoTask�л�ý���Runnable, ���뵽�����̳߳���
			mDecodeThreadPool.execute(photoTask.getPhotoDecodeRunnable()); 

		default:
		    
		    // Ĭ��������ֱ�ӷ���
			mHandler.obtainMessage(state, photoTask).sendToTarget();
			break;
		}

	}

	/** ȡ�������߳� **/
	public static void cancelAll() {

	    // ����һ�������ض��д�Сһ��������
		PhotoTask[] taskArray = new PhotoTask[sInstance.mDownloadWorkQueue.size()];

		// �����ض���ת��������
		sInstance.mDownloadWorkQueue.toArray(taskArray);

		// ���������С
		int taskArraylen = taskArray.length;

		// �̰߳�ȫ
		synchronized (sInstance) {

		    // ѭ������
			for (int taskArrayIndex = 0; taskArrayIndex < taskArraylen; taskArrayIndex++) {

				// ��õ�ǰ�������е��߳�
				Thread thread = taskArray[taskArrayIndex].mThreadThis;

				if (null != thread) {    // ����̴߳���
					thread.interrupt();  // ֹͣ�߳�
				}
			}
		}
	}

	/** ֹͣ�����̣߳������̳߳����Ƴ� **/
	static public void removeDownload(PhotoTask downloaderTask, URL pictureURL) {

	    // ���PhotoTask(�߳�?)��Ȼ���ڣ�������������URL��pictureURLһ��
		if (downloaderTask != null && downloaderTask.getImageURL().equals(pictureURL)) {

			// �̰߳�ȫ
			synchronized (sInstance) {

				// ����������е��߳�
				Thread thread = downloaderTask.getCurrentThread();

				if (null != thread) {    // ����̴߳���
					thread.interrupt();  // ֹͣ�߳�
				}
			}
			
			// �������̳߳����Ƴ������߳�  (�������getCurrentThread��ʲô������)
			sInstance.mDownloadThreadPool.remove(downloaderTask.getHTTPDownloadRunnable());
		}
	}

	/** ��ʼ���غͽ��� **/
	static public PhotoTask startDownload(PhotoView imageView, boolean cacheFlag) {

	    // ��PhotoTask���ж������PhotoTask
		PhotoTask downloadTask = sInstance.mPhotoTaskWorkQueue.poll();

		// ���PhotoTaskΪ�գ���ֱ��ʵ����
		if (null == downloadTask) {
			downloadTask = new PhotoTask();           // ���⻰��Ӧ�ð�url����������������Ϊ������ظ�ʹ�õĻ���ô��
		}

		// ��ʼ��PhotoTask����
		downloadTask.initializeDownloaderTask(PhotoManager.sInstance, imageView, cacheFlag); // �����������url�Ƿ���imageView��

		// ����CacheBuffer , ͨ��LruCache��ô�С
		downloadTask.setByteBuffer(sInstance.mPhotoCache.get(downloadTask.getImageURL()));

		// ���ByteBuffer�ǿգ�˵��ͼƬû�л���
		if (null == downloadTask.getByteBuffer()) {    // ͼƬû�л���
		    
		    // �������߳����ӽ������̳߳���,��ʼִ���߳�����
			sInstance.mDownloadThreadPool.execute(downloadTask.getHTTPDownloadRunnable());

			// ����ͼƬ����
			imageView.setStatusResource(R.drawable.imagequeued);

		} else {                                       // ͼƬ�Ѿ�����

		    // ��Handler����״̬ͼƬ�Ѿ��������
			sInstance.handleState(downloadTask, DOWNLOAD_COMPLETE);
		}

		return downloadTask;
	}

	/** �������� **/
	void recycleTask(PhotoTask downloadTask) {
	    
		// ��������ִ��task�е�recycle����
		downloadTask.recycle();

		// �����β���Ա��ظ�����
		mPhotoTaskWorkQueue.offer(downloadTask);
	}
}