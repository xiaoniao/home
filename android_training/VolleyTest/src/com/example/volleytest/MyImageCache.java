package com.example.volleytest;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class MyImageCache implements ImageCache {

    private LruCache<String, Bitmap> cache;

    /** * 构造函数，完成LruCache<String,Bitmap>对象的初始化 * @param maxSize LruCache的最大空间 单位为Byte */
    public MyImageCache(int maxSize) {
        super();
        cache = new LruCache<String, Bitmap>(maxSize);
    }

    @Override
    public Bitmap getBitmap(String url) {
        return cache.get(url);// 在缓存里面寻找是否有
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        // 这里传进来的bitmap已经根据请求时候给的maxWidth和maxHeight改变过尺寸了
        // 如果是NetworkImageView请求的则是默认原尺寸。但有一点一直没整明白：
        // 为何bitmap的尺寸大小并没有影响到put进去的缓存文件大小呢？？？
        // 还望弄明白的哥们提点一下，先谢了(^o^)/
        cache.put(url, bitmap);// 把图片存进缓存
    }
}
