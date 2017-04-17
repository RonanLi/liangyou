package com.liangyou.api.ips;

/**
 * 环迅接口取现
 * 浏览器跳转POST
 * 协议类型：http/https 
 * 接口类型：POST 
 * @author wujing
 * 
 *
 */
public class DoDwTrade extends IpsModel {
	
	public DoDwTrade(){
		super();
		this.setUrl(this.getSubmitUrl()+"/CreditWeb/doDwTrade.aspx");
	}
	
	/**
	 *账户类型 :0#机构（暂未开放）；1#个人 
	 */
	private String acctType;   
	
	/**
	 * 提现模式:1#普通提现；2#定向提现 暂丌开放
	 */
	private String outType;  
	
	/**
	 * 标号 ：提现模式为2时，此字段
	 * 生效 内容是投标时的标号 
	 */
	private String bidNo;  
	
	/**
	 * 合同号 提现模式为2时，此字
	 * 段生效 内容是投标时的合同号 
	 */
	private String contractNo; 
	
	/**
	 *提现去向  提现模式为2时此字段生效  上
	 *送IPS托管账户号（个人/商户号） 
	 */
	private String dwTo;  
	
	/**
	 * 真实身份证（个人）/由IPS
	 * 颁发的商户号（商户）
	 */
	private String identNo;  
	
	/**
	 * 姓名
	 */
	private String realName;  
	
	/**
	 * IPS账户号    账户类型为1时，IPS个人托管账
	 * 户号   账户类型为0时，由IPS颁发的商户号
	 */
	private String ipsAcctNo;  
	
	/**
	 * 提现日期
	 */
	private String dwDate;   
	
	/**
	 * 提现金额
	 */
	private String trdAmt;    
	
	/**
	 * 平台手续费    金额单位，丌能为负，
	 * 允许为0  这里是平台向用户收取的费用 
	 */
	private String merFee;   
	
	/**
	 * IPS手续费收取方 1：平台
	 * 支付  2：提现方支付 
	 */
	private String ipsFeeType;   
	
	private String[] paramNames = new String[]{};

}
