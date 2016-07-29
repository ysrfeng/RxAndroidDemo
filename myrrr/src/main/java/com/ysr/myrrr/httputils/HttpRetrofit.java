package com.ysr.myrrr.httputils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.ysr.myrrr.MainActivity;
import com.ysr.myrrr.utils.APPNetWork;
import com.ysr.myrrr.utils.LogUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/7/14.
 */
public class HttpRetrofit {
    public static OkHttpClient okHttpClient;
    private Context context;
    public HttpRetrofit() {
        okHttpClient = getOkHttpClient();
    }

    /**
     * 设置调用OkHttpClient
     */
    public OkHttpClient getOkHttpClient() {
         File file = new File(MyApplication.getInstance().getCacheDir().getAbsolutePath(), "download");
        LogUtils.e("filepath", MyApplication.getInstance().getCacheDir().getAbsolutePath());
        return new OkHttpClient().newBuilder()
                .addInterceptor(getHtttpLoggingInterceptor())
                //缓存这两个都要设置才行
                .addNetworkInterceptor(getNetWorkInterceptor())
                .addInterceptor(getNetWorkInterceptor())
                .addInterceptor(getInterceptor())
            .cache(new Cache(MyApplication.getInstance().getCacheDir(), 10 * 1024 * 1024))

                //     .cache(new Cache(file,1024 * 1024*100))
                .retryOnConnectionFailure(true) //失败重连
                .readTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 设置线程订阅转换
     *
     * @param observable
     * @param subscriber
     * @param <T>
     */
    public <T> void httpSubscribe(Observable<T> observable, Subscriber<T> subscriber) {
        observable.map(new Func1<T, T>() {
            @Override
            public T call(T t) {
                //在这里进行json转换并返回
                return t;
            }
        })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }

    /*
    关联接口
     */
    public AppService gerAppService() {
        return getRetrofit().create(AppService.class);
    }

    /*
    初始化Retrofit
     */
    private Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .client(getOkHttpClient())
                .baseUrl("http://c.m.163.com/")
                // .addConverterFactory(GsonConverterFactory.create())
                   .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        //   Gson myGson = new GsonBuilder().registerTypeAdapterFactory(new SafeTypeAdapterFactory().create());
    }

    //    final static class SafeTypeAdapterFactory implements TypeAdapterFactory {
//
//        @Override
//        public TypeAdapter create(Gson gson, final TypeToken type) {
//            final TypeAdapter delegate = gson.getDelegateAdapter(this,type);
//            return new TypeAdapter() {
//                @Override
//                public void write(JsonWriter out, T value) throws IOException {
//                    try {
//                        delegate.write(out, value);
//                    } catch (IOException e) {
//                        delegate.write(out, null);
//                    }
//                }
//
//                @Override
//                public T read(JsonReader in) throws IOException {
//                    try {
//                        return delegate.read(in);
//                    } catch (IOException e) {
//                        in.skipValue();
//                        return null;
//                    } catch (IllegalStateException e) {
//                        in.skipValue();
//                        return null;
//                    } catch (JsonSyntaxException e) {
//                        in.skipValue();
//                        if(type.getType() instanceof Class){
//                            try {
//                                return (T) ((Class)type.getType()).newInstance();
//                            } catch (Exception e1) {
//
//                            }
//                        }
//                        return null;
//                    }
//                }
//            };
//        }
//    }
    /*
    日志打印
     */
    private static HttpLoggingInterceptor getHtttpLoggingInterceptor() {
        return new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                LogUtils.e("message", message);
            }
        }).setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    //设置连接器 设置缓存
    public Interceptor getNetWorkInterceptor() {

        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                LogUtils.e("APPNetWork","进入000");
                Request request = chain.request();
                LogUtils.e("APPNetWork","进入001");
                if (!APPNetWork.isNetWork(MyApplication.getInstance())) {
                    LogUtils.e("APPNetWork", "进入002");
                    request = request.newBuilder()
                            .cacheControl(CacheControl.FORCE_CACHE)
                            .build();
                } else {
                    LogUtils.e("APPNetWork","进入003");
                }

                Response response = chain.proceed(request);
                if (APPNetWork.isNetWork(MyApplication.getInstance())) {
                    //有网络是设置缓存超时时间0小时
                    int maxAge = 0 * 60;
                    response.newBuilder()
                            .header("Cache-Control", "public, max-age=" + maxAge)
                            // 清除头信息
                            .removeHeader("Pragma")
                            .build();
                }
                else {

                    LogUtils.e("APPNetWork","进入else");
                    int maxStale = 60 * 60 * 24 * 7;
                    response.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            .removeHeader("Pragma")
                            .build();
                }
                LogUtils.e("APPNetWork","进入001");
                return response;
            }
        };
    }

    //拦截网络没网读取缓存
    public Interceptor getInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (!APPNetWork.isNetWork(MyApplication.getInstance())) {
                    request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
                    LogUtils.e("APPNetWork","无网络连接");
                }
                LogUtils.e("APPNetWork","有网络连接");
                return chain.proceed(request);
            }
        };
    }
}
