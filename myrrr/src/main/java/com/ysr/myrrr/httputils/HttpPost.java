package com.ysr.myrrr.httputils;

import rx.Subscriber;

/**
 * Created by Administrator on 2016/7/14.
 */
public class HttpPost {
    HttpRetrofit httpRetrofit;

    public HttpPost() {
        httpRetrofit = MyApplication.getInstance().httpRetrofit;
    }
    public void getTest(Subscriber<NewsBean> subscriber) {
        httpRetrofit.httpSubscribe(httpRetrofit.gerAppService().getTest(), subscriber);
    }
}
