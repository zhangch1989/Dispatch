package com.zch.dispatch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zch.dispatch.R;
import com.zch.dispatch.bean.WorksheetInfo;

import java.util.List;

/**
 * Created by zch on 2020/5/3.
 */

public class WorksheetInfoAdapter extends BaseAdapter {
    Context context;
    private LayoutInflater inflater;
    private List<WorksheetInfo> datalist;

    public WorksheetInfoAdapter(Context context, List<WorksheetInfo> datalist){
        this.context = context;
        this.datalist = datalist;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public Object getItem(int i) {
        return datalist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(null == view){
            view = inflater.inflate(R.layout.item_worksheetinfo, null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) view.findViewById(R.id.tv_username);
            holder.tv_tel = (TextView) view.findViewById(R.id.tv_tel);
            holder.tv_content = (TextView) view.findViewById(R.id.tv_content);
            holder.tv_addr = (TextView) view.findViewById(R.id.tv_addr);
            holder.tv_time = (TextView) view.findViewById(R.id.tv_time);
            holder.tv_status = (TextView) view.findViewById(R.id.tv_status);

            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        WorksheetInfo info = datalist.get(i);
        holder.tv_name.setText(info.getUname());
        holder.tv_tel.setText(info.getTelphone());
        holder.tv_content.setText(info.getContent());
        holder.tv_addr.setText(info.getAddr());
        holder.tv_time.setText(info.getAddtime());
        holder.tv_status.setText(WorksheetInfo.getState(info.getStatus()));

        return view;
    }

    private class ViewHolder{
        TextView tv_name;
        TextView tv_tel;
        TextView tv_content;
        TextView tv_addr;
        TextView tv_time;
        TextView tv_status;
    }

    public void Refresh(List<WorksheetInfo> list){
        this.datalist = list;
        notifyDataSetChanged();
    }

}
