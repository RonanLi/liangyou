package com.liangyou.domain;
//package com.rongdu.domain;
//
//import java.io.Serializable;
//
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.OneToOne;
//import javax.persistence.Table;
//
//@Entity
//@Table(name="borrow_file")
//public class BorrowFile implements Serializable {
//
//	@Id	
//	@GeneratedValue(strategy=GenerationType.AUTO)
//	private long id;
//	
//	@OneToOne
//	@JoinColumn(name="borrow_id")
//	private Borrow borrow;
//	private String title;//图片标题
//	private String content;//内容说明
//	private String remark;//备注
//	private String path;//保存路径
//	
//	public long getId() {
//		return id;
//	}
//	public void setId(long id) {
//		this.id = id;
//	}
//	public Borrow getBorrow() {
//		return borrow;
//	}
//	public void setBorrow(Borrow borrow) {
//		this.borrow = borrow;
//	}
//	public String getPath() {
//		return path;
//	}
//	public void setPath(String path) {
//		this.path = path;
//	}
//	public String getTitle() {
//		return title;
//	}
//	public void setTitle(String title) {
//		this.title = title;
//	}
//	public String getContent() {
//		return content;
//	}
//	public void setContent(String content) {
//		this.content = content;
//	}
//	public String getRemark() {
//		return remark;
//	}
//	public void setRemark(String remark) {
//		this.remark = remark;
//	}
//	
//}
