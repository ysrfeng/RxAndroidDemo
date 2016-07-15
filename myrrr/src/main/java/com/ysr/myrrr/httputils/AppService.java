package com.ysr.myrrr.httputils;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;
/*
* author yang
* created 2016/7/14 11:09
*/
public interface AppService {
    @GET("nc/video/list/V9LG4B3A0/y/0-10.html")
         Observable<NewsBean> getTest();
    //  Observable<NewsBean>getTest(@Query("h")String p);
}
