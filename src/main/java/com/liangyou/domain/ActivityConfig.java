package com.liangyou.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.liangyou.context.Global;

/**
 * @Desc 活动配置表  映射类
 * @author yjt_anzi
 *
 */
@Entity
@Table(name = Global.TABLE_PREFIX +  "activity_config")
public class ActivityConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="config_id")
	private long configId;
	@Column(name="activity_name")
	private String activityName;
	@Column(name="get_point")
	private String getPoint;	//获取时点 (invest：投标)
	@Column(name="get_rules")
	private double getRules;	//获取规则 (金额控制)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="activity_date_start")
	private Date activityDateStart;  //活动开始时间
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="activity_date_end")
	private Date activityDateEnd;
	@Column(name="is_enable")
	private String isEnable;	//活动是否生效
	@Column(name="enable_number")
	private String enableNumber;	//生效次数 (1：生效一次....)
	@Column(name="reward_type")
	private String rewardType;
	@Column(name="reward_time_limit")
	private Integer rewardTimeLimit; //有效期（/天）
	@Column(name="borrow_rules")
	private String borrowRules;
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
	
	@OneToMany(mappedBy="activity",fetch=FetchType.LAZY)
	private List<RewardParam> rewardParam;
	
	@OneToMany(mappedBy="activityConfig",fetch=FetchType.LAZY)
	private List<RewardUseRecord> rewardUseReward;
	
	public ActivityConfig() {
		super();
		// TODO Auto-generated constructor stub
	}
/**************************** get set ************************************/

	public long getConfigId() {
		return configId;
	}

	public void setConfigId(long configId) {
		this.configId = configId;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getGetPoint() {
		return getPoint;
	}

	public void setGetPoint(String getPoint) {
		this.getPoint = getPoint;
	}

	public double getGetRules() {
		return getRules;
	}

	public void setGetRules(double getRules) {
		this.getRules = getRules;
	}

	public Date getActivityDateStart() {
		return activityDateStart;
	}

	public void setActivityDateStart(Date activityDateStart) {
		this.activityDateStart = activityDateStart;
	}

	public Date getActivityDateEnd() {
		return activityDateEnd;
	}

	public void setActivityDateEnd(Date activityDateEnd) {
		this.activityDateEnd = activityDateEnd;
	}

	public String getIsEnable() {
		return isEnable;
	}

	public void setIsEnable(String isEnable) {
		this.isEnable = isEnable;
	}

	public String getEnableNumber() {
		return enableNumber;
	}

	public void setEnableNumber(String enableNumber) {
		this.enableNumber = enableNumber;
	}

	public String getRewardType() {
		return rewardType;
	}

	public void setRewardType(String rewardType) {
		this.rewardType = rewardType;
	}

	public int getRewardTimeLimit() {
		return rewardTimeLimit;
	}

	public void setRewardTimeLimit(Integer rewardTimeLimit) {
		this.rewardTimeLimit = rewardTimeLimit;
	}

	public String getBorrowRules() {
		return borrowRules;
	}

	public void setBorrowRules(String borrowRules) {
		this.borrowRules = borrowRules;
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
	public List<RewardParam> getRewardParam() {
		return rewardParam;
	}
	public void setRewardParam(List<RewardParam> rewardParam) {
		this.rewardParam = rewardParam;
	}
	public List<RewardUseRecord> getRewardUseReward() {
		return rewardUseReward;
	}
	public void setRewardUseReward(List<RewardUseRecord> rewardUseReward) {
		this.rewardUseReward = rewardUseReward;
	}
	

}
