package com.liangyou.model.interfac;

import java.util.Date;

/**
 * 我的银行卡
 *
 * Created by Young on 2016/11/21.
 */
public class AccountBankVO {
    private String bankname;
    private String cardNumber;
    private String addressCreate;
    private Date timeCreate;
    private String phonenumber;
    private String userid;
    private String bankid;

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getAddressCreate() {
        return addressCreate;
    }

    public void setAddressCreate(String addressCreate) {
        this.addressCreate = addressCreate;
    }

    public Date getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(Date timeCreate) {
        this.timeCreate = timeCreate;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getBankid() {
        return bankid;
    }

    public void setBankid(String bankid) {
        this.bankid = bankid;
    }
}
