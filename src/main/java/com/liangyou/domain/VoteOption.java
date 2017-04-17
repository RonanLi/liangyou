package com.liangyou.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
//v1.8.0.4_u1 TGPROJECT-16 zf 2014-5-5 start
@Entity(name="vote_option")
public class VoteOption {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;//主键
	
	@Column
	private String name;//投票项名称
	@Column
	private int num;//投票数
	
	@ManyToOne
	@JoinColumn(name="title_id")
	private VoteTitle voteTitle;//关联投票标题
	@ManyToOne
	@JoinColumn(name="vote_id")
	private Vote vote;//关联投票
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date addTime;//添加时间
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateTime;//修改时间
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public VoteTitle getVoteTitle() {
		return voteTitle;
	}
	public void setVoteTitle(VoteTitle voteTitle) {
		this.voteTitle = voteTitle;
	}
	public Date getAddTime() {
		return addTime;
	}
	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
	public Vote getVote() {
		return vote;
	}
	public void setVote(Vote vote) {
		this.vote = vote;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
}//v1.8.0.4_u1 TGPROJECT-16 zf 2014-5-5 end
