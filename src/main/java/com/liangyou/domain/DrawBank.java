package com.liangyou.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;


/**
 * The persistent class for the draw_bank database table.
 * 
 */
@Entity
@Table(name="draw_bank")
public class DrawBank implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	@Column(name="bank_code")
	private String bankCode;

	@Column(name="bank_name")
	private String bankName;

	private String batch;

	@Column(name="card_type")
	private String cardType;

	@Column(name="channel_no")
	private String channelNo;

	private String env;

	@Lob
	@Column(name="logo_url")
	private String logoUrl;

	private String memo;

	private String owner;

	@Column(name="pay_channel_api")
	private String payChannelApi;

	@Column(name="public_tag")
	private String publicTag;

	private String state;

	public DrawBank() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBankCode() {
		return this.bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankName() {
		return this.bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBatch() {
		return this.batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	public String getCardType() {
		return this.cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getChannelNo() {
		return this.channelNo;
	}

	public void setChannelNo(String channelNo) {
		this.channelNo = channelNo;
	}

	public String getEnv() {
		return this.env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public String getLogoUrl() {
		return this.logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getOwner() {
		return this.owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getPayChannelApi() {
		return this.payChannelApi;
	}

	public void setPayChannelApi(String payChannelApi) {
		this.payChannelApi = payChannelApi;
	}

	public String getPublicTag() {
		return this.publicTag;
	}

	public void setPublicTag(String publicTag) {
		this.publicTag = publicTag;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

}