package com.liangyou.model.account;

/**
 * TGPROJECT-289
 * @author lx
 *
 */
public class WebAccountSumModel{
	


	private double webTotal;
	private double webUseMoney;
	private double webNoUseMoney;
	private double webCollection;
	private double webRepay;
	
	
	
	public WebAccountSumModel() {
	}
	public WebAccountSumModel(double webTotal, double webUseMoney,
			double webNoUseMoney, double webCollection, double webRepay) {
		super();
		this.webTotal = webTotal;
		this.webUseMoney = webUseMoney;
		this.webNoUseMoney = webNoUseMoney;
		this.webCollection = webCollection;
		this.webRepay = webRepay;
	}


	public double getWebTotal() {
		return webTotal;
	}
	
	
	public void setWebTotal(double webTotal) {
		this.webTotal = webTotal;
	}
	
	
	public double getWebUseMoney() {
		return webUseMoney;
	}
	
	
	public void setWebUseMoney(double webUseMoney) {
		this.webUseMoney = webUseMoney;
	}
	
	
	public double getWebNoUseMoney() {
		return webNoUseMoney;
	}
	
	
	public void setWebNoUseMoney(double webNoUseMoney) {
		this.webNoUseMoney = webNoUseMoney;
	}
	
	
	public double getWebCollection() {
		return webCollection;
	}
	
	
	public void setWebCollection(double webCollection) {
		this.webCollection = webCollection;
	}
	
	
	public double getWebRepay() {
		return webRepay;
	}
	
	
	public void setWebRepay(double webRepay) {
		this.webRepay = webRepay;
	}
	public double getWebOwnMoney(){
		return this.webTotal-this.webRepay;
	}
	
	
	


	
}
