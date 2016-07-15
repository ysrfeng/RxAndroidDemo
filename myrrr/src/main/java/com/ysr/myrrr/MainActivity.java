package com.ysr.myrrr;

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

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MyAdapter adapter;
    private TextView tv;
    private Button bt;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
        bt = (Button) findViewById(R.id.bt);
        listView = (ListView) findViewById(R.id.list);
        bt.setOnClickListener(this);

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
}
