package com.ysr.myrrr.httputils;

import android.app.Application;

/**
 * Created by Administrator on 2016/7/15.
 */
public class MyApplication extends Application{
    public static MyApplication application;
    public HttpRetrofit httpRetrofit;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        httpRetrofit = new HttpRetrofit();
    }
    public static MyApplication getInstance(){
        return application;
    }
}
