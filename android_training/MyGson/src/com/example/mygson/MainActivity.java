package com.example.mygson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;

import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ���л� ת��Ϊjson�ַ���
//        Gson gson = new Gson();
//        System.out.println(gson.toJson(null));
//        System.out.println(gson.toJson(1));
//        System.out.println(gson.toJson(Long.valueOf(1)));
//        System.out.println(gson.toJson(new int[] {1}));
//        System.out.println(gson.toJson("abcd"));
        // �����л� jsonתjava object
        
        // test_Gson();
        test_json_stream_parser();
    }

    public void test_FieldAttributes() {
        // FieldAttributes ʹ�õ��Ƿ����õ�Field�ֶ�����ֵ
        Field field = null;
        FieldAttributes fieldAttributes = new FieldAttributes(field); // ������Field

        fieldAttributes.getAnnotation(Annotation.class); //  <T extends Annotation>
        fieldAttributes.getAnnotations();                //  Collection<Annotation>
        fieldAttributes.getDeclaredClass();              //  Class<?>
        fieldAttributes.getDeclaredType();               //  Type
        fieldAttributes.getDeclaringClass();             //  Type<?>
        fieldAttributes.getName();                       //  String
        int modifier = 0;
        fieldAttributes.hasModifier(modifier);           //  boolean
        
        Type listParmeterizedType = new TypeToken<List<String>>() {}.getType();
    }
    
    public void test_Gson(){
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
        /** ʵ���� **/
        System.out.println("ʵ����");
        MyType myType = new MyType();
        myType.id = 1;
        myType.title = "haha";

        String json = gson.toJson(myType, MyType.class);
        //String json = gson.toJson(myType); // Ҳ���Բ�ָ�����ͣ������ʵ����Ļ�������ֱ�ӻ������ myType.getClass().getSimpleName()
        System.out.println(json);
        
        MyType myType2 = gson.fromJson(json, MyType.class); // ��Ҫָ������
        System.out.println(myType2.id);
        System.out.println(myType2.title);
        
        /** ���� _��ͨ**/
        System.out.println("���� _��ͨ");
        List<String> list = new ArrayList<String>();
        list.add("one");
        list.add("two");
        String json_list = gson.toJson(list);
        System.out.println(json_list);
        
        Type listType = new TypeToken<List<String>>() {}.getType();
        
        // fromJson ���ַ���ת����ʱ�����ָ������ ������Type �� Class
        List<String> list_string = gson.fromJson(json_list, listType);
        
        for (int i = 0; i < list_string.size(); i++) {
            System.out.println(list_string.get(i));
        }
        
        /** ����_ʵ���� **/
        System.out.println("����_ʵ����");
        List<MyType> myTypeList = new ArrayList<MainActivity.MyType>();
        for (int i = 0; i < 3; i++) {
            MyType myType3 = new MyType();
            myType3.id = i;
            myType3.title = "title" + i;
            myTypeList.add(myType3);
        }
        String myTypeListJson = gson.toJson(myTypeList); // ����ʵ����Ҳ����ָ�����͵İ�,����
        Type myTypeListType = new TypeToken<List<MyType>>() {}.getType();
        // String myTypeListJson = gson.toJson(myTypeList, myTypeListType);
        System.out.println(myTypeListJson);
        
        List<MyType> myTypeList2 = gson.fromJson(myTypeListJson, myTypeListType);
        
        for (int i = 0; i < myTypeList2.size(); i++) {
            System.out.println(myTypeList2.get(i).id);
            System.out.println(myTypeList2.get(i).title);
        }
        
        
    }
    
    public static class MyType {
        int id;
        String title;
    }
    
    // to Json ���Բ�ָ������
    
    public void test_json_stream_parser() { 
        List<MyType> myTypeList = new ArrayList<MyType>();
        for (int i = 0; i < 3; i++) {
            MyType myType3 = new MyType();
            myType3.id = i;
            myType3.title = "haha" + i;
            myTypeList.add(myType3);
        }
        
        PageBean pageBean = new PageBean();
        pageBean.current = 1;
        pageBean.list = myTypeList;
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
        // String pageJson = gson.toJson(pageBean, PageBean.class);
        String pageJson = gson.toJson(pageBean);
        System.out.println(pageJson);
        
        JsonStreamParser parse = new JsonStreamParser(pageJson);
        JsonElement element;    // һ��element ����һ������[]
        synchronized (parse) {
            if (parse.hasNext()) {
                element =  parse.next();
                System.out.println(element.toString());
                System.out.println(">>>>>>>");
            }
        }
        
    }
    
    public static class PageBean {
        int current;
        List list;
    }

}