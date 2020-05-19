package com.zch.dispatch.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zch.dispatch.R;
import com.zch.dispatch.base.BaseActivity;
import com.zch.dispatch.base.BaseCallbackListener;
import com.zch.dispatch.base.Configs;
import com.zch.dispatch.bean.WorksheetInfo;
import com.zch.dispatch.datetime.TimeConfig;
import com.zch.dispatch.fragment.Fragment_New;
import com.zch.dispatch.fragment.Fragment_Todo;
import com.zch.dispatch.tools.DateUtils;
import com.zch.dispatch.tools.DialogUtils;
import com.zch.dispatch.tools.MLog;
import com.zch.dispatch.tools.PerfHelper;
import com.zch.dispatch.tools.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by zch on 2020/5/4.
 * 预约界面
 */

public class Activity_WorksheetOrder extends BaseActivity implements BaseCallbackListener,EasyPermissions.PermissionCallbacks{
    private final static String TAG = "Activity_WorksheetDetail";
    private Context context;

    private ImageButton ibtn_back;
    private TextView tv_name, tv_tel, tv_addr, tv_content,  tv_addtime, tv_cost, tv_remark;
    private EditText et_reserve;
    private Button btn_finish;

    private WorksheetInfo info ;

    public static ReceiveHandler receiveHandler;
    private final static int HANDLE_REQ_DATA = 0;
    private final static int HANDLER_GETDATA_SUCCESS = 1;
    private final static int HANDLER_GETDATA_FAIL = 2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worksheetorder);
        context = this;
        receiveHandler = new ReceiveHandler();
        initView();
        initEvent();
        initData();
    }

    private void initView(){
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_tel = (TextView) findViewById(R.id.tv_tel);
        tv_addr = (TextView) findViewById(R.id.tv_addr);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_remark = (TextView) findViewById(R.id.tv_remark);
        tv_addtime = (TextView) findViewById(R.id.tv_addtime);
        et_reserve = (EditText) findViewById(R.id.ed_reserve);
        tv_cost = (TextView) findViewById(R.id.tv_cost);
        btn_finish = (Button) findViewById(R.id.btn_finish);
    }

    private void initEvent(){
        ibtn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiveHandler.sendEmptyMessage(HANDLE_REQ_DATA);
            }
        });

        tv_tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPhoneTask();
            }
        });

        et_reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String time = et_reserve.getText().toString();
                if(time.equals("")){
                    time = DateUtils.getNowDatetime();
                }
                DateUtils.setDatetime(context, et_reserve, time, TimeConfig.YEAR_MONTH_DAY_HOUR_MINUTE);
            }
        });
    }

    private void initData(){
        Bundle bundle = getIntent().getExtras();
        if (null != bundle){
            info = (WorksheetInfo) bundle.getSerializable("info");
            if (null != info){
                tv_name.setText(info.getUname());
                tv_tel.setText(Html.fromHtml("<u>"+ info.getTelphone() + "</u>"));
                tv_addr.setText(info.getAddr());
                tv_content.setText(info.getContent());
                tv_addtime.setText(info.getAddtime());
                tv_remark.setText(dealNull(info.getRemark()));
                et_reserve.setText(dealNull(info.getReservetime()));
                tv_cost.setText(info.getCost() + "元");
            }
        }
    }

    private String dealNull(String data){
        if (data == null || data.equals("null") || TextUtils.isEmpty(data)){
            return "";
        }

        return data;
    }

    @AfterPermissionGranted(11)
    public void callPhoneTask(){
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CALL_PHONE)) {
            // Have permission, do the thing!
            MainActivity.docallPhone(context, tv_tel.getText().toString());
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, "此功能需要申请拨打电话权限。",
                    11, Manifest.permission.CALL_PHONE);
        }
    }

    private void sendData() throws Exception{
        JSONObject js = new JSONObject();
        js.put("id", info.getId());
        js.put("appointDate", et_reserve.getText().toString().trim() + ":00");
        basePost(Configs.Order_Worksheet, js, 0, this);
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
            String flag = js.optString("code");
            if (flag.equals(Configs.NO_ACCESS)){//token失效，跳转至登录界面
                ToastUtils.showToast(context, "登录失效，请重新登录");
                PerfHelper.setInfo("token", "");
                Intent intent = new Intent(context, Activity_Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }else {

                Message msg = new Message();
                msg.what = HANDLER_GETDATA_FAIL;
                Bundle bundle = new Bundle();
                bundle.putString("mess", mess);
                msg.setData(bundle);
                receiveHandler.sendMessage(msg);
            }
        }catch (JSONException e ){
            MLog.d(TAG, "getdata error "+ e);
            e.printStackTrace();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        callPhoneTask();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
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
                    ToastUtils.showToast(context, "工单状态修改成功！");
                    if (Fragment_Todo.receiveHandler != null){
                        Fragment_Todo.receiveHandler.sendEmptyMessage(Fragment_Todo.HANDLER_REFRESH);
                    }
                    if (Fragment_New.receiveHandler != null){
                        Fragment_New.receiveHandler.sendEmptyMessage(Fragment_New.HANDLER_REFRESH);
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
}
