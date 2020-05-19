package com.zch.dispatch.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextPaint;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zch.dispatch.R;
import com.zch.dispatch.fragment.Fragment_Finish;
import com.zch.dispatch.fragment.Fragment_New;
import com.zch.dispatch.fragment.Fragment_Todo;
import com.zch.dispatch.tools.ToastUtils;
import com.zch.dispatch.views.NoPreloadViewPager;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends FragmentActivity implements EasyPermissions.PermissionCallbacks{
    private final static String TAG = "MainActivity";
    private long firstTime = 0; //记录两次返回按钮的时间

    private Context context;
    public static MainActivity instance;

    private ImageButton ibtn_add;
    private Button btn_about;

    private int currIndex = 0;
    private NoPreloadViewPager mPager;
    private ArrayList<Fragment> fragmentList;
    private LinearLayout ll_tabs;
    private Fragment fragment_tab1, fragment_tab2, fragment_tab3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        instance = this;

        initView();
        initEvent();
    }

    private void initView(){
        btn_about = (Button) findViewById(R.id.btn_about);
        ibtn_add = (ImageButton) findViewById(R.id.ibtn_add);
        ll_tabs = (LinearLayout) findViewById(R.id.ll_tabs);
        mPager = (NoPreloadViewPager) findViewById(R.id.viewpager);

        fragmentList = new ArrayList<Fragment>();
        fragment_tab1 = new Fragment_New();
        fragment_tab2 = new Fragment_Todo();
        fragment_tab3 = new Fragment_Finish();
        fragmentList.add(fragment_tab1);
        fragmentList.add(fragment_tab2);
        fragmentList.add(fragment_tab3);

        mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        mPager.setCurrentItem(0);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
//        mPager.setOffscreenPageLimit(2);
        updataBtnColor();
    }

    private void initEvent(){
        btn_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, Activity_About.class));
            }
        });
        ibtn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, Activity_WorksheetAdd.class));
            }
        });

        //tab 按钮的点击事件
        for (int i = 0; i < ll_tabs.getChildCount(); i++){
            RelativeLayout ll = (RelativeLayout) ll_tabs.getChildAt(i);
            final  int index = i;
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currIndex = index;
                    mPager.setCurrentItem(index);
                }
            });
        }
    }

    /**
     * 修改选中的tab颜色
     */
    private void updataBtnColor(){
        for (int i = 0; i < ll_tabs.getChildCount(); i++){
            TextView tv = (TextView)((RelativeLayout) ll_tabs.getChildAt(i)).getChildAt(0);
            TextPaint tp = tv.getPaint();
            LinearLayout ll = (LinearLayout) ((RelativeLayout) ll_tabs.getChildAt(i)).getChildAt(1);
            if(i == currIndex){
                tv.setTextColor(getResources().getColor(R.color.blue));
                ll.setVisibility(View.VISIBLE);
            }else{
                tv.setTextColor(Color.BLACK);
                ll.setVisibility(View.INVISIBLE);
            }
        }
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

    public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragments;
        public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments){
            super(fm);
            // TODO Auto-generated constructor stub
            mFragments=fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }

    public class MyOnPageChangeListener implements NoPreloadViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            currIndex = position;
            updataBtnColor();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
