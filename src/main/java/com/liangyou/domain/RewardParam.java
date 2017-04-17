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

import com.liangyou.context.Global;

/**
 * @Desc 奖励面额参数表    映射类
 * @author yjt_anzi
 *
 */
@Entity
@Table(name = Global.TABLE_PREFIX +  "reward_param")
public class RewardParam implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="reward_param_id")
	private long rewardParamId;
	@Column(name="denomination")
	private double denomination;	//面额
	@Column(name="use_rules")
	private double useRules;	//使用规则（金额控制）
	@Column(name="counts")
	private Integer counts;		//数量（张）
	
	//FK activityID
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="activity_id")
	private ActivityConfig activity;
	
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
	
	public RewardParam() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**************************** get set ************************************/

	public long getRewardParamId() {
		return rewardParamId;
	}

	public void setRewardParamId(long rewardParamId) {
		this.rewardParamId = rewardParamId;
	}

	public double getDenomination() {
		return denomination;
	}

	public void setDenomination(double denomination) {
		this.denomination = denomination;
	}

	public double getUseRules() {
		return useRules;
	}

	public void setUseRules(double useRules) {
		this.useRules = useRules;
	}

	public Integer getCounts() {
		return counts;
	}

	public void setCounts(Integer counts) {
		this.counts = counts;
	}

	public ActivityConfig getActivity() {
		return activity;
	}

	public void setActivityId(ActivityConfig activity) {
		this.activity = activity;
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
