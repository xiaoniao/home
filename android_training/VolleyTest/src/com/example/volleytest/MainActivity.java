package com.example.volleytest;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

public class MainActivity extends Activity implements OnClickListener {

    private Button           bt_json;
    private Button           bt_image;
    private Button           bt_netimage;
    private Button           bt_imageloder;

    private ImageView        iv_volley;
    private TextView         tv_volley;
    private NetworkImageView netIV_volley;

    private String           jsonObjectStr = "{'name':'llb','age':'20'}";
    private JSONObject       jsonUp;
    private RequestQueue     queue;
    private String           jsonUrl       = "http://218.192.170.251:8080/AndroidServerDemo/LoginServlet";

    private String           imageUrl1     = "http://img1.27.cn/images/201011/04/1288857805_42835600.jpg";
    private String           imageUrl2     = "http://img.cf8.com.cn/uploadfile/2011/1031/20111031100803979.jpg";
    private String           imageUrl3     = "http://img.pconline.com.cn/images/upload/upc/tx/wallpaper/1209/07/c1/13698570_1347000164468_320x480.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

    }

    /** * ��ʼ�������ؼ� */
    private void initView() {

        bt_json = (Button)findViewById(R.id.bt_json);
        bt_image = (Button)findViewById(R.id.bt_image);
        bt_netimage = (Button)findViewById(R.id.bt_netimage);
        bt_imageloder = (Button)findViewById(R.id.bt_imageloder);
        iv_volley = (ImageView)findViewById(R.id.iv_volley);
        tv_volley = (TextView)findViewById(R.id.tv_volley);
        netIV_volley = (NetworkImageView)findViewById(R.id.netIV_volley);

        queue = Volley.newRequestQueue(this);// ��ȡһ��������ж���

        bt_json.setOnClickListener(this);// ��ť����¼�����
        bt_image.setOnClickListener(this);
        bt_netimage.setOnClickListener(this);
        bt_imageloder.setOnClickListener(this);
    }

    /** * ����json���� */
    private void requestJsonObject() {

        try {
            jsonUp = new JSONObject(jsonObjectStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.POST, jsonUrl, jsonUp,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        String json = jsonObject.toString();
                        tv_volley.setText("���������ص�json���ݣ�" + json);
                    }
                }, new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError jsonObject) {
                        tv_volley.setText("����������ʧ�ܣ�" + jsonObject.getMessage());
                    }
                });

        queue.add(jsonObjectRequest);// ������󵽶�����
    }

    private void requestByImageRequest() {

        // ���󷽷�1��ImageRequest�ܹ�������ͼƬ������bitmap��
        ImageRequest imageRequest = new ImageRequest(imageUrl1, new Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                Log.i("llb", "bitmap height" + response.getHeight() + "&width=" + response.getWidth());
                iv_volley.setImageBitmap(response);// ��ʾͼƬ
            }
        }, 200, 200, Config.ARGB_8888, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "����ͼƬʧ����", 0).show();
            }
        });

        // ImageRequest���Ѿ�д���˻��棬ֱ���þͺ��ˣ�ʹ�õ���DiskBasedCache
        // ���Է���ֱ�ӰѴ�ͼ�ŵ��������·�����������Ǹı�����ʾʱ�Ĵ�С��
        // ����ͼƬ��С���ޱ仯�����⣿��
        imageRequest.shouldCache();// �����ļ���/data/data/����/cache/volley
        queue.add(imageRequest);// ��������뵽��������
    }

    private void requestByImageLoader() {

        // ������������ImageLoader
        ImageListener listener = ImageLoader
                .getImageListener(iv_volley, R.drawable.ic_launcher, R.drawable.ic_launcher);

        // �����ļ�Ҳ����/data/data/����/cache/volley������ͼƬ��С���ޱ仯
        ImageLoader loader = new ImageLoader(queue, new MyImageCache(5 * 1024 * 1024));

        loader.get(imageUrl2, listener, 300, 300);// ��ȡͼƬ
        // ����ǵ���ImageRequest�����doParse()����ȥ��������
    }

    private void requestByNetworkImageView() {

        // ������������NetworkImageView������ͼƬ
        ImageLoader imageLoader = new ImageLoader(queue, new MyImageCache(5 * 1024 * 1024));

        netIV_volley.setDefaultImageResId(R.drawable.ic_launcher);// Ĭ��ͼƬ
        netIV_volley.setErrorImageResId(R.drawable.ic_launcher);// ����ʱ��ͼƬ

        netIV_volley.setImageUrl(imageUrl3, imageLoader);// ����ͼƬ
    }

    @Override
    public void onClick(View v) { // ��ť��Ӧ
        switch (v.getId()) {
            case R.id.bt_json:// ����json
                requestJsonObject();
                break;
            case R.id.bt_image:// ����ImageRequest����ͼƬ
                requestByImageRequest();
                break;
            case R.id.bt_imageloder:// ����ImageLoader����ͼƬ
                requestByImageLoader();
                break;
            case R.id.bt_netimage:
                requestByNetworkImageView();
                break;
        }
    }

}
