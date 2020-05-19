package com.zch.dispatch.base;

/**
 * Created by zch on 2020/5/3.
 */

public interface Configs {
    /**
     * log日志开关，否表示不输出LOG日志
     */
    boolean LOG_ENABLED = true;

    /**
     * 接口返回标志
     */
    String SUCCESS ="0";
    String NO_ACCESS = "201"; //token失效
    String FAIL = "1";


    String IP_PATH = " http://123.56.62.165:8000";
    String Send_Login = IP_PATH + "/clean/account/login"; //登录接口
    String Get_List =  IP_PATH  + "/clean/order/list";//获取工单列表
    String Add_Worksheet = IP_PATH + "/clean/order/add";  //新增工单
    String Send_Worksheet = IP_PATH + "/clean/order/finished"; //修改工单状态
    String Update_Worksheet = IP_PATH + "/clean/order/appointModify"; //修改工单
    String Order_Worksheet = IP_PATH + "/clean/order/appointModify"; //修改工单预约时间
}
