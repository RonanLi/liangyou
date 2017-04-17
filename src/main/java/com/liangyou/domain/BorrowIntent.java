package com.liangyou.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.liangyou.context.Global;
import com.liangyou.dao.BorrowTenderDao;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.admin.AdminUserinfoAction;

/**
 * 
 * @author lx TGPROJECT-324 2014-05-29 add
 *
 */
@Entity
@Table(name = Global.TABLE_PREFIX +  "borrow_intent")
public class BorrowIntent implements Serializable {
	private static final long serialVersionUID = 1L;

	public BorrowIntent() {
	}
	
	public BorrowIntent(long id) {
		super();
		this.id = id;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;//提交该申请的前台用户
	
	@ManyToOne(fetch=FetchType.LAZY)  
	private Area province;//所在省份
	
	@ManyToOne(fetch=FetchType.LAZY)  
	private Area city;//所在市
	
	@Column(name="contact_username")
	private String contactUsername;//联系人
	
	private String phone;//联系电话
	
	@Column(name="intent_account")
	private double intentAccount;//意向借款金额
	
	@Column(name="company_name")
	private String companyName;//公司名称
	
	@Column(name="intent_time_limit")
	private int intentTimeLimit;//意向借款期限(月)
	
	private int status;//0未处理  1已处理
	
	private String remark;//备注

	//v1.8.0.3_u3 XINHEHANG-66  qinjun  2014-06-20  start 
	private Date addtime;
	private Date updatetime;
	private String memo;
	public Date getAddtime() {
		return addtime;
	}
	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	//v1.8.0.3_u3 XINHEHANG-66  qinjun  2014-06-20  start
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Area getProvince() {
		return province;
	}

	public void setProvince(Area province) {
		this.province = province;
	}

	public Area getCity() {
		return city;
	}

	public void setCity(Area city) {
		this.city = city;
	}

	public String getContactUsername() {
		return contactUsername;
	}

	public void setContactUsername(String contactUsername) {
		this.contactUsername = contactUsername;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public double getIntentAccount() {
		return intentAccount;
	}

	public void setIntentAccount(double intentAccount) {
		this.intentAccount = intentAccount;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public int getIntentTimeLimit() {
		return intentTimeLimit;
	}

	public void setIntentTimeLimit(int intentTimeLimit) {
		this.intentTimeLimit = intentTimeLimit;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	
	
}
