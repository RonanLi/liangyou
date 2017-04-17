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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.liangyou.context.Global;

/**
 * @Desc 奖励使用记录表  映射类
 * @author yjt_anzi
 *
 */
@Entity
@Table(name = Global.TABLE_PREFIX +  "reward_use_record")
public class RewardUseRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="reward_use_record_id")
	private long rewardUseRecordId;
	
	// FK_RewardRecordId 奖励ID
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="reward_record_id") 
	private RewardRecord rewardRecord;
	
	
	//FK_RURUserId	用户ID
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;
	
	
	//FK_RURActivityConfigId  活动ID
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="activity_id")
	private ActivityConfig activityConfig;
	
	@Column(name="order_no")
	private String orderNo;	//订单号
	@Column(name="is_enable")
	private String isEnable;
	@Column(name="note")
	private String note;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_date")
	private Date createDate;
	@Column(name="create_by")
	private String createBy;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="last_update_date")
	private Date lastUpdateDate;
	@Column(name="last_update_by")
	private String lastUpdateBy;
	
	
	public RewardUseRecord() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**************************** get set ************************************/


	public long getRewardUseRecordId() {
		return rewardUseRecordId;
	}


	public void setRewardUseRecordId(long rewardUseRecordId) {
		this.rewardUseRecordId = rewardUseRecordId;
	}


	public RewardRecord getRewardRecord() {
		return rewardRecord;
	}


	public void setRewardRecord(RewardRecord rewardRecord) {
		this.rewardRecord = rewardRecord;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public ActivityConfig getActivityConfig() {
		return activityConfig;
	}


	public void setActivityConfig(ActivityConfig activityConfig) {
		this.activityConfig = activityConfig;
	}


	public String getOrderNo() {
		return orderNo;
	}


	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}


	public String getIsEnable() {
		return isEnable;
	}


	public void setIsEnable(String isEnable) {
		this.isEnable = isEnable;
	}


	public String getNote() {
		return note;
	}


	public void setNote(String note) {
		this.note = note;
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



}
