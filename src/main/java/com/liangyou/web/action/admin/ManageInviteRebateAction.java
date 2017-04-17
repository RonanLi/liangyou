package com.liangyou.web.action.admin;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.context.Constant;
import com.liangyou.context.ExcelType;
import com.liangyou.context.Global;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.AccountRecharge;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowFee;
import com.liangyou.domain.BorrowProperty;
import com.liangyou.domain.InviteUserRebate;
import com.liangyou.domain.OperateLog;
import com.liangyou.domain.OperateProgress;
import com.liangyou.domain.Rule;
import com.liangyou.domain.User;
import com.liangyou.exception.ManageBussinessException;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.model.borrow.BorrowHelper;
import com.liangyou.model.borrow.BorrowModel;
import com.liangyou.service.InviteUserRebateService;
import com.liangyou.tool.jcaptcha.CaptchaServiceSingleton;
import com.liangyou.tool.jxl.ExcelHelper;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.BaseAction;
import com.octo.captcha.service.CaptchaServiceException;
/**
 * 
 * @author lx TGPROJECT-302 add
 *
 */
@Namespace("/admin/inviteuserrebate")
@ParentPackage("p2p-default")
@InterceptorRefs(@InterceptorRef("manageAuthStack"))
public class ManageInviteRebateAction extends BaseAction {
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(ManageInviteRebateAction.class);
	@Autowired
	private InviteUserRebateService inviteUserRebateService;
	
	/**
	 * 提成记录
	 * @return
	 * @throws Exception
	 */
	@Action(value="inviterebatelist",
		results={@Result(name="inviterebatelist",type="ftl",location="/admin/userinfo/inviterebate/inviterebatelist.html")}
		)
	public String inviteRebateList() throws Exception {
		//用户名
		String username = paramString("username");	
		SearchParam param=SearchParam.getInstance().addPage(paramInt("page"));
		//状态
		String status = paramString("status");
		int borrowId=paramInt("borrowId");
		if(!StringUtils.isBlank(username)){
			param.addParam("inviteUser.username", Operator.LIKE, username);
		}
		if(!StringUtils.isBlank(status)) {
			param.addParam("status", NumberUtils.getInt(status));
		}
		if(borrowId!=0) {
			param.addParam("borrowTender.borrow.id", borrowId);
		}
		param.addOrder(OrderType.DESC, "addtime");
		PageDataList<InviteUserRebate> list=inviteUserRebateService.getInviteUserRebateListBySearchParam(param);
		setPageAttribute(list, param);
		if(StringUtils.isBlank(actionType)){
			return "inviterebatelist";
		}else{
			List<InviteUserRebate> listExport=inviteUserRebateService.getExportInviteUserRebateList(param);
			String contextPath = ServletActionContext.getServletContext().getRealPath("/");
			String downloadFile="推荐提成_"+DateUtils.dateStr3(new Date())+".xls";
			String infile=contextPath+"/data/export/"+downloadFile;
			String[] names=ExcelType.get_manager_invite_rebate_names();
			String[] titles=ExcelType.get_manager_invite_rebate_titles();
			ExcelHelper.writeExcel(infile, listExport, InviteUserRebate.class, Arrays.asList(names), Arrays.asList(titles));
			this.export(infile, downloadFile);
			return null;
			
		}
	}
	
