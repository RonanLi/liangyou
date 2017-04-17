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
 * 
 * @author lx add  TGPROJECT-302
 *
 */
@Entity
@Table(name = Global.TABLE_PREFIX+"rebate_proportion")
public class RebateProportion implements Serializable {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -6011454443375740470L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	/**
	 * 推荐投资金额开始区间
	 */
	@Column(name="begin_account")
	private  double beginAccount;
	/**
	 * 推荐投资金额结束区间
	 */
	@Column(name="end_account")
	private  double endAccount;
	/**
	 * 回扣比例
	 */
	private  double rebate;
	
	private int status;//是否启用，1启用，0不启用
	/**
	 *操作人
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="op_user")
	private  User opUser;
	/**
	 * 新增时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private  Date addtime;
	/**
	 *修改者
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="update_user")
	private  User updateUser;
	
	/**
	 *修改时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private  Date updatetime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getBeginAccount() {
		return beginAccount;
	}

	public void setBeginAccount(double beginAccount) {
		this.beginAccount = beginAccount;
	}

	public double getEndAccount() {
		return endAccount;
	}

	public void setEndAccount(double endAccount) {
		this.endAccount = endAccount;
	}

	public double getRebate() {
		return rebate;
	}

	public void setRebate(double rebate) {
		this.rebate = rebate;
	}

	public User getOpUser() {
		return opUser;
	}

	public void setOpUser(User opUser) {
		this.opUser = opUser;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public User getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(User updateUser) {
		this.updateUser = updateUser;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	

}