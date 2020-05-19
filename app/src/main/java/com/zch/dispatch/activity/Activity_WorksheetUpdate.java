package com.zch.dispatch.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.zch.dispatch.R;
import com.zch.dispatch.adapter.StringAdapter;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zch on 2020/5/4.
 * 修改界面
 */

public class Activity_WorksheetUpdate extends BaseActivity implements BaseCallbackListener{
    private final static String TAG = "Activity_WorksheetDetail";
    private Context context;

    private ImageButton ibtn_back;
    private TextView tv_content,  tv_addtime, tv_cost;
    private EditText et_name, et_tel, et_addr;
    private Spinner sp_city, sp_area;
    private EditText et_reserve, et_remark;
    private Button btn_finish;

    private WorksheetInfo info ;
    private List<String> citys = new ArrayList<>();
    private List<String> areas = new ArrayList<>();

    public static ReceiveHandler receiveHandler;
    private final static int HANDLE_REQ_DATA = 0;
    private final static int HANDLER_GETDATA_SUCCESS = 1;
    private final static int HANDLER_GETDATA_FAIL = 2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worksheetupdate);
        context = this;
        receiveHandler = new ReceiveHandler();
        initView();
        initEvent();
        initData();
    }

    private void initView(){
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        et_name = (EditText) findViewById(R.id.et_name);
        et_tel = (EditText) findViewById(R.id.et_tel);
        tv_content = (TextView) findViewById(R.id.tv_content);
        et_remark = (EditText) findViewById(R.id.ed_remark);
        tv_addtime = (TextView) findViewById(R.id.tv_addtime);
        et_reserve = (EditText) findViewById(R.id.ed_reserve);
        tv_cost = (TextView) findViewById(R.id.tv_cost);
        et_addr = (EditText) findViewById(R.id.ed_addr);
        sp_city = (Spinner) findViewById(R.id.sp_city);
        sp_area = (Spinner) findViewById(R.id.sp_area);
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
                et_name.setText(info.getUname());
                et_tel.setText(info.getTelphone() );
                tv_content.setText(info.getContent());
                tv_addtime.setText(info.getAddtime());
                et_remark.setText(dealNull(info.getRemark()));
                et_reserve.setText(dealNull(info.getReservetime()));
                tv_cost.setText(info.getCost() + "元");

                if (info.getAddr() != null) {
                    initAddrData();
                }
            }
        }
    }

    private void initAddrData(){
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

        //拆分区
        String areastr = info.getAddr().substring(3);
        String[] str = null;
        if (areastr.contains("区")){
            str = areastr.split("区");
        }else {
            str = areastr.split("县");
        }
        for (int i = 0; i< areas.size(); i++){
            if (areas.get(i).contains(str[0])){
                sp_area.setSelection(i);
                break;
            }
        }
        et_addr.setText(areastr.substring(str[0].length()+1));
    }

    private String dealNull(String data){
        if (data == null || data.equals("null") || TextUtils.isEmpty(data)){
            return "";
        }

        return data;
    }

    private void sendData() throws Exception{
        JSONObject js = new JSONObject();
        js.put("id", info.getId());
        js.put("customerName", et_name.getText().toString().trim());
        js.put("customerMobile", et_tel.getText().toString().trim());
        String addr = sp_city.getSelectedItem().toString() + sp_area.getSelectedItem().toString() + et_addr.getText().toString().trim();
        js.put("address", addr);
        js.put("area", sp_area.getSelectedItem().toString());
        js.put("appointDate", et_reserve.getText().toString().trim() + ":00");
        js.put("remarks", et_remark.getText().toString());
        basePost(Configs.Update_Worksheet, js, 0, this);
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
                    ToastUtils.showToast(context, "工单修改成功！");
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
