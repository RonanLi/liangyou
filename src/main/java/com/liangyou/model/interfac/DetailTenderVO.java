package com.liangyou.model.interfac;

import java.util.Date;

/**
 * Created by Young on 2016/11/16.
 */
public class DetailTenderVO {
    private String userName;
    private Double borrowApr;
    private Double tenderMoney;
    private Date tenderDate;

    public Double getBorrowApr() {
        return borrowApr;
    }

    public void setBorrowApr(Double borrowApr) {
        this.borrowApr = borrowApr;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
