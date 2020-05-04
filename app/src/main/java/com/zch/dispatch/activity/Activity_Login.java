package com.zch.dispatch.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.zch.dispatch.R;
import com.zch.dispatch.base.BaseActivity;
import com.zch.dispatch.base.BaseCallbackListener;
import com.zch.dispatch.base.Configs;
import com.zch.dispatch.tools.DialogUtils;
import com.zch.dispatch.tools.MLog;
import com.zch.dispatch.tools.PerfHelper;
import com.zch.dispatch.tools.ToastUtils;
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

public class Activity_Login extends BaseActivity implements BaseCallbackListener{
    private final static String TAG = "Activity_Login";
    private Context context;

    private EditText et_username, et_pwd;
    private Button btn_login;

    public static ReceiveHandler receiveHandler;
    private final static int HANDLE_REQ_DATA = 0;
    private final static int HANDLER_GETDATA_SUCCESS = 1;
    private final static int HANDLER_GETDATA_FAIL = 2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        receiveHandler = new ReceiveHandler();
        initView();
        initEvent();
    }

    private void initView(){
        et_username = (EditText) findViewById(R.id.et_username);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        btn_login = (Button) findViewById(R.id.btn_login);

//        et_username.setText(PerfHelper.getStringData("loginuser"));
//        et_pwd.setText(PerfHelper.getStringData("loginpwd"));
        et_username.setText("13222233454");
        et_pwd.setText("3214");
    }

    private void initEvent(){
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidate()){
                    receiveHandler.sendEmptyMessage(HANDLE_REQ_DATA);
                }
            }
        });
    }

    /**
     * 验证用户输入是否有效
     * @return
     */
    private boolean isValidate(){
        if(null == et_username.getText() || TextUtils.isEmpty(et_username.getText().toString())){
            ToastUtils.showToast(context, "请输入用户名！");
            return  false;
        }
        if(null == et_pwd.getText() || TextUtils.isEmpty(et_pwd.getText().toString())){
            ToastUtils.showToast(context, "请输入密码！");
            return  false;
        }

        return  true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (Activity_Login.this.getCurrentFocus() != null) {
                if (Activity_Login.this.getCurrentFocus().getWindowToken() != null) {
                    imm.hideSoftInputFromWindow(Activity_Login.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            System.gc();
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }

    private void sendData() throws Exception{
        JSONObject js = new JSONObject();
        js.put("loginName", et_username.getText().toString().trim());
        js.put("password", et_pwd.getText().toString().trim());
        basePost2(Configs.Send_Login, js, 0, this);
    }

    private void dealData(String str){
        try{
            JSONObject js = new JSONObject(str);
            JSONObject data = js.optJSONObject("data");
            if (null != data){
                PerfHelper.setInfo("userid", data.optString("userId"));
                PerfHelper.setInfo("token", data.optString("token"));
                ToastUtils.showToast(context, "登录成功");

                //记录登录用户信息
                PerfHelper.setInfo("loginuser", et_username.getText().toString().trim());
                PerfHelper.setInfo("loginpwd", et_pwd.getText().toString().trim());

                startActivity(new Intent(context, MainActivity.class));
                finish();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onComplete(int code, String response) {
        switch (code){
            case 0:
                Message msg = new Message();
                msg.what = HANDLER_GETDATA_SUCCESS;
                Bundle bundle = new Bundle();
                bundle.putString("str", response);
                msg.setData(bundle);
                receiveHandler.sendMessage(msg);
                break;
            default:
                break;
        }
    }

    @Override
    public void onIOException(int code, IOException e) {
        MLog.d(TAG, "get data IOException"+e);

        Message msg = new Message();
        msg.what = HANDLER_GETDATA_FAIL;
        Bundle bundle = new Bundle();
        bundle.putString("mess", "数据请求失败！");
        msg.setData(bundle);
        receiveHandler.sendMessage(msg);
    }

    @Override
    public void onError(int code, String response) {
        try{
            JSONObject js = new JSONObject(response);
            String mess = js.optString("message","");

            Message msg = new Message();
            msg.what = HANDLER_GETDATA_FAIL;
            Bundle bundle = new Bundle();
            bundle.putString("mess", mess);
            msg.setData(bundle);
            receiveHandler.sendMessage(msg);
        }catch (JSONException e ){
            MLog.d(TAG, "getdata error "+ e);
            e.printStackTrace();
        }
    }

    public class ReceiveHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANDLE_REQ_DATA:
                    try {
                        sendData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case HANDLER_GETDATA_SUCCESS:
                    DialogUtils.dismissProcessDialog();
                    String str = msg.getData().getString("str");
                    dealData(str);
                    break;
                case HANDLER_GETDATA_FAIL:
                    DialogUtils.dismissProcessDialog();
                    String mess = msg.getData().getString("mess");
                    ToastUtils.showToast(context, mess);
                    break;
                default:
                    break;
            }
        }
    }

    public void basePost2(String url, JSONObject data, int code, final BaseCallbackListener callback) throws JSONException {
        MLog.d(TAG, "请求地址 ：" + url.toString());

        JSONObject js = new JSONObject();
        js.put("data", data);
        MLog.d(TAG, "请求参数 ：" + js.toString());
        OkHttpUtils.postString()
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(js.toString())
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
