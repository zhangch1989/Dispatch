package com.zch.dispatch.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.zch.dispatch.R;
import com.zch.dispatch.adapter.WorksheetInfoAdapter;
import com.zch.dispatch.base.BaseActivity;
import com.zch.dispatch.base.BaseCallbackListener;
import com.zch.dispatch.base.Configs;
import com.zch.dispatch.bean.WorksheetInfo;
import com.zch.dispatch.pullload.PullToRefreshLayout;
import com.zch.dispatch.pullload.PullableListView;
import com.zch.dispatch.tools.DialogUtils;
import com.zch.dispatch.tools.MLog;
import com.zch.dispatch.tools.PerfHelper;
import com.zch.dispatch.tools.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity implements BaseCallbackListener, EasyPermissions.PermissionCallbacks{
    private final static String TAG = "MainActivity";
    private long firstTime = 0; //记录两次返回按钮的时间

    private Context context;
    public static MainActivity instance;

    private ImageButton ibtn_add;
    private PullableListView listview;
    private PullToRefreshLayout refreshLayout;

    private WorksheetInfoAdapter adapter;
    private List<WorksheetInfo> datalist = new ArrayList<>();

    private int page = 1;
    private int pagenum = 20;
    private int returnpage = -1;
    private boolean isloading = false;
    public static ReceiveHandler receiveHandler;
    private final static int HANDLE_REQ_DATA = 0;
    private final static int HANDLER_GETDATA_SUCCESS = 1;
    private final static int HANDLER_GETDATA_FAIL = 2;
    public final static int HANDLER_REFRESH = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        instance = this;
        receiveHandler = new ReceiveHandler();

        initView();
        initEvent();
        refreshLayout.autoRefresh();
    }

    private void initView(){
        ibtn_add = (ImageButton) findViewById(R.id.ibtn_add);
        listview = (PullableListView) findViewById(R.id.listview);
        refreshLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);

        adapter = new WorksheetInfoAdapter(context, datalist);
        listview.setAdapter(adapter);
    }

    private void initEvent(){
        ibtn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, Activity_WorksheetAdd.class));
            }
        });
        refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                page = 1;
                returnpage = -1;
                receiveHandler.sendEmptyMessage(HANDLE_REQ_DATA);
                isloading = true;
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                if ( returnpage != -1 && !isloading) {
                    ++page;
                    isloading = true;
                    receiveHandler.sendEmptyMessage(HANDLE_REQ_DATA);
                }else {
                    ToastUtils.showToast(context, "已是最底");
                    refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            }
        });


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    WorksheetInfo info = datalist.get(i);
                    Intent intent = new Intent(context, Activity_WorksheetDetail.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("info", info);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void getData() throws Exception{
        JSONObject js = new JSONObject();
        js.put("workerId ", PerfHelper.getStringData("userid"));
        baseGet(Configs.Get_List, PerfHelper.getStringData("userid"), 0, this);
    }

    private void dealdata(String str){
        try{
            JSONObject js = new JSONObject(str);
            JSONArray ja = js.optJSONArray("data");
            if(null != ja){
                int length = ja.length();
                if (page == 1) {
                    datalist.clear();
                }
                if (length < pagenum){
                    returnpage = -1;
                }else {
                    returnpage = page;
                }
                for (int i = 0; i < length; i++){
                    JSONObject temp = ja.getJSONObject(i);
                    WorksheetInfo info = new WorksheetInfo();
                    info.setId(temp.optString("id"));
                    info.setUname(temp.optString("customerName"));
                    info.setTelphone(temp.optString("customerMobile"));
                    info.setAddr(temp.optString("address"));
                    info.setContent(temp.optString("needs"));
                    info.setAreaname(temp.optString("area"));
                    info.setDeal_user(temp.optString("worker"));
                    info.setDeal_tel(temp.optString("workerMobile"));
                    info.setOwner(temp.optString("director"));
                    info.setAddtime(temp.optString("createTime"));
                    info.setDealtime(temp.optString(""));
                    info.setStatus(temp.optString("state"));
                    info.setRemark(temp.optString("remarks"));
                    datalist.add(info);
                }
            }else{
                if (page == 1){
                    datalist.clear();
                }else{
                    returnpage = -1;
                }
            }
        }catch (Exception e){
            MLog.e(TAG, "deal data error "+e);
            e.printStackTrace();
        }

        adapter.Refresh(datalist);
        isloading = false;
        refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
        refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
    }

    @AfterPermissionGranted(11)
    public static void callPhoneTask(Context context,String phonenum){
        if (EasyPermissions.hasPermissions(context, Manifest.permission.CALL_PHONE)) {
            // Have permission, do the thing!
            docallPhone(context, phonenum);
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(context, "此功能需要申请拨打电话权限。",
                    11, Manifest.permission.CALL_PHONE);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (System.currentTimeMillis() - firstTime > 2000){
                ToastUtils.showToast(this, "再按一次退出程序");
                firstTime = System.currentTimeMillis();
            }else {
                finish();
                System.exit(0);
            }
            return true;
        }else{
            return super.onKeyDown(keyCode,event);
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

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
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
                        getData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case HANDLER_GETDATA_SUCCESS:
                    DialogUtils.dismissProcessDialog();
                    String str = msg.getData().getString("str");
                    dealdata(str);

                    break;
                case HANDLER_GETDATA_FAIL:
                    DialogUtils.dismissProcessDialog();
                    String mess = msg.getData().getString("mess");
                    ToastUtils.showToast(context, mess);
                    isloading = false;
                    refreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                    refreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
                    break;
                case HANDLER_REFRESH:
                    try {
                        refreshLayout.autoRefresh();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 拨打电话（）
     *@param context
     * @param phoneNum 电话号码
     */
    public static void docallPhone(final Context context, final String phoneNum) {
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("确认拨打电话："+ phoneNum+"？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callPhone(context, phoneNum);
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


    /**
     * 拨打电话
     * @param phoneNum 电话号码
     */
    @SuppressLint("MissingPermission")
    public static void callPhone(Context context, String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        context.startActivity(intent);

//        //跳转到拨号界面，用户手动点击拨打
//        Intent intent = new Intent(Intent.ACTION_DIAL);
//        Uri data = Uri.parse("tel:" + phoneNum);
//        intent.setData(data);
//        context.startActivity(intent);
    }
}
