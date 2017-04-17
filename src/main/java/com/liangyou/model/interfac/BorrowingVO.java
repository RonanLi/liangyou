package com.liangyou.model.interfac;

import java.util.Date;

/**
 * 正在招标中的项目 / 正在还款中的项目
 * Created by Young on 2016/11/21.
 */
public class BorrowingVO {
    private String borrowName;
    private String borrowLink;
    private Double borrowAccount;
    private Double borrowApr;
    private String timeLimit;
    private String repayTime;
    private String status;

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

    public Double getBorrowAccount() {
        return borrowAccount;
    }

    public void setBorrowAccount(Double borrowAccount) {
        this.borrowAccount = borrowAccount;
    }

    public Double getBorrowApr() {
        return borrowApr;
    }

    public void setBorrowApr(Double borrowApr) {
        this.borrowApr = borrowApr;
    }

    public String getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(String timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getRepayTime() {
        return repayTime;
    }

    public void setRepayTime(String repayTime) {
        this.repayTime = repayTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
