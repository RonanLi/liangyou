package com.liangyou.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.liangyou.context.Global;

/**
 * @Desc 奖励记录表   映射类
 * @author yjt_anzi
 *
 */
@Entity
@Table(name = Global.TABLE_PREFIX +  "reward_record")
public class RewardRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="reward_record_id")
	private long rewardRecordId;
	@Column(name="reward_type")
	private String rewardType;
	@Column(name="denomination")
	private double denomination;
	
	// FK userId
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_date")
	private Date createDate;
	@Column(name="create_by")
	private String createBy;
	
	// FK activityId
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="activity_id") 
	private ActivityConfig activityConfig;
	
	@Column(name="is_enable")
	private String isEnable;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="use_date")
	private Date useDate; //使用日期
	@Column(name="order_no")
	private String orderNo; //订单号
	@Column(name="from_be")
	private String fromBe; //奖励来源
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="last_update_date")
	private Date lastUpdateDate;
	@Column(name="last_update_by")
	private String lastUpdateBy;
	
	@Column(name="standby_one")
	private String standbyOne;
	
	@Column(name="standby_two")
	private String standbyTwo;
	
	
	@Column(name="standby_three")
	private int standbyThree;
	
	
	@Column(name="standby_four")
	private int standbyFour;
	
	@Column(name="remark")
	private String remark;
	
	public RewardRecord() {
		super();
		// TODO Auto-generated constructor stub
	}
/**************************** get set ************************************/



	public long getRewardRecordId() {
		return rewardRecordId;
	}



	public void setRewardRecordId(long rewardRecordId) {
		this.rewardRecordId = rewardRecordId;
	}


	public String getRewardType() {
		return rewardType;
	}
	
	
	public void setRewardType(String rewardType) {
		this.rewardType = rewardType;
	}
	
	
	public double getDenomination() {
		return denomination;
	}



	public void setDenomination(double denomination) {
		this.denomination = denomination;
	}



	public User getUser() {
		return user;
	}



	public void setUser(User user) {
		this.user = user;
	}



	public Date getCreateDate() {
		return createDate;
	}



	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}



	public String getCreateBy() {
		return createBy;
	}



	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}



	public ActivityConfig getActivityConfig() {
		return activityConfig;
	}



	public void setActivityConfig(ActivityConfig activityConfig) {
		this.activityConfig = activityConfig;
	}



	public String getIsEnable() {
		return isEnable;
	}



	public void setIsEnable(String isEnable) {
		this.isEnable = isEnable;
	}



	public Date getUseDate() {
		return useDate;
	}



	public void setUseDate(Date useDate) {
		this.useDate = useDate;
	}



	public String getOrderNo() {
		return orderNo;
	}



	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}



	public String getFromBe() {
		return fromBe;
	}



	public void setFromBe(String fromBe) {
		this.fromBe = fromBe;
	}



	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}



	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}



	public String getLastUpdateBy() {
		return lastUpdateBy;
	}



	public void setLastUpdateBy(String lastUpdateBy) {
		this.lastUpdateBy = lastUpdateBy;
	}
	
	
	
	public String getStandbyOne() {
		return standbyOne;
	}
	public void setStandbyOne(String standbyOne) {
		this.standbyOne = standbyOne;
	}
	public String getStandbyTwo() {
		return standbyTwo;
	}
	public void setStandbyTwo(String standbyTwo) {
		this.standbyTwo = standbyTwo;
	}
	public int getStandbyThree() {
		return standbyThree;
	}
	public void setStandbyThree(int standbyThree) {
		this.standbyThree = standbyThree;
	}
	public int getStandbyFour() {
		return standbyFour;
	}
	public void setStandbyFour(int standbyFour) {
		this.standbyFour = standbyFour;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

	
	
	
}
