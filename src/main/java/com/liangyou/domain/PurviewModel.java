package com.liangyou.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**封装  json返回值
 * @author 1432
 *
 */
@Entity
public class PurviewModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	private String name;
	private String url;
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	public PurviewModel() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public PurviewModel(String name, String url) {
		super();
		this.name = name;
		this.url = url;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
