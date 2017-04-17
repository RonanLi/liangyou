package com.liangyou.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * @author wujing 
 * @version  创建时间：2013-12-19 上午10:22:34
 * 类说明:用于存放投标这信息,例如：投标备注等等
 */
@Entity(name="tender_property")
public class TenderProperty {
	
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tender_id",updatable=false)
	private BorrowTender borrowTender;
	private String content;
	
	@Column(name="tender_no")
	private String tenderNo;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public BorrowTender getBorrowTender() {
		return borrowTender;
	}

	public void setBorrowTender(BorrowTender borrowTender) {
		this.borrowTender = borrowTender;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTenderNo() {
		return tenderNo;
	}

	public void setTenderNo(String tenderNo) {
		this.tenderNo = tenderNo;
	}

}
