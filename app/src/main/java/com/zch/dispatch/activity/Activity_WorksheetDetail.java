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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zch.dispatch.R;
import com.zch.dispatch.base.BaseActivity;
import com.zch.dispatch.base.BaseCallbackListener;
import com.zch.dispatch.base.Configs;
import com.zch.dispatch.bean.WorksheetInfo;
import com.zch.dispatch.fragment.Fragment_Finish;
import com.zch.dispatch.fragment.Fragment_Todo;
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
 */

public class Activity_WorksheetDetail extends BaseActivity implements BaseCallbackListener,EasyPermissions.PermissionCallbacks{
    private final static String TAG = "Activity_WorksheetDetail";
    private Context context;

    private ImageButton ibtn_back;
    private TextView tv_name, tv_tel, tv_addr, tv_content, tv_remark, tv_addtime, tv_reserve, tv_cost, tv_status;
    private Button btn_finish, btn_update;
    private TextView tv_finishtime, tv_lastline;
    private LinearLayout ll_finishtime;

    private WorksheetInfo info ;

    public static ReceiveHandler receiveHandler;
    private final static int HANDLE_REQ_DATA = 0;
    private final static int HANDLER_GETDATA_SUCCESS = 1;
    private final static int HANDLER_GETDATA_FAIL = 2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worksheetdetail);
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
        tv_reserve = (TextView) findViewById(R.id.tv_reserve);
        tv_cost = (TextView) findViewById(R.id.tv_cost);
        tv_status = (TextView) findViewById(R.id.tv_status);
        btn_finish = (Button) findViewById(R.id.btn_finish);
        btn_update = (Button) findViewById(R.id.btn_update);

        tv_finishtime = (TextView) findViewById(R.id.tv_finishtime);
        tv_lastline = (TextView) findViewById(R.id.tv_lastline);
        ll_finishtime = (LinearLayout) findViewById(R.id.ll_finishtime);
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

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Activity_WorksheetUpdate.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("info", info);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
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
                tv_reserve.setText( dealNull(info.getReservetime()));
                tv_cost.setText(info.getCost() + "元");
                tv_status.setText(WorksheetInfo.getState(info.getStatus()));
                tv_finishtime.setText(dealNull(info.getDealtime()));
                if (info.getStatus().equals("0")){
                    tv_status.setTextColor(context.getResources().getColor(R.color.blue));
                }else if (info.getStatus().equals("1")){
                    tv_status.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
                }else if (info.getStatus().equals("2")){
                    tv_status.setTextColor(context.getResources().getColor(R.color.green));
                }
                if (info.getStatus().equals("2")){ //已完成和已结算
                    btn_finish.setVisibility(View.GONE);
                    btn_update.setVisibility(View.GONE);
                    ll_finishtime.setVisibility(View.VISIBLE);
                    tv_lastline.setVisibility(View.VISIBLE);
                }else {
                    ll_finishtime.setVisibility(View.GONE);
                    tv_lastline.setVisibility(View.GONE);
                    btn_finish.setVisibility(View.VISIBLE);
                    btn_update.setVisibility(View.VISIBLE);
                }
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
        js.put("orderId", info.getId());
        basePost(Configs.Send_Worksheet, js, 0, this);
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
                    if (Fragment_Finish.receiveHandler != null){
                        Fragment_Finish.receiveHandler.sendEmptyMessage(Fragment_Finish.HANDLER_REFRESH);
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
