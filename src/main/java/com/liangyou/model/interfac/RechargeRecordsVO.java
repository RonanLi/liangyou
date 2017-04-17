package com.liangyou.model.interfac;

import java.util.Date;

/**
 * 充值记录
 * Created by Young on 2016/11/21.
 */
public class RechargeRecordsVO {
    private String orderNo;         // 订单编号
    private String rechargeType;    // 类型
    private Double rechargeAmount;  // 金额
    private Date rechargeTime;      // 时间
    private String rechargeStatus;  // 状态

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getRechargeType() {
        return rechargeType;
    }

    public void setRechargeType(String rechargeType) {
        this.rechargeType = rechargeType;
    }

    public Double getRechargeAmount() {
        return rechargeAmount;
    }

    public void setRechargeAmount(Double rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    public Date getRechargeTime() {
        return rechargeTime;
    }

    public void setRechargeTime(Date rechargeTime) {
        this.rechargeTime = rechargeTime;
    }

    public String getRechargeStatus() {
        return rechargeStatus;
    }

    public void setRechargeStatus(String rechargeStatus) {
        this.rechargeStatus = rechargeStatus;
    }
}
