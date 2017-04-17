package com.liangyou.model.wechatModel;
public class WeChatNewsListRS {

	private Long  id;
	private String digest;
	private Boolean send;
	private Boolean isOk;
	private String media_id;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDigest() {
		return digest;
	}
	public void setDigest(String digest) {
		this.digest = digest;
	}
	public Boolean getSend() {
		return send;
	}
	public void setSend(Boolean send) {
		this.send = send;
	}
	public Boolean getIsOk() {
		return isOk;
	}
	public void setIsOk(Boolean isOk) {
		this.isOk = isOk;
	}
	public String getMedia_id() {
		return media_id;
	}
	public void setMedia_id(String media_id) {
		this.media_id = media_id;
	}
	
	
}
