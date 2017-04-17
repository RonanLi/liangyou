package com.liangyou.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;


/**
 * The persistent class for the article_fields database table.
 * 
 */
@Entity
@Table(name="article_fields")
public class ArticleField implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int aid;

	private String files;

	//bi-directional one-to-one association to Article
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="aid")
	private Article article;

	public ArticleField() {
	}

	public int getAid() {
		return this.aid;
	}

	public void setAid(int aid) {
		this.aid = aid;
	}

	public String getFiles() {
		return this.files;
	}

	public void setFiles(String files) {
		this.files = files;
	}

	public Article getArticle() {
		return this.article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

}