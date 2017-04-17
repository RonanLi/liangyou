package com.liangyou.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

/**
 * 微信图文消息
 * @author lijing
 *
 */
@Entity
public class WeChatGraphicMsg implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private Integer totalCount;//总数
	private Integer itemCount;//显示页数
	private String media_id;//新增微信图文消息成功后返回id
	private String title;// 标题
	private String thumb_media_id;// 图文消息的封面图片素材id（必须是永久mediaID）
	private Integer show_cover_pic;// 是否显示封面，0为false，即不显示，1为true，即显示
	private String author;// 作者
	private String digest;//图文消息的摘要，仅有单图文消息才有摘要，多图文此处为空 brief
	@Type(type = "text")
	private String content;// 消息的具体内容，支持HTML标签，必须少于2万字符，小于1M，且此处会去除JS,涉及图片url必须来源"上传图文消息内的图片获取URL"接口获取。外部图片url将被过滤。
	private String url;//图文页的URL，或者，当获取的列表是图片素材列表时，该字段是图片的URL
	private String content_source_url;// 图文消息的原文地址，即点击“阅读原文”后的URL fromurl
	private Date update_time;// 这篇图文消息素材的最后更新时间
	private String name;//文件名称
	private String picpath;//封面图片的微信端地址,用于上传至微信端生成响应得url上传成功后生成微信端访问的图片地址,不在是本服务器的地址
	private String picdir;//封面图片绝对目录
	private int type;//3 为我们菜单使用的图文消息
	private String msgtype;//消息类型;
	private String inputcode;//关注者发送的消息
	private String rule;//规则，目前是 “相等”
	private int enable;//是否可用 0 不可用 1 可用
	@Column(columnDefinition="0")
	private Integer readcount;//消息阅读数
	@Column(columnDefinition="0")
	private Integer favourcount;//消息点赞数
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}
	public Integer getItemCount() {
		return itemCount;
	}
	public void setItemCount(Integer itemCount) {
		this.itemCount = itemCount;
	}
	public String getPicdir() {
		return picdir;
	}
	public void setPicdir(String picdir) {
		this.picdir = picdir;
	}
	public String getMsgtype() {
		return msgtype;
	}
	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}
	public String getInputcode() {
		return inputcode;
	}
	public void setInputcode(String inputcode) {
		this.inputcode = inputcode;
	}
	public String getRule() {
		return rule;
	}
	public void setRule(String rule) {
		this.rule = rule;
	}
	public int getEnable() {
		return enable;
	}
	public void setEnable(int enable) {
		this.enable = enable;
	}
	public Integer getReadcount() {
		return readcount;
	}
	public void setReadcount(Integer readcount) {
		this.readcount = readcount;
	}
	public Integer getFavourcount() {
		return favourcount;
	}
	public void setFavourcount(Integer favourcount) {
		this.favourcount = favourcount;
	}
	public String getCreateTimeStr() {
		return createTimeStr;
	}
	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}
	@Transient
	private String createTimeStr;
	
	public String getPicpath() {
		return picpath;
	}
	public void setPicpath(String picpath) {
		this.picpath = picpath;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getMedia_id() {
		return media_id;
	}
	public void setMedia_id(String media_id) {
		this.media_id = media_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getThumb_media_id() {
		return thumb_media_id;
	}
	public void setThumb_media_id(String thumb_media_id) {
		this.thumb_media_id = thumb_media_id;
	}
	public Integer getShow_cover_pic() {
		return show_cover_pic;
	}
	public void setShow_cover_pic(Integer show_cover_pic) {
		this.show_cover_pic = show_cover_pic;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getDigest() {
		return digest;
	}
	public void setDigest(String digest) {
		this.digest = digest;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getContent_source_url() {
		return content_source_url;
	}
	public void setContent_source_url(String content_source_url) {
		this.content_source_url = content_source_url;
	}
	public Date getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "WeChatGraphicMsg [media_id=" + media_id + ", title=" + title + ", thumb_media_id=" + thumb_media_id
				+ ", show_cover_pic=" + show_cover_pic + ", author=" + author + ", digest=" + digest + ", content="
				+ content + ", url=" + url + ", content_source_url=" + content_source_url + ", update_time="
				+ update_time + ", name=" + name + "]";
	}
	
}
