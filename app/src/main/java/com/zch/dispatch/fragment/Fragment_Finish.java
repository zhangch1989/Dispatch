package com.zch.dispatch.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.zch.dispatch.R;
import com.zch.dispatch.activity.Activity_Login;
import com.zch.dispatch.activity.Activity_WorksheetDetail;
import com.zch.dispatch.activity.MainActivity;
import com.zch.dispatch.adapter.WorksheetInfoAdapter;
import com.zch.dispatch.base.BaseCallbackListener;
import com.zch.dispatch.base.BaseFragment;
import com.zch.dispatch.base.Configs;
import com.zch.dispatch.bean.WorksheetInfo;
import com.zch.dispatch.tools.DialogUtils;
import com.zch.dispatch.tools.MLog;
import com.zch.dispatch.tools.PerfHelper;
import com.zch.dispatch.tools.ToastUtils;
import com.zch.mylibrary.pullload.PullToRefreshLayout;
import com.zch.mylibrary.pullload.PullableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Zch on 2020/5/11 10:00.
 */

public class Fragment_Finish extends BaseFragment implements BaseCallbackListener{
    private final static String TAG = "Fragment_Finish";
    private Context context;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        receiveHandler = new ReceiveHandler();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finish, null);
        initView(view);
        initEvent();
        refreshLayout.autoRefresh();
        return view;
    }

    private void initView(View view){
        listview = (PullableListView) view.findViewById(R.id.listview);
        refreshLayout = (PullToRefreshLayout) view.findViewById(R.id.refresh_view);

        adapter = new WorksheetInfoAdapter(context, datalist);
        listview.setAdapter(adapter);
    }

    private void initEvent(){
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
        js.put("pageSize", pagenum);
        js.put("currentPage", page);
        baseGet(Configs.Get_List, PerfHelper.getStringData("userid"), "2", 0, this);
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
                    info.setCost(temp.optString("actualFee"));
                    info.setReservetime(temp.optString("appointDate"));
                    info.setStatus(temp.optString("state"));
                    info.setDealtime(temp.optString("finishDate"));
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
    public static void callPhoneTask(Context context, String phonenum){
        if (EasyPermissions.hasPermissions(context, Manifest.permission.CALL_PHONE)) {
            // Have permission, do the thing!
            MainActivity.docallPhone(context, phonenum);
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(context, "此功能需要申请拨打电话权限。",
                    11, Manifest.permission.CALL_PHONE);
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
            String flag = js.optString("code");
            if (flag.equals(Configs.NO_ACCESS)){//token失效，跳转至登录界面
                ToastUtils.showToast(context, "登录失效，请重新登录");
                PerfHelper.setInfo("token", "");
                Intent intent = new Intent(context, Activity_Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                MainActivity.instance.finish();
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
}
