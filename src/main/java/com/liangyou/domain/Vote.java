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
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

//v1.8.0.4_u1 TGPROJECT-270 zf 2014-5-8 start
@Entity
public class Vote {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;//主键
	@Column
	private String title;//投票标题
	@Column
	private int status;//1:启用 ；0：不启用；2：删除
	
	@Column
	@Lob
	private String description;//投票说明
	@OneToMany(mappedBy="vote",cascade=CascadeType.ALL)
	private List<VoteTitle> voteTitle;//关联投票小标题
	
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
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public List<VoteTitle> getVoteTitle() {
		return voteTitle;
	}

	public void setVoteTitle(List<VoteTitle> voteTitle) {
		this.voteTitle = voteTitle;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
}
//v1.8.0.4_u1 TGPROJECT-16 zf 2014-5-8 end