	/**
	 * 审核
	 * @return
	 */
	@Action(value="verify",
			results={@Result(name="success",type="ftl",location="/admin/userinfo/inviterebate/verify.html")})
	public String verify() throws Exception {
		long id=paramLong("id");
		InviteUserRebate iur=inviteUserRebateService.getInviteUserRebateById(id);
		if(iur!=null){
			double rebateProportion=inviteUserRebateService.calculateRebate(iur);
			double rebateAmount=iur.getBorrowFee()*rebateProportion;
			iur.setRebateAmount(rebateAmount);
			iur.setRebateProportion(rebateProportion+"");
			inviteUserRebateService.update(iur);
			request.setAttribute("iur", iur);
			return "success";
		}
		message("记录异常，请联系系统管理员！");
		return ADMINMSG;
	}
	/**
	 * 审核推荐提成  
	 * @return
	 */
	@Action(value="verifyInviteRebate")
	public String verifyInviteRebate() throws Exception {
			checkAdminValidImg();//验证验证码
			int  status=paramInt("status");
			long id=paramLong("id");
			InviteUserRebate iur=inviteUserRebateService.getInviteUserRebateById(id);
			User authUser = getAuthUser();
			if(status ==1 && iur!=null){
				if(iur.getStatus()!=0){
					message("该记录已审核，请勿重复操作！");
					return ADMINMSG;
				}
				iur.setVerifyUser(authUser);
				iur.setStatus(status);
				iur.setVerifytime(new Date());
				
				AccountRecharge r=new AccountRecharge();
				r.setUser(iur.getInviteUser());
				r.setType("1");
				r.setAddtime(new Date());
				r.setAddip(this.getRequestIp());
				r.setPayment("");
				r.setMoney(iur.getRebateAmount());
				r.setRemark("推荐提成");
				r.setStatus(1);
				r.setVerifyRemark("推荐提成发放，收到推荐提成金额："+iur.getRebateAmount()+" 元");
				AccountLog log=new AccountLog(Constant.ADMIN_ID,Constant.INVITE_REBATE,iur.getInviteUser().getUserId(),
						"推荐提成发放，收到推荐提成金额："+iur.getRebateAmount()+" 元", getRequestIp());
				try {
					inviteUserRebateService.verify(iur,log,r);
					super.systemLogAdd(authUser, 20, "用户线下充值,金额:"+r.getMoney()+",订单号:"+r.getTradeNo());
				} catch (Exception e) {
					e.printStackTrace();
					logger.info("推荐提成发放失败："+e.getMessage());
					message("推荐提成发放失败:"+e.getMessage() , "/admin/inviteuserrebate/inviterebatelist.html");
					return ADMINMSG;
				}
				message("推荐提成发放成功" , "/admin/inviteuserrebate/inviterebatelist.html");
				super.systemLogAdd(authUser, 48, "管理员后台充值");
			}else if(status ==2 && iur!=null){
				if(iur.getStatus()!=0){
					message("该记录已审核，请勿重复操作！");
					return ADMINMSG;
				}
				iur.setVerifyUser(getAuthUser());
				iur.setStatus(status);
				iur.setVerifytime(new Date());
				inviteUserRebateService.cancelInviteRebate(iur);
				this.systemLogAdd(authUser, 50, "取消用户推荐提成成功");
				message("取消推荐提成成功!","/admin/inviteuserrebate/inviterebatelist.html");
			}else{
				message("非法操作！");
			}
			return ADMINMSG;
	}
	
	
	/**
	 * 审核不通过推荐提成  
	 * @return
	 */
	@Action(value="cancelInviteRebate")
	public String cancelInviteRebate() throws Exception {
			int  status=paramInt("status");
			long id=paramLong("id");
			InviteUserRebate iur=inviteUserRebateService.getInviteUserRebateById(id);
			User authUser = getAuthUser();
			if(status ==2 && iur!=null){
				if(iur.getStatus()!=0){
					message("该记录已审核，请勿重复操作！");
					return ADMINMSG;
				}
				iur.setVerifyUser(getAuthUser());
				iur.setStatus(status);
				iur.setVerifytime(new Date());
				inviteUserRebateService.cancelInviteRebate(iur);
				this.systemLogAdd(authUser, 50, "取消用户推荐提成成功");
				message("取消推荐提成成功!","/admin/inviteuserrebate/inviterebatelist.html");
			}else{
				message("非法操作！");
			}
			return ADMINMSG;
	}
	
	
	
