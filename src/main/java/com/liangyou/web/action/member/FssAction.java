package com.liangyou.web.action.member;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.Friend;
import com.liangyou.domain.InviteUser;
import com.liangyou.domain.User;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.service.FriendService;
import com.liangyou.service.UserService;
import com.liangyou.tool.coder.BASE64Encoder;
import com.liangyou.web.action.BaseAction;
import com.opensymphony.xwork2.ModelDriven;

@Namespace("/member/fss")
@ParentPackage("p2p-default") 
public class FssAction extends BaseAction {

	private static Logger logger = Logger.getLogger(FssAction.class);
	@Autowired
	private FriendService friendService;
	@Autowired
	private  UserService userService;
	
	
	

	
	
	

}
