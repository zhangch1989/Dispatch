package com.zch.dispatch.tools;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.Time;
import android.widget.EditText;
import android.widget.TextView;


import com.zch.dispatch.datetime.DateListener;
import com.zch.dispatch.datetime.TimeSelectorDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by zch on 2017/10/31.
 */
public class DateUtils {

    public static SimpleDateFormat sf = null;

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getNowTime(String string) {
        String timeString = null;
        Time time = new Time();
        time.setToNow();
        String year = thanTen(time.year);
        String month = thanTen(time.month + 1);
        String monthDay = thanTen(time.monthDay);
        String hour = thanTen(time.hour);
//        String minute = thanTen(time.minute);

        timeString = year + "-" + month + "-" + monthDay + " " + hour;// + ":" + minute;
        // System.out.println("-------timeString----------" + timeString);
        return timeString;
    }

    /**
     * 十以下加零
     *
     * @param str
     * @return
     */
    public static String thanTen(int str) {
        String string = null;
        if (str < 10) {
            string = "0" + str;
        } else {
            string = "" + str;
        }
        return string;
    }

    /**
     * 日期转星期
     *
     * @param datetime
     * @return 星期几
     */
    public static String dateToWeek(String datetime) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        Calendar cal = Calendar.getInstance(); // 获得一个日历
        Date datet = null;
        try {
            datet = f.parse(datetime);
            cal.setTime(datet);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * 日期转星期
     *
     * @param datetime
     * @return  周几
     */
    public static String dateToWeek2(String datetime) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String[] weekDays = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
        Calendar cal = Calendar.getInstance(); // 获得一个日历
        Date datet = null;
        try {
            datet = f.parse(datetime);
            cal.setTime(datet);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    public static String getNowDatetime(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    public static String getNowDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    public static String getLastMonthDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH, -30);
        String str = formatter.format(now.getTime());
        return str;
    }

    public static String getNowMonth(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    /**
     * 获取当前季度
     * @return
     */
    public static String getNowQuarter(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        Calendar c = Calendar.getInstance();
        int m = c.get(Calendar.MONTH) + 1;
        String quarter = "";
        if (m >= 1 && m <= 3){
            quarter = "1";
        }
        if (m >= 4 && m <= 6){
            quarter = "2";
        }
        if (m >= 7 && m <= 9){
            quarter = "3";
        }
        if (m >= 10 && m <= 12){
            quarter = "4";
        }
        return str + "年 第" + quarter + "季度";
    }

    public static String getNowYear(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    /**
     * 得到当前是今年第几周
     * @return  2019年 第X周
     */
    public static String getNowWeek(){
        SimpleDateFormat formatterY = new SimpleDateFormat("yyyy");
        SimpleDateFormat formatterM = new SimpleDateFormat("MM");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        Calendar c = Calendar.getInstance();
        int week = c.get(Calendar.WEEK_OF_YEAR);
        int month = Integer.valueOf(formatterM.format(curDate));
        int year = Integer.valueOf(formatterY.format(curDate));
        String str_year;
        if(week == 1 && month == 12){
            str_year = String.valueOf(year + 1);
        }else {
            str_year = String.valueOf(year);
        }

        return str_year + "年 第" + week + "周";
    }

    public static String getNowDateHour(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    /**
     * 获取明天的日期
     * @return
     */
    public static String getTomorrowDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date=new Date();//取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE,1);//把日期往后增加一天.整数往后推,负数往前移动
        date=calendar.getTime(); //这个时间就是日期往后推一天的结果
        String str = formatter.format(date);
        return str;
    }

    /**
     * 获取本周第一天的日期
     * 默认周天为本周的第一天getFirstweekDay
     * @return
     */
    public static String getWeekFirstDate(){
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.getTime();
        return dateFormater.format(cal.getTime()) + "";
    }

    /**
     * 获取本月第一天的日期
     * @return
     */
    public static String getMonthFirstDate(){
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.getTime();
        return dateFormater.format(cal.getTime()) + "";
    }

    /**
     * 比较两个时间，若newtime>nowtime  即有最新数据
     * @param nowtime
     * @param newtime
     * @return
     */
    public static boolean CompareTime(String nowtime, String newtime){
        boolean flag = false;
        if(nowtime.equals("")){
            return false;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");//年-月-日 时-分
        try {
            Date date1 = dateFormat.parse(nowtime);//开始时间
            Date date2 = dateFormat.parse(newtime);//结束时间

            if (date2.getTime()<date1.getTime()){
                flag = false;
//                Toast.makeText(PostActivity.this,"结束时间小于开始时间", Toast.LENGTH_SHORT).show();
            }else if (date2.getTime()==date1.getTime()){
                flag = false;
//                Toast.makeText(PostActivity.this,"开始时间与结束时间相同", Toast.LENGTH_SHORT).show();
            }else if (date2.getTime()>date1.getTime()){
                //正常情况下的逻辑操作.
                flag = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return flag;
    }
    /**
     * 控件选择控件
     * @param et
     */
    public static void setDatetime(Context context, final EditText et, String nowtime, int type){
        TimeSelectorDialog dialog = new TimeSelectorDialog(context);
        //设置标题
        dialog.setTimeTitle("选择时间:");
        //显示类型
        dialog.setIsShowtype(type);
        //默认时间
        dialog.setCurrentDate(nowtime);
        //隐藏清除按钮
        dialog.setEmptyIsShow(true);
        //设置起始时间
//        dialog.setStartYear(2017);
        dialog.setDateListener(new DateListener() {
            @Override
            public void onReturnDate(String time,int year, int month, int day, int hour, int minute, int isShowType) {
                et.setText(time);
            }
            @Override
            public void onReturnDate(String empty) {
                et.setText(empty);
            }
        });
        dialog.show();
    }

    /**
     * 控件选择控件
     * @param tv
     */
    public static void setDatetime(Context context, final TextView tv, String nowtime, int type){
        TimeSelectorDialog dialog = new TimeSelectorDialog(context);
        //设置标题
        dialog.setTimeTitle("选择时间:");
        //显示类型
        dialog.setIsShowtype(type);
        //默认时间
        dialog.setCurrentDate(nowtime);
        //隐藏清除按钮
        dialog.setEmptyIsShow(false);
        //设置起始时间
//        dialog.setStartYear(2017);
        dialog.setDateListener(new DateListener() {
            @Override
            public void onReturnDate(String time,int year, int month, int day, int hour, int minute, int isShowType) {
                tv.setText(time);
            }
            @Override
            public void onReturnDate(String empty) {
                tv.setText(empty);
            }
        });
        dialog.show();
    }


    /**
     * 获取两个时间的差值  1小时以内以分钟，一小时以上按小时，24小时以上按天
     * @param firsttime 前一个时间
     * @param lasttime 后一个时间
     * @return
     */
    public static String getTimeGap(String firsttime, String lasttime){
        String result = "";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (TextUtils.isEmpty(lasttime) || TextUtils.isEmpty(lasttime)){
            return result;
        }
        try
        {
            Date d1 = df.parse(firsttime);
            Date d2 = df.parse(lasttime);
            long diff = d2.getTime() - d1.getTime() ;//这样得到的差值是毫秒级别

            double min = Math.ceil(1.0 *  diff % (1000 * 24 * 60 * 60) % (1000 * 60 * 60) / (1000* 60));
            double hour = Math.ceil(1.0 *  diff % (1000 * 24 * 60 * 60) /(1000 * 60 * 60) );
            double day =  Math.ceil(1.0 *  diff / (1000 * 24 * 60 * 60) );
//            MLog.e("---------", "min="+ min + " ,hour=" + hour + " ,day="+day);
            if (day > 1){
                return (int)day + "天";
            }else if (hour < 24 && hour > 1){
                return (int)hour + "小时";
            }else {
                return (int)min + "分钟";
            }
//            System.out.println(""+days+"天"+hours+"小时"+minutes+"分");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
