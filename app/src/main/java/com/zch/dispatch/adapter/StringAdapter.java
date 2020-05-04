package com.zch.dispatch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zch.dispatch.R;

import java.util.List;

/**
 * Created by zch on 2020/5/4.
 */

public class StringAdapter extends BaseAdapter {
    private List<String> datalist;
    private Context context;

    public StringAdapter(Context context, List<String> data){
        this.context = context;
        this.datalist = data;
    }

    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public Object getItem(int position) {
        return datalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if(null == view){
            view = LayoutInflater.from(context).inflate(R.layout.item_sp_area, null);
            holder = new ViewHolder();
            holder.tv = (TextView) view.findViewById(R.id.tv);

            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        holder.tv.setText(datalist.get(position));
        return view;
    }

    private class ViewHolder{
        TextView tv;
    }
}
