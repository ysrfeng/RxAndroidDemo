package com.ysr.myrrr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ysr.myrrr.R;
import com.ysr.myrrr.httputils.NewsBean;

import java.util.List;

/**
 * Created by Administrator on 2016/7/15.
 */
public class MyAdapter extends BaseAdapter {
    private Context context;
    private List<NewsBean.V9LG4B3A0Bean> list;

    public List<NewsBean.V9LG4B3A0Bean> getList() {
        return list;
    }

    public void setList(List<NewsBean.V9LG4B3A0Bean> list) {
        this.list = list;
    }

    public MyAdapter(Context context, List<NewsBean.V9LG4B3A0Bean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (v == null) {
            holder = new ViewHolder();
            v = LayoutInflater.from(context).inflate(R.layout.item, viewGroup, false);
            holder.img = (ImageView) v.findViewById(R.id.img);
            holder.title = (TextView) v.findViewById(R.id.title2);
            holder.description = (TextView) v.findViewById(R.id.details);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        holder.title.setText(list.get(position).getTitle());//标题
        holder.description.setText(list.get(position).getDescription());
        String cover = list.get(position).getCover();
        //预加载缩略图
        Glide.with(context)
                .load(cover)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder.img);
        return v;
    }

    class ViewHolder {
        private ImageView img;
        public TextView title;
        public TextView description;

    }
}
