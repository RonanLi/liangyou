package com.liangyou.web.action.member;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.context.Constant;
import com.liangyou.domain.BorrowCollection;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.User;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.service.BorrowService;
import com.liangyou.service.UserService;
import com.liangyou.tool.Page;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.BaseAction;

@Namespace("/member/invest")
@ParentPackage("p2p-default") 
@Results({ @Result(name="member", type="redirect",location="/member/index.html")})
@InterceptorRefs(@InterceptorRef("mydefault"))
public class MemberInvestAction extends BaseAction {
	
	private static Logger logger = Logger.getLogger(MemberInvestAction.class);
	@Autowired
	private BorrowService borrowService;
	@Autowired
	private UserService userService;
	
	@Action(value="hasTender",results={@Result(name="success", type="ftl",location="/member/invest/tender.html")})
	public String hasTender() {
		User user = getSessionUser();
		String startTime = paramString("dotime1");
		String endTime = paramString("dotime2");
		SearchParam param=SearchParam.getInstance()
				.addParam("user", user)
				.addOrFilter("status",0,1)
				.addPage(paramInt("page"))
				.addParam("borrow.status",Operator.GTE, 6)
				.addOrder(OrderType.DESC, "id");
		if(!StringUtils.isBlank(startTime)){
			startTime+= " 00:00:00";
			param.addParam("addtime", Operator.GTE , DateUtils.getDate2(startTime));
		}
		if(!StringUtils.isBlank(endTime)){
			endTime+= " 23:59:59";
			param.addParam("addtime", Operator.LTE , DateUtils.getDate2(endTime));
		}
		PageDataList<BorrowTender> list=borrowService.getTenderList(param);
		request.setAttribute("page", list.getPage());
	    request.setAttribute("list", list.getList());
		request.setAttribute("param", new HashMap<String, String>());
		request.setAttribute("invest_type", "hasTender");
		return SUCCESS;
	}
	
	@Action(value="tendering",results={@Result(name="success", type="ftl",location="/member/invest/tendering.html")})
	public String tendering() {
		User user = getSessionUser();
		SearchParam param=SearchParam.getInstance()
				.addParam("status", 0)
				.addParam("borrow.type",Operator.NOTEQ,Constant.TYPE_FLOW) //流转标即投即扣
				.addParam("waitAccount", 0)
				.addParam("user", user)
				.addPage(paramInt("page"))
				.addOrder(OrderType.DESC, "id");
		PageDataList<BorrowTender> list=borrowService.getTenderList(param);
		request.setAttribute("page", list.getPage());
	    request.setAttribute("list", list.getList());
		request.setAttribute("param", new HashMap<String, String>());
		request.setAttribute("invest_type", "tendering");
		return SUCCESS;
	}
	@Action(value="notCollect",results={@Result(name="success", type="ftl",location="/member/invest/notCollect.html")})
	public String notCollect() {
		User user = getSessionUser();
		String startTime = paramString("dotime1");
		String endTime = paramString("dotime2");
		//v1.8.0.4 TGPROJECT-142 lx 2014-04-21 start
		SearchParam param=SearchParam.getInstance()
				.addParam("status", 0)
				.addParam("borrowTender.user", user)
				.addParam("borrowTender.waitAccount", Operator.GT, 0 )
				.addParam("borrowTender.status", 0)
				.addPage(paramInt("page"))
				.addOrder(OrderType.ASC, "repayTime");
		//v1.8.0.4 TGPROJECT-142 lx 2014-04-21 end
		if(!StringUtils.isBlank(startTime)){
			startTime+= " 00:00:00";
			param.addParam("repayTime", Operator.GTE , DateUtils.getDate2(startTime));
		}
		if(!StringUtils.isBlank(endTime)){
			endTime+= " 23:59:59";
			param.addParam("repayTime", Operator.LTE , DateUtils.getDate2(endTime));
		}
		PageDataList<BorrowCollection> list=borrowService.getCollectList(param);
		request.setAttribute("page", list.getPage());
	    request.setAttribute("list", list.getList());
		request.setAttribute("param", new HashMap<String, String>());
		request.setAttribute("invest_type", "notCollect");
		return SUCCESS;
	}
	
	@Action(value="hasCollect",results={@Result(name="success", type="ftl",location="/member/invest/hasCollect.html")})
	public String hasCollect() {
		User user = getSessionUser();
		String startTime = paramString("dotime1");
		String endTime = paramString("dotime2");
		SearchParam param=SearchParam.getInstance()
				.addJoin("borrowCollection", BorrowCollection.class)
				.addParam("status", 1)
				.addParam("borrowTender.user", user)
				.addPage(paramInt("page"))
				.addOrder(OrderType.DESC, "id");
		if(!StringUtils.isBlank(startTime)){
			startTime+= " 00:00:00";
			param.addParam("repayTime", Operator.GTE , DateUtils.getDate2(startTime));
		}
		if(!StringUtils.isBlank(endTime)){
			endTime+= " 23:59:59";
			param.addParam("repayTime", Operator.LTE , DateUtils.getDate2(endTime));
		}
		PageDataList<BorrowCollection> list=borrowService.getCollectList(param);
		request.setAttribute("page", list.getPage());
	    request.setAttribute("list", list.getList());
		request.setAttribute("param", new HashMap<String, String>());
		request.setAttribute("invest_type", "hasCollect");
		return SUCCESS;
	}
	
	@Action(value="collect",results={@Result(name="success", type="ftl",location="/member/invest/collect.html")})
	public String collect() {
		User user=getSessionUser();
		long userId=user.getUserId();
		int page=NumberUtils.getInt(request.getParameter("page"));
		int status=NumberUtils.getInt(request.getParameter("status"));
		SearchParam param=new SearchParam();
		param.addPage(page, Page.ROWS);
		param.addParam("borrowTender.user", new User(userId));
		param.addParam("status", status);
		//Collection cList=borrowService.getCollectionList(user_id, status, page, param);
		PageDataList cList = borrowService.getCollectList(param);
		java.util.Date date = Calendar.getInstance().getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        request.setAttribute("currentTime",sdf.format(date));   
        
		request.setAttribute("list", cList.getList());
		request.setAttribute("page", cList.getPage());
		//用户分页中的地址处理 
		//param.setStatus(status);
		request.setAttribute("param", param.toMap());
		request.setAttribute("status", status);
		return "success";
	}
	
	/**
	 *债权转让列表
	 * @return
	 */
	@Action(value="assignmentList",
			results={@Result(name="success", type="ftl",location="/member/invest/assignmentList.html")})
	public String assignmentList(){
		User user=getSessionUser();	
		user = userService.getUserById(user.getUserId());
		if(user == null){
			message("请您登陆", "/user/login.html");
			return MSG;
		}
		SearchParam tenderParam = SearchParam.getInstance();
		tenderParam.addParam("user", user);
		tenderParam.addParam("status", 0);
		tenderParam.addOrFilter("borrow.status", 6,7);
		tenderParam.addPage(paramInt("page"));
		tenderParam.addOrder(OrderType.DESC, "addtime");
		PageDataList<BorrowTender> dataList  = borrowService.getAssignmentBorrowTenders(tenderParam);
		setPageAttribute(dataList, tenderParam);
		return SUCCESS;
	}
	
}
