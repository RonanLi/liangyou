package com.liangyou.model.interfac;

import java.util.Date;

/**
 * Created by Young on 2016/11/22.
 */
public class BorrowVO {
    private String borrowName;      // 标的名称
    private String borrowLink;      // 标的详情链接
    private String borrowType;      // 标的类型
    private Double borrowApr;       // 标的年化收益
    private Double borrowAmount;    // 标的金额
    private String borrowTimeLimit; // 标的期限
    private String borrowStyle;     // 标的还款方式
    private Double borrowSchedule;  // 标的进度
    private int borrowIsDay;
    private Date borrowAddTime;
    private int timeLimitDay;
    private int timeLimit;

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

    public String getBorrowType() {
        return borrowType;
    }

    public void setBorrowType(String borrowType) {
        this.borrowType = borrowType;
    }

    public Double getBorrowApr() {
        return borrowApr;
    }

    public void setBorrowApr(Double borrowApr) {
        this.borrowApr = borrowApr;
    }

    public Double getBorrowAmount() {
        return borrowAmount;
    }

    public void setBorrowAmount(Double borrowAmount) {
        this.borrowAmount = borrowAmount;
    }

    public String getBorrowTimeLimit() {
        return borrowTimeLimit;
    }

    public void setBorrowTimeLimit(String borrowTimeLimit) {
        this.borrowTimeLimit = borrowTimeLimit;
    }

    public String getBorrowStyle() {
        return borrowStyle;
    }

    public void setBorrowStyle(String borrowStyle) {
        this.borrowStyle = borrowStyle;
    }

    public Double getBorrowSchedule() {
        return borrowSchedule;
    }

    public void setBorrowSchedule(Double borrowSchedule) {
        this.borrowSchedule = borrowSchedule;
    }

    public int getBorrowIsDay() {
        return borrowIsDay;
    }

    public void setBorrowIsDay(int borrowIsDay) {
        this.borrowIsDay = borrowIsDay;
    }

    public Date getBorrowAddTime() {
        return borrowAddTime;
    }

    public void setBorrowAddTime(Date borrowAddTime) {
        this.borrowAddTime = borrowAddTime;
    }

    public int getTimeLimitDay() {
        return timeLimitDay;
    }

    public void setTimeLimitDay(int timeLimitDay) {
        this.timeLimitDay = timeLimitDay;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }
}
