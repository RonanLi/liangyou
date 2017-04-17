package com.liangyou.model.interfac;

import java.util.Date;

/**
 * 招标中的项目VO
 * Created by Young on 2016/11/21.
 */
public class TenderingVO {
    private String borrowName; // 标名
    private String borrowLink; // 标详情链接
    private String borrower; // 借款人
    private Double tenderMoney; // 有效金额
    private Date tenderDate; // 投标时间
    private String tenderStatus; // 状态

    public String getBorrowName() {
        return borrowName;
    }

    public void setBorrowName(String borrowName) {
        this.borrowName = borrowName;
    }

    public String getBorrowLink() {
        return borrowLink;
    }

    public void setBorrowLink(String borrowLink) {
        this.borrowLink = borrowLink;
    }

    public String getBorrower() {
        return borrower;
    }

    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }

    public Double getTenderMoney() {
        return tenderMoney;
    }

    public void setTenderMoney(Double tenderMoney) {
        this.tenderMoney = tenderMoney;
    }

    public Date getTenderDate() {
        return tenderDate;
    }

    public void setTenderDate(Date tenderDate) {
        this.tenderDate = tenderDate;
    }

    public String getTenderStatus() {
        return tenderStatus;
    }

    public void setTenderStatus(String tenderStatus) {
        this.tenderStatus = tenderStatus;
    }
}

