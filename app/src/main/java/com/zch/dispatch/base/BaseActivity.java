package com.zch.dispatch.base;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.zch.dispatch.tools.MLog;
import com.zch.dispatch.tools.PerfHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by zch on 2020/5/3.
 */

public class BaseActivity extends Activity {
    private final static String TAG = "BaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION  //该参数指布局能延伸到navigationbar，我们场景中不应加这个参数
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
////            window.setNavigationBarColor(Color.TRANSPARENT); //设置navigationbar颜色为透明
//        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            //透明状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }

    }


    public void basePost(String url, JSONObject data, int code, final BaseCallbackListener callback) throws JSONException {
        MLog.d(TAG, "请求地址 ：" + url.toString());

        JSONObject js = new JSONObject();
        js.put("data", data);
//        js.put("ip", "android");
//        js.put("sid", "sid");
//        js.put("timestamp", 0);
        MLog.d(TAG, "请求参数 ：" + js.toString());
        OkHttpUtils.postString()
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(js.toString())
                .addHeader("token", PerfHelper.getStringData("token"))
                .id(code)
                .url(url)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                MLog.e(TAG, "error = " + e.toString());
                if (e instanceof IOException){
                    callback.onIOException(id, (IOException) e);
                }else {
                    callback.onError(id, e.toString());
                }
            }

            @Override
            public void onResponse(String response, int id) {
                MLog.d(TAG, "response: " + response.toString());
                try {
                    JSONObject js = new JSONObject(response);
                    String flag = js.optString("code");

                    if (flag.equals(Configs.SUCCESS)) {
                        callback.onComplete(id, response.toString());
                    } else {
                        callback.onError(id, response);
                    }

                } catch (Exception e) {
                    // TODO: handle exception
                    MLog.d(TAG, "e: " + e.toString());
                    e.printStackTrace();
                }
            }
        });
    }
    public void baseGet(String url, String userid, int code, final BaseCallbackListener callback) throws JSONException {
        MLog.d(TAG, "请求地址 ：" + url.toString());

        OkHttpUtils.get().addParams("workerId", userid)
                .addHeader("token", PerfHelper.getStringData("token"))
                .id(code)
                .url(url)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                MLog.e(TAG, "error = " + e.toString());
                if (e instanceof IOException){
                    callback.onIOException(id, (IOException) e);
                }else {
                    callback.onError(id, e.toString());
                }
            }

            @Override
            public void onResponse(String response, int id) {
                MLog.d(TAG, "response: " + response.toString());
                try {
                    JSONObject js = new JSONObject(response);
                    String flag = js.optString("code");

                    if (flag.equals(Configs.SUCCESS)) {
                        callback.onComplete(id, response.toString());
                    } else {
                        callback.onError(id, response);
                    }

                } catch (Exception e) {
                    // TODO: handle exception
                    MLog.d(TAG, "e: " + e.toString());
                    e.printStackTrace();
                }
            }
        });
    }

}
