package com.liangyou.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

//v1.8.0.4_u1 TGPROJECT-16 zf 2014-5-5 start
@Entity(name="vote_title")
public class VoteTitle {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;//主键
	@Column
	private String title;//投票小标题
	@Column
	private String type;//投票类型
	@Column
	private int min;//最小投票数
	@Column
	private int max;//最大投票数
	@ManyToOne
	@JoinColumn(name="vote_id")
	private Vote vote;//关联投票标题
	@Column
	private int status;//1:启用 ；0：不启用
	@OneToMany(mappedBy="voteTitle",cascade=CascadeType.ALL)
	private List<VoteOption> voteOption;//关联投票选项
	
	@OneToOne
	@JoinColumn(name="user_id")
	private User user;//关联投票用户
	@Temporal(TemporalType.TIMESTAMP)
	private Date addTime;//添加时间
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateTime;//修改时间
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<VoteOption> getVoteOption() {
		return voteOption;
	}
	public void setVoteOption(List<VoteOption> voteOption) {
		this.voteOption = voteOption;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Date getAddTime() {
		return addTime;
	}
	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public Vote getVote() {
		return vote;
	}
	public void setVote(Vote vote) {
		this.vote = vote;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	
}
//v1.8.0.4_u1 TGPROJECT-16 zf 2014-5-5 end