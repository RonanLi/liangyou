package com.liangyou.model.interfac;

import java.util.Date;

/**
 * Created by Young on 2016/11/21.
 */
public class AccountLogVO {
    private String type;            // 类型
    private double account;         // 操作金额
    private Double totalAccount;    // 总金额
    private Double useAccount;      // 可用金额
    private Double noUseAccount;    // 冻结金额
    private Double collectAccount;  // 待收金额
    private Double repayAccount;    // 待还款金额
    private String userName;        // 交易对方
    private Date addTime;           // 记录时间
    private String remark;          // 备注信息

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAccount() {
        return account;
    }

    public void setAccount(double account) {
        this.account = account;
    }

    public Double getTotalAccount() {
        return totalAccount;
    }

    public void setTotalAccount(Double totalAccount) {
        this.totalAccount = totalAccount;
    }

    public Double getUseAccount() {
        return useAccount;
    }

    public void setUseAccount(Double useAccount) {
        this.useAccount = useAccount;
    }

    public Double getNoUseAccount() {
        return noUseAccount;
    }

    public void setNoUseAccount(Double noUseAccount) {
        this.noUseAccount = noUseAccount;
    }

    public Double getCollectAccount() {
        return collectAccount;
    }

    public void setCollectAccount(Double collectAccount) {
        this.collectAccount = collectAccount;
    }

    public Double getRepayAccount() {
        return repayAccount;
    }

    public void setRepayAccount(Double repayAccount) {
        this.repayAccount = repayAccount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
