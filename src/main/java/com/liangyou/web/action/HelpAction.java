package com.liangyou.web.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;



@Namespace("/help")
@ParentPackage("p2p-default") 
public class HelpAction  extends BaseAction{

	@Action(value="list02",
			results={@Result(name="list02",type="ftl",location="/help/list02.html")}
			)
	public String list02() throws Exception {
		return "list02";
	}
	
	@Action(value="onlinehelp",
			results={@Result(name="success",type="ftl",location="/help/onlinehelp.html")}
			)
	public String onlinehelp() throws Exception {//注册
		return SUCCESS;
	} 
	
	@Action(value="loginhelp",
			results={@Result(name="success",type="ftl",location="/help/loginhelp.html")}
			)
	public String loginhelp() throws Exception {//登录
		
		return SUCCESS;
	} 

	
	@Action(value="identifyhelp",
			results={@Result(name="success",type="ftl",location="/help/identifyhelp.html")}
			)
	public String identifyhelp() throws Exception {//认证
		
		return SUCCESS;
	} 
	
	
	@Action(value="rechargehelp",
			results={@Result(name="success",type="ftl",location="/help/rechargehelp.html")}
			)
	public String rechargehelp() throws Exception {//充值
		
		return SUCCESS;
	} 
	
	
	@Action(value="cashhelp",
			results={@Result(name="success",type="ftl",location="/help/cashhelp.html")}
			)
	public String cashhelp() throws Exception {//提现
		
		return SUCCESS;
	} 
	
	@Action(value="tenderfhelp",
			results={@Result(name="success",type="ftl",location="/help/tenderfhelp.html")}
			)
	public String tenderfhelp() throws Exception {//发标
		
		return SUCCESS;
	} 
	
	@Action(value="tenderthelp",
			results={@Result(name="success",type="ftl",location="/help/tenderthelp.html")}
			)
	public String tenderthelp() throws Exception {//发标
		
		return SUCCESS;
	}
	
	@Action(value="accounthelp",
			results={@Result(name="success",type="ftl",location="/help/accounthelp.html")}
			)
	public String accounthelp() throws Exception {//发标
		
		return SUCCESS;
	}
	
	
	@Action(value="bankhelp",
			results={@Result(name="success",type="ftl",location="/help/bankhelp.html")}
			)
	public String bankhelp() throws Exception {//发标
		
		return SUCCESS;
	}
	
	@Action(value="licaihelp",
			results={@Result(name="success",type="ftl",location="/help/licaihelp.html")}
			)
	public String licaihelp() throws Exception {//发标
		
		return SUCCESS;
	}
	
	
	@Action(value="daikuanhelp",
			results={@Result(name="success",type="ftl",location="/help/daikuanhelp.html")}
			)
	public String daikuanhelp() throws Exception {//发标
		
		return SUCCESS;
	}
	
}
