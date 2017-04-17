package com.liangyou.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.liangyou.domain.WeChatKfAccount;
import com.liangyou.domain.WeChatAutoReply;
import com.liangyou.domain.WeChatGraphicMsg;
import com.liangyou.domain.WeChatImage;
import com.liangyou.domain.WeChatMenu;
import com.liangyou.domain.WeChatMenuGroup;
import com.liangyou.domain.WeChatMsg;
import com.liangyou.domain.WeChatUser;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.tool.wechatkit.SnsAccessToken;

/**
 * @author lijing
 *微信 服务层
 */
public interface WeChatService {

	/**
	 * 微信接入
	 * @param request
	 * @param response
	 */
	void handlerConfig(HttpServletRequest request, HttpServletResponse response);
	void handlerMsg(HttpServletRequest request, HttpServletResponse response);
	
	/**
	 * 客服功能
	 */
	WeChatMsg getWechatMsgById(long id);
	void delWeChatMsg(long parseLong);
	void updateWeChatMsg(WeChatMsg msg);
	WeChatKfAccount findWeChatKfAccountById(long parseLong);
	String sendCustomTextMsg(WeChatMsg weChatMsg);
	String sendCustomImgMsg(String filesFileName, String openid);
	String sendCustomVoiceMsg(String filesFileName, String openid);
	String sendCustomVideoMsg(String filesFileName, String openid);
	String sendCustomMusicMsg(String filesFileName, String openid);
	String sendCustomNewsMsg(WeChatGraphicMsg weChatMsg,String openid);

	/**
	 * 客服管理
	 * @param kfAccount
	 * @return
	 */
	String createCustomService(WeChatKfAccount kfAccount);
	String modifyCustomService(WeChatKfAccount kfAccount);
	String delCustomService(Long id);
	List<WeChatKfAccount> getCustomList();
	String uploadHeadImg(String filePath, String account);
	String getArticleTotal(String bodyJson);
	String getUserShare(String bodyJson);
	PageDataList<WeChatUser> getWeChatUserList(SearchParam param);
	
	/**
	 * 菜单功能
	 * @param gid
	 * @return
	 */
	String addWeChatMenu(String gid);
	String delWechatMenu();
	void delMenu(long id);
	void delMenuGroup(long id);
	void saveMenuGroup(WeChatMenuGroup weChatMenuGroup);
	void saveMenu(WeChatMenu weChatMenu);
	PageDataList<WeChatMsg> getWechatMsg(SearchParam param);
	PageDataList<WeChatMenuGroup> getMenuGroup(SearchParam param);
	PageDataList<WeChatMenu> showMenu(SearchParam param);
	WeChatMenuGroup findWeChatMenuGroupById(long id);
	void updateMenuGroup(WeChatMenuGroup weChatMenuGroup);
	List<WeChatMenu> getParentMenu(SearchParam param);
	WeChatMenu findWeChatMenuById(Long parentid);
	
	/**
	 *图文消息
	 */
	PageDataList<WeChatGraphicMsg> getWeChatGraphicMsg(SearchParam param);
	String delGraphicMsg(Long id);
	String syncGraphicMsg();
	WeChatGraphicMsg findGraphicMsgById(Long id);
	List<WeChatGraphicMsg> findGraphicMsgByIds(String[] ids);
	String updateWeChatGraphicMsg(WeChatGraphicMsg weChatGraphicMsg);
	void saveWeChatGraphicMsg(WeChatGraphicMsg weChatGraphicMsg);
	JSONObject uploadGraphicMsg(List<WeChatGraphicMsg> msgNewsList);
	String uploadGraphicMsgImage(String filesFileName);
	PageDataList<WeChatImage> getWeChatImage(SearchParam param);
	
	/**
	 * 自动回复
	 * @param weChatAutoReply
	 */
	void mergeWeChatAutoReply(WeChatAutoReply weChatAutoReply);
	void delWeChatAutoReply(long id);
	PageDataList<WeChatAutoReply> getWeChatAutoReply(SearchParam param);
	WeChatUser findWeChatUserById(String openid);
	WeChatAutoReply findWechatARepById(long parseLong);
	
	
	
	/**
	 * 群发服务
	 */
	String sendAllGraphicMsg(WeChatGraphicMsg msg);
	String sendAllTextMsg(String content);
	String sendAllVoiceMsg(String filesFileName);
	String sendAllImgMsg(String filesFileName);
	String sendAllVideoMsg(String filesFileName,String content,String description);

	/**
	 * 微信粉丝
	 * @return
	 */
	String syncWeChatUserList();
	WeChatUser doLogin(SearchParam param);
	
	/**
	 * oauth2认证
	 * @param code
	 * @return 
	 */
	SnsAccessToken getOAuth2Token(String code);
	SnsAccessToken refreshToken(String string);
	WeChatUser getWeChatUserInfo(SnsAccessToken snsAccessToken);
	
	

}
