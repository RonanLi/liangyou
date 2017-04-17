package com.liangyou.model.APIModel;
import com.liangyou.domain.User;


/**
 *  TGPROJECT-337
 * 用于平台向用户支付接口
 * @author 武警
 *
 */
public class WebPayModel {
	
	private double money;  //支付金额
	 
	private User payUser;   //接收人
	
	public WebPayModel(){
		
	}
	public WebPayModel(User user ,double money){
		this.money = money;
		this.payUser = user;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public User getPayUser() {
		return payUser;
	}

	public void setPayUser(User payUser) {
		this.payUser = payUser;
	}
	
	

}
