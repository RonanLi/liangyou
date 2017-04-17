package com.liangyou.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.liangyou.domain.Message;
import com.liangyou.domain.MsgOperate;
import com.liangyou.domain.User;

/*****************
* @ClassName: MsgReq
* @Description: 消息请求参数
* @author xx_erongdu
* @date 2013-7-17 下午2:36:23
*
*****************/ 
public class MsgReq {
	//站内信
	private Message message;
	
	private String ip;
	private User sender;
	//必填   接受者
	private User receiver;
	//必填   业务
	private MsgOperate msgOperate;
	
	private Map<String, String> map;
	//用户名
	private String username;
	//手机号或者身份证号
	private String no;
	//状态
	private String status;
	//类型
	private String type;
	//提现、充值金额
	private String money;
	//时间
	private String time;
	//提示信息
	private String msg;
	//手续费
	private String fee;
	//标名
	private String borrowname;
	//借款总额
	private String account;
	//年利率
	private String apr;
	//借款期限
	private String timelimit;
	//借款当前期
	private String order;
	//月利率
	private String monthapr;
	
	//标的路径
	private String borrowUrl;
	//v1.8.0.3 TGPROJECT-12 lx 2014-04-02 start
	/**
	 * 投标本金
	 */
	private String tenderAccount;
	/**
	 * 投标应得利息
	 */
	private String tenderInterest;
	/**
	 * 投标实际获取
	 */
	private String tenderRepaymentYesAccount;
	
	public String getTenderAccount() {
		return tenderAccount;
	}

	public void setTenderAccount(String tenderAccount) {
		this.tenderAccount = tenderAccount;
	}

	public String getTenderInterest() {
		return tenderInterest;
	}

	public void setTenderInterest(String tenderInterest) {
		this.tenderInterest = tenderInterest;
	}

	public String getTenderRepaymentYesAccount() {
		return tenderRepaymentYesAccount;
	}

	public void setTenderRepaymentYesAccount(String tenderRepaymentYesAccount) {
		this.tenderRepaymentYesAccount = tenderRepaymentYesAccount;
	}

	//v1.8.0.3 TGPROJECT-12 lx 2014-04-02 end
	//v1.8.0.3 TGPROJECT-30 lx 2014-04-09 start
	private String endTime;
	private String valueDate;
	private String repaymentTime;
	
	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getValueDate() {
		return valueDate;
	}

	public void setValueDate(String valueDate) {
		this.valueDate = valueDate;
	}

	public String getRepaymentTime() {
		return repaymentTime;
	}

	public void setRepaymentTime(String repaymentTime) {
		this.repaymentTime = repaymentTime;
	}

	//v1.8.0.3 TGPROJECT-30 lx 2014-04-09 end
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public User getReceiver() {
		return receiver;
	}

	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}

	public MsgOperate getMsgOperate() {
		return msgOperate;
	}

	public void setMsgOperate(MsgOperate msgOperate) {
		this.msgOperate = msgOperate;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public Map<String, String> getMap() {
		map = new HashMap<String, String>();
		if(StringUtils.isNotBlank(username)){
			map.put("username", username);
		}
		if(StringUtils.isNotBlank(no)){
			map.put("no", no);
		}
		if(StringUtils.isNotBlank(status)){
			map.put("status", status);
		}
		if(StringUtils.isNotBlank(type)){
			map.put("type", type);
		}
		if(StringUtils.isNotBlank(money)){
			map.put("money", money);
		}
		if(StringUtils.isNotBlank(time)){
			map.put("time", time);
		}
		if(StringUtils.isNotBlank(msg)){
			map.put("msg", msg);
		}
		if(StringUtils.isNotBlank(fee)){
			map.put("fee", fee);
		}
		if(StringUtils.isNotBlank(borrowname)){
			map.put("borrowname", borrowname);
		}
		if(StringUtils.isNotBlank(account)){
			map.put("account", account);
		}
		if(StringUtils.isNotBlank(apr)){
			map.put("apr", apr);
		}
		if(StringUtils.isNotBlank(timelimit)){
			map.put("timelimit", timelimit);
		}
		if(StringUtils.isNotBlank(order)){
			map.put("order", order);
		}
		if(StringUtils.isNotBlank(borrowUrl)){
			map.put("borrowUrl",borrowUrl);
		}
		//v1.8.0.3 TGPROJECT-12 lx 2014-04-02 start
		if(StringUtils.isNotBlank(tenderAccount)){
			map.put("tenderAccount", tenderAccount);
		}
		if(StringUtils.isNotBlank(tenderInterest)){
			map.put("tenderInterest", tenderInterest);
		}
		if(StringUtils.isNotBlank(tenderRepaymentYesAccount)){
			map.put("tenderRepaymentYesAccount",tenderRepaymentYesAccount);
		}
		//v1.8.0.3 TGPROJECT-12 lx 2014-04-02 end
		//v1.8.0.3 TGPROJECT-30 lx 2014-04-09 start
		if(StringUtils.isNotBlank(endTime)){
			map.put("endTime", endTime);
		}
		if(StringUtils.isNotBlank(valueDate)){
			map.put("valueDate", valueDate);
		}
		if(StringUtils.isNotBlank(repaymentTime)){
			map.put("repaymentTime",repaymentTime);
		}
		//v1.8.0.3 TGPROJECT-30 lx 2014-04-09 end
		
		//V1.8.0.4 TGPROJECT-97  显示月利率的方法  start
		if(StringUtils.isNotBlank(monthapr)){
			map.put("monthapr",monthapr);
		}
		//V1.8.0.4 TGPROJECT-97  显示月利率的方法  end
		return map;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public String getBorrowname() {
		return borrowname;
	}

	public void setBorrowname(String borrowname) {
		this.borrowname = borrowname;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getApr() {
		return apr;
	}

	public void setApr(String apr) {
		this.apr = apr;
	}

	public String getTimelimit() {
		return timelimit;
	}

	public void setTimelimit(String timelimit) {
		this.timelimit = timelimit;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}

	public String getBorrowUrl() {
		return borrowUrl;
	}

	public void setBorrowUrl(String borrowUrl) {
		this.borrowUrl = borrowUrl;
	}

	public String getMonthapr() {
		return monthapr;
	}

	public void setMonthapr(String monthapr) {
		this.monthapr = monthapr;
	}
	
}
