package com.liangyou.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the credit database table.
 * 封装了获取key 的方法
 * 星级排名
 */

@Entity
@Table(name="star_rank")
public class StarRank implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	private int rank;

	@Column(name="score_start")
	private double scoreStart;
	
	@Column(name="score_end")
	private double scoreEnd;
	
	private String pic;
	
	private String type;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public double getScoreStart() {
		return scoreStart;
	}

	public void setScoreStart(double scoreStart) {
		this.scoreStart = scoreStart;
	}

	public double getScoreEnd() {
		return scoreEnd;
	}

	public void setScoreEnd(double scoreEnd) {
		this.scoreEnd = scoreEnd;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}