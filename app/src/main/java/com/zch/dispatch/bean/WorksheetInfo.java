package com.zch.dispatch.bean;

import java.io.Serializable;

/**
 * Created by zch on 2020/5/3.
 */

public class WorksheetInfo implements Serializable {

    private String id;
    private String uname; //客户姓名
    private String telphone;
    private String addr;
    private String lat; //纬度
    private String lon;//经度
    private String content; //清洗需求
    private String areaname;//区域
    private String owner; //责任人
    private String reservetime; //预约时间
    private String cost ;  //清洗费用
    private String deal_user; //师傅
    private String deal_tel;//师傅电话
    private String addtime;  //工单生成时间
    private String dealtime; //工单完成时间
    private String status; // 工单状态  0新单， 1未完成， 2已完成，3已结算
    private String remark;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAreaname() {
        return areaname;
    }

    public void setAreaname(String areaname) {
        this.areaname = areaname;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getReservetime() {
        return reservetime;
    }

    public void setReservetime(String reservetime) {
        this.reservetime = reservetime;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getDeal_user() {
        return deal_user;
    }

    public void setDeal_user(String deal_user) {
        this.deal_user = deal_user;
    }

    public String getDeal_tel() {
        return deal_tel;
    }

    public void setDeal_tel(String deal_tel) {
        this.deal_tel = deal_tel;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getDealtime() {
        return dealtime;
    }

    public void setDealtime(String dealtime) {
        this.dealtime = dealtime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public static String getState(String statecode){
        if (statecode.equals("0")){
            return "新单";
        }else if (statecode.equals("1")){
            return "未完成";
        }else if (statecode.equals("2")){
            return "已完成";
        }else if (statecode.equals("3")){
            return "已结算";
        }
        return "";
    }
}
