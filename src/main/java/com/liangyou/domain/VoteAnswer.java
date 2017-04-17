package com.liangyou.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
//v1.8.0.4_u1 TGPROJECT-16 zf 2014-5-5 start
@Entity(name="vote_answer")
public class VoteAnswer {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;//主键
	@OneToOne
	@JoinColumn(name="vote_id")
	private Vote vote;//关联投票
	@OneToOne
	@JoinColumn(name="title_id")
	private VoteTitle voteTitle;//关联投票标题
	@OneToOne
	@JoinColumn(name="option_id")
	private VoteOption voteOption;//关联投票选项
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
	
	public Vote getVote() {
		return vote;
	}
	public void setVote(Vote vote) {
		this.vote = vote;
	}
	public VoteTitle getVoteTitle() {
		return voteTitle;
	}
	public void setVoteTitle(VoteTitle voteTitle) {
		this.voteTitle = voteTitle;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public VoteOption getVoteOption() {
		return voteOption;
	}
	public void setVoteOption(VoteOption voteOption) {
		this.voteOption = voteOption;
	}
	public Date getAddTime() {
		return addTime;
	}
	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
}//v1.8.0.4_u1 TGPROJECT-16 zf 2014-5-5 end
