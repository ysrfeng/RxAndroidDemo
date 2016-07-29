package com.ysr.myrrr;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ysr.myrrr.adapter.MyAdapter;
import com.ysr.myrrr.httputils.HttpPost;
import com.ysr.myrrr.httputils.NewsBean;
import com.ysr.myrrr.utils.APPNetWork;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MyAdapter adapter;
    private TextView tv;
    private Button bt;
    private ListView listView;
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
        bt = (Button) findViewById(R.id.bt);
        listView = (ListView) findViewById(R.id.list);
        bt.setOnClickListener(this);
        if (!isPermitWriteStorage()) {
            requestPermissions(
                    new String[]{Manifest.permission.INTERNET},
                    PRTMISSION_WRITE_EXTERNAL_STORAGE);
            checkNetState();
        }

    }
    /**
     * 检查网络是否连接
     */
    private void checkNetState() {
        if (!APPNetWork.isNetWork(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("网络状态提醒");
            builder.setMessage("当前网络不可用，是否打开网络设置???");
            builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (android.os.Build.VERSION.SDK_INT > 10) {
                        startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                    } else {
                        startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                    }
                }
            });
            builder.create().show();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt:
                HttpPost httpPost = new HttpPost();
                httpPost.getTest(new Subscriber<NewsBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("onError", "连接失败");
                    }

                    @Override
                    public void onNext(NewsBean newsBean) {
//                      tv.setText(newsBean.getV9LG4B3A0().get(1).title.toString());
                        adapter = new MyAdapter(MainActivity.this, newsBean.V9LG4B3A0);
                        listView.setAdapter(adapter);
                    }
                });
                break;
        }
    }
    private final static int PRTMISSION_WRITE_EXTERNAL_STORAGE = 00001;

    //判断sdl版本是否大于23
    private boolean isPermitWriteStorage() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PRTMISSION_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length == 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }

        }
    }

}
