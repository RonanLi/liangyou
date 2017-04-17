package com.liangyou.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * v1.8.0.3_u3  TGPROJECT-332  qinjun 2014-06-06 
 */
@Entity(name="user_property")
public class UserProperty implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	//是否签约1：是0：否
	private int isContract;
	//是否守约1：是0：否
	private int contractStatus;
	//签约金额
	private double contractMoney;
	//拓展字段
	private String extend;
	//备注
	private String memo;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id", updatable=false)
	private User user;

	public UserProperty() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getIsContract() {
		return isContract;
	}

	public void setIsContract(int isContract) {
		this.isContract = isContract;
	}

	public int getContractStatus() {
		return contractStatus;
	}

	public void setContractStatus(int contractStatus) {
		this.contractStatus = contractStatus;
	}

	public double getContractMoney() {
		return contractMoney;
	}

	public void setContractMoney(double contractMoney) {
		this.contractMoney = contractMoney;
	}

	public String getExtend() {
		return extend;
	}

	public void setExtend(String extend) {
		this.extend = extend;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
}