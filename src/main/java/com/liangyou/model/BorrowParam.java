package com.liangyou.model;

import javax.servlet.http.HttpServletRequest;

import com.liangyou.domain.ExperienceMoney;
import com.liangyou.tool.iphelper.IPUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;

public class BorrowParam {
	private double money;
	private Long id;
	private String pwd;
	private String paypwd;
	private String validcode;
	private String ip;
	private int tenderCount;
	private String resultFlag; // 投标专用
	private String tenderNo; // 汇付接口专用，用户保存投标回调保存订单号
	private String tenderDate; // 汇付投标专用，保存投标订单时间

	private String resultCode;// 双乾处理结果
	private boolean isAuto;

	private String LoanNo;// 双乾转账订单号

	private String emLoanNo; // 体验金利息抵扣本金的双乾转账订单号
	private String emTenderNo; // 体验金利息抵扣本金的用户保存投标回调保存订单号

	private int type;

	private ExperienceMoney experienceMoney; // add by gy 2016-10-26 17:12:17 增加体验金的参数字段

	private boolean isWap = false; // add by gy 2016-11-29 11:53:56 增加wap端访问的标识

	private HttpServletRequest request;

	public BorrowParam() {
		super();
	}

	public BorrowParam(HttpServletRequest request) {
		super();
		this.request = request;
		init();
	}

	private void init() {
		this.money = NumberUtils.format2(NumberUtils.getDouble(getValue("money")));
		this.id = NumberUtils.getLong(getValue("id"));
		this.pwd = getValue("pwd");
		this.paypwd = getValue("paypwd");
		this.validcode = getValue("validCode");
		this.ip = IPUtils.getRemortIP(request);
		this.tenderCount = NumberUtils.getInt(getValue("tenderCount"));
	}

	private String getValue(String name) {
		return StringUtils.isNull(request.getParameter(name));
	}

	public boolean isAuto() {
		return isAuto;
	}

	public void setAuto(boolean isAuto) {
		this.isAuto = isAuto;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getValidcode() {
		return validcode;
	}

	public void setValidcode(String validcode) {
		this.validcode = validcode;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getPaypwd() {
		return paypwd;
	}

	public void setPaypwd(String paypwd) {
		this.paypwd = paypwd;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getTenderCount() {
		return tenderCount;
	}

	public void setTenderCount(int tenderCount) {
		this.tenderCount = tenderCount;
	}

	public String getResultFlag() {
		return resultFlag;
	}

	public void setResultFlag(String resultFlag) {
		this.resultFlag = resultFlag;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTenderNo() {
		return tenderNo;
	}

	public void setTenderNo(String tenderNo) {
		this.tenderNo = tenderNo;
	}

	public String getTenderDate() {
		return tenderDate;
	}

	public void setTenderDate(String tenderDate) {
		this.tenderDate = tenderDate;
	}

	public String getLoanNo() {
		return LoanNo;
	}

	public void setLoanNo(String loanNo) {
		LoanNo = loanNo;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	// add by gy 2016-10-26 17:12:39 begin
	public ExperienceMoney getExperienceMoney() {
		return experienceMoney;
	}

	public void setExperienceMoney(ExperienceMoney experienceMoney) {
		this.experienceMoney = experienceMoney;
	}

	public String getEmLoanNo() {
		return emLoanNo;
	}

	public void setEmLoanNo(String emLoanNo) {
		this.emLoanNo = emLoanNo;
	}

	public String getEmTenderNo() {
		return emTenderNo;
	}

	public void setEmTenderNo(String emTenderNo) {
		this.emTenderNo = emTenderNo;
	}
	// add by gy 2016-10-26 17:12:39 end

	public boolean isWap() {
		return isWap;
	}

	public void setWap(boolean wap) {
		isWap = wap;
	}
	
	@Override
	public String toString() {
		return "BorrowParam [money=" + money + ", id=" + id + ", pwd=" + pwd + ", paypwd=" + paypwd + ", validcode=" + validcode + ", ip=" + ip + ", tenderCount=" + tenderCount + ", resultFlag=" + resultFlag + ", tenderNo=" + tenderNo + ", tenderDate=" + tenderDate + ", resultCode=" + resultCode + ", isAuto=" + isAuto + ", LoanNo=" + LoanNo + ", emLoanNo=" + emLoanNo + ", emTenderNo=" + emTenderNo + ", type=" + type + ", experienceMoney=" + experienceMoney + ", request=" + request + "]";
	}
}
