package com.liangyou.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="user_credit_type")
public class UserCreditType implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	private String name;
	
	private int status;
	
	/*积分派送类型， 0：不启用
	 * 		   1：全部派送，就是满标复审的时候全部给投资人，推荐人，借款人积分；
	 *         2：按月派送，就是按照还款的时间每个月派送给投资人、推荐人、借款人积分。
	 */
	@Column(name="credit_manager_status")
	private int creditManagerStatus; 
	  
	
	private String type;
	
	private String nid;
	
	private long value;
	
	@Column(name="value_rate")
	private double valueRate; //比例，比如投资成功500，积分为：500*valueRate，这里就是计算的比例
	
	@Column(name="valid_rate")//默认为1，积分100%转换成可用积分；当客户要求是可以设置比例
	private double validRate;
	
	@Column(name="borrow_type")//哪几种标可以算积分,格式：101,104
	private String borrowType;
	
	@Column(name="decimal_manager")//四舍五入的比例值，比如 0.3，就是小数部分0>=0.3进1；
	private double decimalManager;
	
	private String remark;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="op_user")//操作用户，一般指管理员
	private User oPuser;
	
	private String ip;
	
	public UserCreditType(){
		
	}
	
	public UserCreditType(int id){
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}
	
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public User getoPuser() {
		return oPuser;
	}

	public void setoPuser(User oPuser) {
		this.oPuser = oPuser;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public double getValueRate() {
		return valueRate;
	}

	public void setValueRate(double valueRate) {
		this.valueRate = valueRate;
	}

	public double getValidRate() {
		return validRate;
	}

	public void setValidRate(double validRate) {
		this.validRate = validRate;
	}

	public double getDecimalManager() {
		return decimalManager;
	}

	public void setDecimalManager(double decimalManager) {
		this.decimalManager = decimalManager;
	}

	public String getNid() {
		return nid;
	}

	public void setNid(String nid) {
		this.nid = nid;
	}

	public String getBorrowType() {
		return borrowType;
	}

	public void setBorrowType(String borrowType) {
		this.borrowType = borrowType;
	}

	public int getCreditManagerStatus() {
		return creditManagerStatus;
	}

	public void setCreditManagerStatus(int creditManagerStatus) {
		this.creditManagerStatus = creditManagerStatus;
	}
	
	
}
