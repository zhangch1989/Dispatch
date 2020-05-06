package com.zch.dispatch.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.zch.dispatch.R;
import com.zch.dispatch.adapter.StringAdapter;
import com.zch.dispatch.base.BaseActivity;
import com.zch.dispatch.base.BaseCallbackListener;
import com.zch.dispatch.base.Configs;
import com.zch.dispatch.tools.DialogUtils;
import com.zch.dispatch.tools.MLog;
import com.zch.dispatch.tools.PerfHelper;
import com.zch.dispatch.tools.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zch on 2020/5/4.
 */

public class Activity_WorksheetAdd extends BaseActivity implements BaseCallbackListener {
    private final static String TAG = "Activity_WorksheetAdd";
    private Context context;

    private ImageButton ibtn_back;
    private EditText et_name, et_tel, et_addr, et_content, et_remark, et_owner;
    private Spinner sp_city, sp_area;
    private Button btn_submit;

    private List<String> citys = new ArrayList<>();
    private List<String> areas = new ArrayList<>();

    public static ReceiveHandler receiveHandler;
    private final static int HANDLE_REQ_DATA = 0;
    private final static int HANDLER_GETDATA_SUCCESS = 1;
    private final static int HANDLER_GETDATA_FAIL = 2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worksheetadd);
        context = this;
        receiveHandler = new ReceiveHandler();
        initView();
        initEvent();
        initData();
    }

    private void initView(){
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        et_name = (EditText) findViewById(R.id.ed_name);
        et_tel = (EditText) findViewById(R.id.ed_tel);
        et_addr = (EditText) findViewById(R.id.ed_addr);
        et_content = (EditText) findViewById(R.id.ed_content);
        et_owner = (EditText) findViewById(R.id.ed_owner);
        et_remark = (EditText) findViewById(R.id.ed_remark);
        sp_city = (Spinner) findViewById(R.id.sp_city);
        sp_area = (Spinner) findViewById(R.id.sp_area);
        btn_submit = (Button) findViewById(R.id.btn_submit);
    }

    private void initEvent(){
        ibtn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidate()) {
                    receiveHandler.sendEmptyMessage(HANDLE_REQ_DATA);
                }
            }
        });
    }

    private void initData(){
        citys.clear();
        areas.clear();

        citys.add("重庆市");

        areas.add("请选择区域");
        areas.add("渝中区");
        areas.add("大渡口区");
        areas.add("江北区");
        areas.add("沙坪坝区");
        areas.add("九龙坡区");
        areas.add("南岸区");
        areas.add("北碚区");
        areas.add("綦江区");
        areas.add("大足区");
        areas.add("渝北区");
        areas.add("巴南区");
        areas.add("万州区");
        areas.add("涪陵区");
        areas.add("黔江区");
        areas.add("长寿区");
        areas.add("江津区");
        areas.add("合川区");
        areas.add("永川区");
        areas.add("南川区");
        areas.add("璧山区");
        areas.add("铜梁区");
        areas.add("潼南区");
        areas.add("荣昌区");
        areas.add("开州区");
        areas.add("梁平区");
        areas.add("武隆区");
        areas.add("城口县");
        areas.add("丰都县");
        areas.add("垫江县");
        areas.add("忠县");
        areas.add("云阳县");
        areas.add("奉节县");
        areas.add("巫山县");
        areas.add("巫溪县");
        areas.add("石柱土家族自治县");
        areas.add("秀山土家族苗族自治县");
        areas.add("酉阳土家族苗族自治县");
        areas.add("彭水苗族土家族自治县");

        StringAdapter city_adapter = new StringAdapter(context, citys);
        StringAdapter area_adapter = new StringAdapter(context, areas);
        sp_city.setAdapter(city_adapter);
        sp_area.setAdapter(area_adapter);
    }

    private void sendData() throws Exception{
        JSONObject js = new JSONObject();
        js.put("customerName", et_name.getText().toString().trim());
        js.put("customerMobile", et_tel.getText().toString().trim());
        String addr = sp_city.getSelectedItem().toString() + sp_area.getSelectedItem().toString() + et_addr.getText().toString().trim();
        js.put("address", addr);
        js.put("area", sp_area.getSelectedItem().toString());
        js.put("director", et_owner.getText().toString().trim());
        js.put("needs", et_content.getText().toString().trim());
        js.put("remarks", et_remark.getText().toString().trim());
        js.put("state", "0");
        js.put("workerId", PerfHelper.getStringData("userid"));
        basePost(Configs.Add_Worksheet, js, 0, this);
    }

    private boolean isValidate(){
        if(null == et_name.getText() || TextUtils.isEmpty(et_name.getText().toString())){
            ToastUtils.showToast(context, "客户姓名不能为空！");
            return  false;
        }
        if(null == et_tel.getText() || TextUtils.isEmpty(et_tel.getText().toString())){
            ToastUtils.showToast(context, "联系电话不能为空！");
            return  false;
        }
        if (sp_area.getSelectedItemPosition() == 0){
            ToastUtils.showToast(context, "请选择区域！");
            return  false;
        }
        if(null == et_addr.getText() || TextUtils.isEmpty(et_addr.getText().toString())){
            ToastUtils.showToast(context, "详细地址不能为空！");
            return  false;
        }
        if(null == et_content.getText() || TextUtils.isEmpty(et_content.getText().toString())){
            ToastUtils.showToast(context, "客户要求不能为空！");
            return  false;
        }

        return true;
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
                    ToastUtils.showToast(context, "工单提交成功！");
                    if (MainActivity.instance != null){
                        MainActivity.receiveHandler.sendEmptyMessage(MainActivity.HANDLER_REFRESH);
                    }
                    finish();
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (Activity_WorksheetAdd.this.getCurrentFocus() != null) {
                if (Activity_WorksheetAdd.this.getCurrentFocus().getWindowToken() != null) {
                    imm.hideSoftInputFromWindow(Activity_WorksheetAdd.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        return super.onTouchEvent(event);
    }
}
