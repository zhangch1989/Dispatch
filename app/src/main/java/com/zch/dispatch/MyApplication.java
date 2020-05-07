package com.zch.dispatch;

import android.app.Application;
import android.os.Handler;

import com.zch.dispatch.tools.DialogUtils;
import com.zch.dispatch.tools.PerfHelper;
import com.zhy.http.okhttp.OkHttpUtils;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by zch on 2020/5/4.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DialogUtils.h = new Handler();  //初始化工具消息
        PerfHelper.getPerferences(this);
        initOkHttpClient();
    }
    private void initOkHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS) //链接超时
                .readTimeout(10000L,TimeUnit.MILLISECONDS) //读取超时
                .proxy(Proxy.NO_PROXY)
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }
}
