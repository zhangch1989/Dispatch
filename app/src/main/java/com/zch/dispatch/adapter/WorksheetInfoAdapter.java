package com.zch.dispatch.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zch.dispatch.R;
import com.zch.dispatch.activity.MainActivity;
import com.zch.dispatch.bean.WorksheetInfo;

import java.util.List;

/**
 * Created by zch on 2020/5/3.
 */

public class WorksheetInfoAdapter extends BaseAdapter {
    Context context;
    private LayoutInflater inflater;
    private List<WorksheetInfo> datalist;

    public WorksheetInfoAdapter(Context context, List<WorksheetInfo> datalist) {
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
        if (null == view) {
            view = inflater.inflate(R.layout.item_worksheetinfo, null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) view.findViewById(R.id.tv_username);
            holder.tv_tel = (TextView) view.findViewById(R.id.tv_tel);
            holder.tv_content = (TextView) view.findViewById(R.id.tv_content);
            holder.tv_addr = (TextView) view.findViewById(R.id.tv_addr);
            holder.tv_time = (TextView) view.findViewById(R.id.tv_time);
            holder.tv_status = (TextView) view.findViewById(R.id.tv_status);
            holder.ll_reserve = (LinearLayout) view.findViewById(R.id.ll_reserve);
            holder.tv_reservetime = (TextView) view.findViewById(R.id.tv_reservetime);
            holder.tv_reservestr = (TextView) view.findViewById(R.id.tv_reservestr);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final WorksheetInfo info = datalist.get(i);
        holder.tv_name.setText(info.getUname());
        holder.tv_tel.setText(Html.fromHtml("<u>" + info.getTelphone() + "</u>"));
        holder.tv_content.setText(info.getContent());
        holder.tv_addr.setText("地址："+info.getAddr());
        holder.tv_time.setText(info.getAddtime());
        holder.tv_status.setText(WorksheetInfo.getState(info.getStatus()));
        if (info.getStatus().equals("0")){
            holder.ll_reserve.setVisibility(View.GONE);
            holder.tv_status.setTextColor(context.getResources().getColor(R.color.blue));
        }else if (info.getStatus().equals("1")){
            holder.tv_reservestr.setText("预约上门时间：");
            holder.tv_reservetime.setText( dealNull(info.getReservetime()));
            holder.ll_reserve.setVisibility(View.VISIBLE);
            holder.tv_status.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
        }else if (info.getStatus().equals("2")){
            holder.tv_reservestr.setText("完成时间：");
            holder.tv_reservetime.setText( dealNull(info.getDealtime()));
            holder.ll_reserve.setVisibility(View.VISIBLE);
            holder.tv_status.setTextColor(context.getResources().getColor(R.color.green));
        }

        holder.tv_tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.callPhoneTask(context, info.getTelphone());
            }
        });

        return view;
    }

    private class ViewHolder {
        TextView tv_name;
        TextView tv_tel;
        TextView tv_content;
        TextView tv_addr;
        TextView tv_time;
        TextView tv_status;
        TextView tv_reservetime;
        TextView tv_reservestr;
        LinearLayout ll_reserve;
    }

    public void Refresh(List<WorksheetInfo> list) {
        this.datalist = list;
        notifyDataSetChanged();
    }

    private String dealNull(String data){
        if (data == null || data.equals("null") || TextUtils.isEmpty(data)){
            return "";
        }

        return data;
    }

}
