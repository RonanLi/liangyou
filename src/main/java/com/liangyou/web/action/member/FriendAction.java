package com.liangyou.web.action.member;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.context.ExcelType;
import com.liangyou.domain.Friend;
import com.liangyou.domain.InviteUser;
import com.liangyou.domain.InviteUserRebate;
import com.liangyou.domain.User;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.service.FriendService;
import com.liangyou.service.InviteUserRebateService;
import com.liangyou.service.UserService;
import com.liangyou.tool.coder.BASE64Encoder;
import com.liangyou.tool.jxl.ExcelHelper;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.BaseAction;
import com.opensymphony.xwork2.ModelDriven;

@Namespace("/member/friend")
@ParentPackage("p2p-default") 
public class FriendAction extends BaseAction implements ModelDriven<Friend>{

	private static Logger logger = Logger.getLogger(FriendAction.class);
	@Autowired
	private FriendService friendService;
	@Autowired
	private  UserService userService;
	//v1.8.0.4_u2 TGPROJECT-302  lx start
	@Autowired
	private InviteUserRebateService inviteUserRebateService;
	//v1.8.0.4_u2 TGPROJECT-302  lx end
	private Friend friend=new Friend();

	
	@Override
	public Friend getModel() {
		return friend;
	}
	
	@Action(value="myfriend",results={@Result(name="success", type="ftl",location="/member/friend/myfriend.html")}) 
	public String myfriend(){
		User user=getSessionUser();	
		int page=paramInt("page");
		SearchParam param = SearchParam.getInstance().addPage(page);
		param.addParam("inviteUser", new User(user.getUserId()));
		//List<Friend> list=friendService.getFriendsRequest(user.getUserId());//输出好友列表
		PageDataList<InviteUser>	list =userService.getInvitreUserBySearchParam(param);
		setPageAttribute(list, param); 
		
		return "success";
	}
	
	@Action(value="showmyfriend",results={@Result(name="success",type="ftl",location="/member/friend/myfriend.html")})
	public String showmyfriend() throws Exception{
		String username=request.getParameter("username");
		User user=getSessionUser();
		List list=friendService.selFriend(user.getUserId(), username);
		request.setAttribute("list", list);
		return "success";
		//return null;
	}
	
	@Action(value="delfriend",results={@Result(name="success", type="ftl",location="/member/friend/myfriend.html")})
	public String delfriend(){//删除好友
		User user=getSessionUser();
		String username=request.getParameter("username");
		friendService.delFriend(user.getUserId(), username);
		return "success";		
		
	}
	
	
	@Action(value="blackfriend",results={@Result(name="success", type="ftl",location="/member/friend/black.html")})
	public String blackfriend(){//添加黑名单
		User user=getSessionUser();
		String username=request.getParameter("username");
		String ip=getRequestIp();
		friendService.addBlackFriend(user.getUserId(), username,ip);
		return black();
	}

	
	@Action(value="request",results={@Result(name="success", type="ftl",location="/member/friend/request.html")})
	public String request(){//添加向你提出邀请的好友
		
		String idstr = request.getParameter("userid");
		User user=getSessionUser();
		List<Friend> list=friendService.readdFriend(user.getUserId());//输出邀请我的好友列表
		System.out.println(list.toString());
		request.setAttribute("list", list);
		return "success";
	}
	
	//v1.8.0.4_u2 TGPROJECT-302  lx start
	@Action(value="inviterebate",results={@Result(name="success", type="ftl",location="/member/friend/inviterebate.html")})
	public String inviterebate(){//添加向你提出邀请的好友
		User user=getSessionUser();
		//用户名
		String username = paramString("username");	
		String dotime1 = paramString("dotime1");
		String dotime2 = paramString("dotime2");
		SearchParam param=SearchParam.getInstance().addPage(paramInt("page"));
		int borrowId=paramInt("borrowId");
		if(!StringUtils.isBlank(username)){
			param.addParam("borrowTender.user.username", Operator.LIKE, username);
		}
		if(borrowId!=0) {
			param.addParam("borrowTender.borrow.id", borrowId);
		}
		if(!StringUtils.isBlank(dotime1)){
			param.addParam("addtime", Operator.GTE, DateUtils.getDate2(dotime1+" 00:00:00"));
		}
		if(!StringUtils.isBlank(dotime2)){
			param.addParam("addtime", Operator.LTE, DateUtils.getDate2(dotime2+" 23:59:59"));
		}
		
		param.addParam("status", 1);
		param.addParam("inviteUser", user);
		param.addOrder(OrderType.DESC, "addtime");
		PageDataList<InviteUserRebate> list=inviteUserRebateService.getInviteUserRebateListBySearchParam(param);
		param.addParam("dotime1",dotime1);
		param.addParam("dotime2", dotime2);
		setPageAttribute(list, param);
		return "success";
		
		
	}
	//v1.8.0.4_u2 TGPROJECT-302  lx end
	
	@Action(value="readdfriend",results={@Result(name="success", type="ftl",location="/member/friend/myfriend.html")})
	public String readdfriend(){//重新加为好友
		
		String username = request.getParameter("username");
		User user=getSessionUser();
		friendService.readdFriend(user.getUserId(),username);
		
		return myfriend();
	}
	
//  v1.8.0.4 TGPROJECT-60 lx 2014-04-15 start
	@Action(value="invite",results={@Result(name="success", type="ftl",location="/member/friend/invite.html")})
	public String invite(){//邀请好友
		User user = this.getSessionUser();
		long user_id = user.getUserId();
		//  v1.8.0.4 TGPROJECT-249 lx  start
		int page=paramInt("page");
		SearchParam param = SearchParam.getInstance().addPage(page);		
		param.addParam("inviteUser", new User(user.getUserId()));
		PageDataList<InviteUser>	list =userService.getInvitreUserBySearchParam(param);
		setPageAttribute(list, param); 
		//  v1.8.0.4 TGPROJECT-249 lx  end
		String userid=user_id+"";
		BASE64Encoder encoder = new BASE64Encoder();
		String s=encoder.encode((user.getUserId()+"").getBytes());
		request.setAttribute("userid", s);
		return "success";
	} 
//  v1.8.0.4 TGPROJECT-60 lx 2014-04-15 end
	@Action(value="black",results={@Result(name="success", type="ftl",location="/member/friend/black.html")})
	public String black(){
		User user=getSessionUser();
		List<Friend> list=friendService.getBlackList(user.getUserId());//输出黑名单友列表
		request.setAttribute("list", list);
		return "success";	
		
	} 

	@Action(value="addfriend",results={@Result(name="success", type="ftl",location="/member/friend/myfriend.html")})
	public String addfriend(){
		String id=request.getParameter("friends_id");
		String friend=request.getParameter("friend");
		String content=request.getParameter("content");
		friendService.addfriend(id, friend,content);
		return myfriend();
	} 


	
	
	

}
