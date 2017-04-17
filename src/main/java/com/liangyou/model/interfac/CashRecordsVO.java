package com.liangyou.model.interfac;

import java.util.Date;

/**
 * 提现记录
 * <p>
 * Created by Young on 2016/11/21.
 */
public class CashRecordsVO {
    private String cashCardNo;  // 提现卡号
    private Double cashAmount;  // 金额
    private Date cashTime;      // 时间
    private String cashStatus;  // 状态

    public String getCashCardNo() {
        return cashCardNo;
    }

    public void setCashCardNo(String cashCardNo) {
        this.cashCardNo = cashCardNo;
    }

    public Double getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(Double cashAmount) {
        this.cashAmount = cashAmount;
    }

    public Date getCashTime() {
        return cashTime;
    }

    public void setCashTime(Date cashTime) {
        this.cashTime = cashTime;
    }

    public String getCashStatus() {
        return cashStatus;
    }

    public void setCashStatus(String cashStatus) {
        this.cashStatus = cashStatus;
    }
}