	/**
	 * 批量审核推荐提成  
	 * @return
	 */
	@Action(value="verifyInviteRebateList")
	public String verifyInviteRebateList() throws Exception {
		String ids = request.getParameter("ids");
		String[] values = null;
		if(!StringUtils.isBlank(ids)){			
			values = ids.substring(0, ids.length()-1).split(",");
			for(int i=0;i<values.length;i++){
				int id = NumberUtils.getInt(values[i].trim());
				logger.info("当前处理推荐提成：" + id);
				if(id!=0){
					InviteUserRebate iur=inviteUserRebateService.getInviteUserRebateById(id);
					User authUser = getAuthUser();
					if( iur!=null){
						if(iur.getStatus()!=0){
							continue;
						}
						double rebateProportion=inviteUserRebateService.calculateRebate(iur);
						double rebateAmount=iur.getBorrowFee()*rebateProportion;
						iur.setRebateAmount(rebateAmount);
						iur.setRebateProportion(rebateProportion+"");
						iur.setVerifyUser(authUser);
						iur.setStatus(1);
						iur.setVerifytime(new Date());
						
						AccountRecharge r=new AccountRecharge();
						r.setUser(iur.getInviteUser());
						r.setType("1");
						r.setAddtime(new Date());
						r.setAddip(this.getRequestIp());
						r.setPayment("");
						r.setMoney(iur.getRebateAmount());
						r.setRemark("推荐提成");
						r.setStatus(1);
						r.setVerifyRemark("推荐提成发放，收到推荐提成金额："+iur.getRebateAmount()+" 元");
						AccountLog log=new AccountLog(Constant.ADMIN_ID,Constant.INVITE_REBATE,iur.getInviteUser().getUserId(),
								"推荐提成发放，收到推荐提成金额："+iur.getRebateAmount()+" 元", getRequestIp());
						try {
							inviteUserRebateService.verify(iur,log,r);
							super.systemLogAdd(authUser, 20, "用户线下充值,金额:"+r.getMoney()+",订单号:"+r.getTradeNo());
						} catch (Exception e) {
							e.printStackTrace();
							logger.info("推荐提成发放失败："+e.getMessage());
							message("推荐提成发放失败:"+e.getMessage() , "/admin/inviteuserrebate/inviterebatelist.html");
							return ADMINMSG;
						}
						super.systemLogAdd(authUser, 48, "管理员后台充值");
					}else{
						message("非法操作！");
					}
				}					
			}
		}
		
		message("批量推荐提成发放成功" , "/admin/inviteuserrebate/inviterebatelist.html");
		return ADMINMSG;
	}
	
	
	/**
	 * 审核不通过推荐提成  
	 * @return
	 */
	@Action(value="cancelInviteRebateList")
	public String cancelInviteRebateList() throws Exception {
		String ids = request.getParameter("ids");
		String[] values = null;
		if(!StringUtils.isBlank(ids)){			
			values = ids.substring(0, ids.length()-1).split(",");
			for(int i=0;i<values.length;i++){
				int id = NumberUtils.getInt(values[i].trim());
				logger.info("当前处理推荐提成" + id);
				if(id!=0){
					InviteUserRebate iur=inviteUserRebateService.getInviteUserRebateById(id);
					User authUser = getAuthUser();
					if(iur!=null){
						if(iur.getStatus()!=0){
							continue;
						}
						double rebateProportion=inviteUserRebateService.calculateRebate(iur);
						double rebateAmount=iur.getBorrowFee()*rebateProportion;
						iur.setRebateAmount(rebateAmount);
						iur.setRebateProportion(rebateProportion+"");
						iur.setVerifyUser(getAuthUser());
						iur.setStatus(2);
						iur.setVerifytime(new Date());
						inviteUserRebateService.cancelInviteRebate(iur);
						this.systemLogAdd(authUser, 50, "取消用户推荐提成成功");
					}else{
						message("非法操作！");
					}
				}					
			}
		}
			
		message("批量取消推荐提成成功!","/admin/inviteuserrebate/inviterebatelist.html");
			return ADMINMSG;
	}
	
	
	/**
	 * 校验校验码是否正确  
	 * @param valid
	 * @return
	 */
	protected void checkAdminValidImg(){
		if(isOpenValidCode()){
			boolean b = false;		
			String validCode = paramString("validCode");
			if(StringUtils.isBlank(validCode)) {
				throw new ManageBussinessException("温馨提示：请输入验证码！");
			}
			try {
				b = CaptchaServiceSingleton.getInstance().validateResponseForID(request.getSession().getId(), validCode.toLowerCase());
			} catch (CaptchaServiceException e) {			
				throw new ManageBussinessException("温馨提示：您输入的验证码不正确，请重新输入！");			
			}
			if(!b) {
				throw new ManageBussinessException("温馨提示：您输入的验证码不正确，请重新输入！");	
			}
		}
	} 
	
}
