package com.zch.dispatch.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.zch.dispatch.R;
import com.zch.dispatch.base.BaseActivity;
import com.zch.dispatch.tools.PerfHelper;

/**
 * Created by zch on 2020/5/7.
 */

public class Activity_About extends BaseActivity {
    private final static String TAG = "Activity_About";
    private Context context;

    private ImageButton ibtn_back;
    private Button btn_loginout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        context = this;
        initView();
        initEvent();
    }

    private void initView(){
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        btn_loginout = (Button) findViewById(R.id.btn_loginout);
    }

    private void initEvent(){
        ibtn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_loginout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               loginOut();
            }
        });
    }

    private void loginOut(){
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("确认退出系统？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //清空token
                        PerfHelper.setInfo("token", "");
                        finish();
                        System.exit(0);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
