/*
 * Copyright (C) ${year} The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.threadsample;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 下载线程 :通过URL下载字节数据,当任务完成时会通过handleState发送结果状态。 
 * 
 * 该线程由PhotoTask实例化和管理,使用接口来进行通信
 */
class PhotoDownloadRunnable implements Runnable {
    
	// 每次读取的字节大小
	private static final int READ_SIZE            = 1024 * 2;

	// 下载状态码
	static final int         HTTP_STATE_FAILED    = -1;        // 下载失败
	static final int         HTTP_STATE_STARTED   = 0;         // 下载开始
	static final int         HTTP_STATE_COMPLETED = 1;         // 下载完成

	// 实例化接口，用于和PhotoTask通信
	final TaskRunnableDownloadMethods mPhotoTask;

	/** 构造方法，并由PhotoTask保持引用 **/
    PhotoDownloadRunnable(TaskRunnableDownloadMethods photoTask) {
        mPhotoTask = photoTask;
    }
    
	/** 自定义下载接口 **/
	interface TaskRunnableDownloadMethods {
		void   setDownloadThread(Thread currentThread);
		byte[] getByteBuffer();
		void   setByteBuffer(byte[] buffer);
		void   handleDownloadState(int state);
		URL    getImageURL();
	}

	@Override
	public void run() {

		// 设置当前线程，用于PhotoTask管理
		mPhotoTask.setDownloadThread(Thread.currentThread());

		// 设置线程级别
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND); // 设置为后台优先级

		// 获得byteBuffer
		byte[] byteBuffer = mPhotoTask.getByteBuffer();

		try {
		    
			if (Thread.interrupted()) {            // 检测线程是否被停止
				throw new InterruptedException();
			}

			if (null == byteBuffer) {              // 没有缓存,就下载

				// 发现消息 开始下载
				mPhotoTask.handleDownloadState(HTTP_STATE_STARTED);

				InputStream byteStream = null;

				try {

				    // 获得数据源URL，执行下载 (题外话：可以把访问URL同样的放在PhotoTask中,这样比直接放在构造方法传递过来好的多)
					HttpURLConnection httpConn = (HttpURLConnection) mPhotoTask.getImageURL().openConnection();

					httpConn.setRequestProperty("User-Agent", Constants.USER_AGENT);

					if (Thread.interrupted()) {             // 检测线程是否被停止
						throw new InterruptedException();
					}
					
					byteStream = httpConn.getInputStream(); // 获得下载图片流

					if (Thread.interrupted()) {             // 检测线程是否被停止
						throw new InterruptedException();
					}

					// 获得下载文件的大小 (有可能不返回)
					int contentSize = httpConn.getContentLength();

					if (-1 == contentSize) {

						// 指定每次读取的大小
						byte[] tempBuffer = new byte[READ_SIZE];

						int bufferLeft = tempBuffer.length;

						int bufferOffset = 0;
						int readResult = 0;

						/*
						 * The "outer" loop continues until all the bytes have been downloaded. 
						 * 
						 * The inner loop continues until the temporary buffer is full, and then allocates more buffer space.
						 */
						outer: do {
						    
							while (bufferLeft > 0) {
							    // InputStream.read() 返回0 代表下载完毕
								readResult = byteStream.read(tempBuffer, bufferOffset, bufferLeft);
								if (readResult < 0) {
									break outer;                     // 下载完毕退出outer
								}
								bufferOffset += readResult;
								bufferLeft -= readResult;
								if (Thread.interrupted()) {           // 检测线程是否被停止
									throw new InterruptedException();
								}
							}
							bufferLeft = READ_SIZE;
							int newSize = tempBuffer.length + READ_SIZE;
							byte[] expandedBuffer = new byte[newSize];
							System.arraycopy(tempBuffer, 0, expandedBuffer, 0, tempBuffer.length);
							tempBuffer = expandedBuffer;
						} while (true);

						byteBuffer = new byte[bufferOffset];
						System.arraycopy(tempBuffer, 0, byteBuffer, 0, bufferOffset);
						
					} else {
					    
						byteBuffer = new byte[contentSize];
						int remainingLength = contentSize;
						int bufferOffset = 0;
						while (remainingLength > 0) {
							int readResult = byteStream.read(byteBuffer, bufferOffset, remainingLength);
							if (readResult < 0) {
								throw new EOFException();
							}
							bufferOffset += readResult;
							remainingLength -= readResult;
							if (Thread.interrupted()) {
								throw new InterruptedException();
							}
						}
						
					}

					if (Thread.interrupted()) {
						throw new InterruptedException();
					}

				} catch (IOException e) {
				    
					e.printStackTrace();
					return;
					
				} finally {
				    
					if (null != byteStream) {
						try {
							byteStream.close();
						} catch (Exception e) {

						}
					}
				}
			}

			// 保存读取到的byteBuffer  ps.题外话，如果是通过线程下载数据这里应该改方法，例如setBodyString 发请求到的数据发送出去
			mPhotoTask.setByteBuffer(byteBuffer);

			// 发送消息 下载完毕
			mPhotoTask.handleDownloadState(HTTP_STATE_COMPLETED);

		} catch (InterruptedException e1) {

		} finally {

		    // 如果byteBuffer为空 发送消息 下载失败
			if (null == byteBuffer) {
				mPhotoTask.handleDownloadState(HTTP_STATE_FAILED);
			}

			// 设置下载线程为空，另一方面释放内存
			mPhotoTask.setDownloadThread(null);

			// 终止线程
			Thread.interrupted();
		}
	}
}
