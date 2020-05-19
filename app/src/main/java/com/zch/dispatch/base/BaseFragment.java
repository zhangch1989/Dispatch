package com.zch.dispatch.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zch.dispatch.tools.MLog;
import com.zch.dispatch.tools.PerfHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by Zch on 2020/5/11 9:56.
 */

public class BaseFragment extends Fragment {
    private final static String TAG = "BaseFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void basePost(String url, JSONObject data, int code, final BaseCallbackListener callback) throws JSONException {
        MLog.d(TAG, "请求地址 ：" + url.toString());

        JSONObject js = new JSONObject();
        js.put("data", data);
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

    /**
     * 根据用户及查询状态获取列表信息
     * @param url
     * @param userid
     * @param state
     * @param code
     * @param callback
     * @throws JSONException
     */
    public void baseGet(String url, String userid, String state, int code, final BaseCallbackListener callback) throws JSONException {
        MLog.d(TAG, "请求地址 ：" + url.toString());

        OkHttpUtils.get().addParams("workerId", userid)
                .addParams("state", state)
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

    /**
     * 根据预约时间筛选列表
     * @param url
     * @param userid
     * @param state
     * @param time
     * @param code
     * @param callback
     * @throws JSONException
     */
    public void baseGet(String url, String userid, String state, String time, int code, final BaseCallbackListener callback) throws JSONException {
        MLog.d(TAG, "请求地址 ：" + url.toString());

        OkHttpUtils.get().addParams("workerId", userid)
                .addParams("state", state)
                .addParams("startAppointDate", time + " 00:00:00")
                .addParams("endAppointDate", time + " 23:59:59")
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
