package com.liangyou.model.interfac;

import java.util.Date;

/**
 * // 未收 / 已收款明细
 * Created by Young on 2016/11/21.
 */
public class CollectVO {
    private String borrowName;      // 标名
    private String borrowLink;      // 标的详情链接
    private Date collectTime;       // 收款日期
    private String borrower;        // 借款人
    private Double capital;         // 本金
    private Double interest;        // 利息
    private String collectStatus;   // 状态

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

    public Date getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(Date collectTime) {
        this.collectTime = collectTime;
    }

    public String getBorrower() {
        return borrower;
    }

    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }

    public Double getCapital() {
        return capital;
    }

    public void setCapital(Double capital) {
        this.capital = capital;
    }

    public Double getInterest() {
        return interest;
    }

    public void setInterest(Double interest) {
        this.interest = interest;
    }

    public String getCollectStatus() {
        return collectStatus;
    }

    public void setCollectStatus(String collectStatus) {
        this.collectStatus = collectStatus;
    }
}
