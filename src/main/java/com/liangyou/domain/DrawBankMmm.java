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
@Table(name="draw_bank_mmm")
public class DrawBankMmm implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	@Column(name="bank_code")
	private String bankCode;

	@Column(name="bank_name")
	private String bankName;

	@Column(name="card_type")
	private String cardType;

	private String state;
	

	public DrawBankMmm() {
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getBankCode() {
		return bankCode;
	}


	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}


	public String getBankName() {
		return bankName;
	}


	public void setBankName(String bankName) {
		this.bankName = bankName;
	}


	public String getCardType() {
		return cardType;
	}


	public void setCardType(String cardType) {
		this.cardType = cardType;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}

	

}