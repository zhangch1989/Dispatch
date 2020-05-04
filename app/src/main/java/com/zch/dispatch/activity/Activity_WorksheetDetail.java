package com.zch.dispatch.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Html;
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
import com.zch.dispatch.pullload.PullToRefreshLayout;
import com.zch.dispatch.tools.DialogUtils;
import com.zch.dispatch.tools.MLog;
import com.zch.dispatch.tools.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zch on 2020/5/4.
 */

public class Activity_WorksheetDetail extends BaseActivity implements BaseCallbackListener{
    private final static String TAG = "Activity_WorksheetDetail";
    private Context context;

    private ImageButton ibtn_back;
    private TextView tv_name, tv_tel, tv_addr, tv_content, tv_remark, tv_addtime, tv_area, tv_owner, tv_status;
    private Button btn_finish;

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
        tv_area = (TextView) findViewById(R.id.tv_area);
        tv_owner = (TextView) findViewById(R.id.tv_owner);
        tv_status = (TextView) findViewById(R.id.tv_status);
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
                callPhone(tv_tel.getText().toString());
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
                tv_remark.setText(info.getRemark());
                tv_area.setText(info.getAreaname());
                tv_owner.setText(info.getOwner());
                tv_status.setText(WorksheetInfo.getState(info.getStatus()));
                if (info.getStatus().equals("2") || info.getStatus().equals("3")){ //已完成和已结算
                    btn_finish.setVisibility(View.GONE);
                }else {
                    btn_finish.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    /**
     * 拨打电话（跳转到拨号界面，用户手动点击拨打）
     *
     * @param phoneNum 电话号码
     */
    public void callPhone(final String phoneNum) {
        new  AlertDialog.Builder(context)
                .setTitle("提示" )
                .setIcon(android.R.drawable.ic_dialog_info)
                .setMessage("是否确认拨打电话？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        Uri data = Uri.parse("tel:" + phoneNum);
                        intent.setData(data);
                        startActivity(intent);
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
            String mess = js.optString("msg","");

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
}
