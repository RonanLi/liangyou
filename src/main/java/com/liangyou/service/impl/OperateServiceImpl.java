package com.liangyou.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.dao.OperateFlowDao;
import com.liangyou.dao.OperateLogDao;
import com.liangyou.dao.OperateProgressDao;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.OperateFlow;
import com.liangyou.domain.OperateLog;
import com.liangyou.domain.OperateProgress;
import com.liangyou.domain.User;
import com.liangyou.domain.UserType;
import com.liangyou.exception.ManageBussinessException;
import com.liangyou.model.SearchParam;
import com.liangyou.service.OperateService;
import com.liangyou.service.UserService;
import com.liangyou.util.StringUtils;

@Service(value="operateService")
@Transactional
public class OperateServiceImpl implements OperateService {
	
	@Autowired
	private OperateFlowDao operateFlowDao;
	@Autowired
	private OperateLogDao operateLogDao;
	@Autowired
	private UserService userService;
	@Autowired
	private OperateProgressDao operateProgressDao;
	
	
	
	@Override
	public void addOperateFlow(OperateFlow operaFlow) {
		int max = operateFlowDao.getMaxSortByType(operaFlow.getOperateType());
		if(operaFlow.getSort() <= max ){
			throw new ManageBussinessException("您输入的排序" + operaFlow.getSort() + "必须大于最小排序"+ max + "<你已经添加过的>", "/admin/borrow/showFlowRule.html");
		}
		operateFlowDao.save(operaFlow);
	}

	@Override
	public void deleteOperateFlow(int id) {
		operateFlowDao.delete(id);
		
	}

	@Override
	public void updateOperateFlow(OperateFlow operateFlow) {
		operateFlowDao.update(operateFlow);
		
	}

	@Override
	public List<OperateFlow> getOperateFlowByType(String type) {
		return operateFlowDao.getOperateFlowByType(type);
	}
	@Override
	public OperateFlow getOperateById(long id){
		return operateFlowDao.find(id);
	}

	@Override
	public void addOperateLog(OperateLog operateLog) {
		operateLogDao.save(operateLog);
		
	}

	@Override
	public void deleteOperateLog(int id) {
		operateLogDao.delete(id);
		
	}

	@Override
	public void updateOperateLog(OperateLog operateLog) {
		operateLogDao.update(operateLog);
	}
	
	@Override
	public List<OperateFlow> getOperateFlowListByType(String type) {
		return operateFlowDao.getOperateFlowByType(type);
	}
	
	@Override
	public List<OperateLog> getAllOperateLogByOrdNo(Borrow b) {
		List<OperateLog> list = operateLogDao.getAllBorrowOperateLogByOrdNo(b.getId());
		//网站垫付，查询一条还款计划的垫付记录
		if(b.getStatus() == 6 ||b.getStatus() == 7 || b.getStatus() == 8){
			List<OperateLog> list1 = operateLogDao.getAllRepayOperateLogByOrdNo(b.getBorrowRepayments().get(0).getId());
			list.addAll(list1);	
		}
	    return list;
	}

	@Override
	public List<OperateLog> getOperateLogByOrdNo(long id,String type) {
		return operateLogDao.getOperateLogByOrdNo(id,type);
	}
	
	@Override
	public OperateLog getLastOperateLogByOrdNo(String type, long orderNO){
		return operateLogDao.getLastOperateLogByOrdNo(type, orderNO);
	}
	

	@Override
	public int getMaxSortByType(String type) {
		return operateFlowDao.getMaxSortByType(type);
	}
	
	@Override
	public List<OperateFlow> getOperateFlowList(SearchParam searchParam) {
		return operateFlowDao.findAllPageList(searchParam).getList();
	}
	
	@Override
	public List<OperateProgress> getOperateProgressList(SearchParam searchParam) {
		return operateProgressDao.findAllPageList(searchParam).getList();
	}

	/**
	 * 查询审核的下一个可用的流程
	 * 例如：流程有1、2、3、4
	 * 当3不启用是，2审核后，应该由4来执行审核操作，过滤3
	 */
	@Override
	public OperateFlow getUseOperateFlowBytypeSort(String type, int sort) {
		OperateFlow operateFlow = operateFlowDao.getOperateFlow(type, sort);
		if (operateFlow.getStatus() == 1) {
			return operateFlow;
		}else{
			List<OperateFlow> operateList = getOperateFlowByType(type);
			for (int i = operateFlow.getSort()-1; i < operateList.size(); i++) {
				operateFlow = operateList.get(i);
				if (operateFlow.getStatus() == 1) {
					return operateFlow;
				}
			}
			return null;
		}
		
	}

	@Override
	public OperateFlow getOperateFlowBytypeSort(String type, int sort) {
		return operateFlowDao.getOperateFlow(type, sort);
	}

	@Override
	public int getMaxAllOperateSort(String type) {
		return operateFlowDao.getMaxAllSortByType(type);
	}
	
	/**
	 * 流程控制步骤：
	 * 1、根据用户的user, 查询用户类型， 用户的可用的操作步骤有哪些。
	 * 2、根据type, orderNo 查询 查询当前的进度，表 operate_progress
	 * 3、判断是否有权限进行此操作。
	 * 4、根据status 进一步做处理，0：返回上一步；1：成功处理； 2： 返回上一步。
	 */
	@Override
	public  boolean doOperate(String type,long id,User user,String memo,int status){//参数（标复审流程、标ID、当前后台用户、审核备注信息、审核状态）
		
		boolean result= true;
		user = userService.getUserById(user.getUserId());
		if(user == null){
			throw new ManageBussinessException("请您登陆后，进行操作");
		}
		UserType userType = user.getUserType();
		//查询当前操作的进度
		OperateProgress operateProgress =  operateProgressDao.getOperateProgressByTypeAndOrderNo(type, id);
		if(operateProgress !=null&&operateProgress.getStatus()==1){ throw new ManageBussinessException("此操作流程，已经结束请和管理员联系。");}
		//判断是否有权限
		if(operateProgress == null){//第一次操作，判断当前用户类型是否有权限，此时需要 插入记录 operateProgress
			//根据当前操作类型，查询该操作的第一个流程。
			OperateFlow of = operateFlowDao.getFirstFlowByType(type);
			if(of.getUserType().getTypeId() != userType.getTypeId()){
				throw new ManageBussinessException("您无权限操作此流程，请联系管理员");
			}
		    result = 	dealOperateFlowTask(memo, user, type, id, status, operateProgress, of);
		}else if(operateProgress.getStatus() == 0){//操作进行中，需要更新。
			//根据当前操作类型，查询
	        OperateFlow of = operateProgress.getNextOperateFlow();
	        if(of.getUserType().getTypeId()!=userType.getTypeId()){
	        	throw new ManageBussinessException("您无权限操作此流程，请联系管理员");
	        }
	        //参数（审核备注信息、当前登录用户、标复审流程、标ID、审核状态、审核进度、审核流程）
	        result =  dealOperateFlowTask(memo, user, type, id, status, operateProgress, of);
		}
		
		return result;
	}
	
	/**
	 * 具体处理流业务    //参数（审核备注信息、当前登录用户、标复审流程、标ID、审核状态、审核进度、审核流程）
	 * @return
	 */
	public boolean dealOperateFlowTask(String memo,User user,String type,long orderNo, int status, OperateProgress operateProgress, OperateFlow of){
		StringBuffer realDetail = new StringBuffer();
		realDetail.append(of.getName());
		
		//开始处理审核操作，将记录插入到operatelog表中
		OperateLog operateLog= new OperateLog();
		operateLog.setAddtime(new Date());
		operateLog.setMemo(memo);
		operateLog.setStatus(status);
		operateLog.setOperateUser(user);
		operateLog.setType(type);
		operateLog.setOrderNo(orderNo);
		
		if(operateProgress == null ){//第一次进来，没有进度数据，需要添加
			operateProgress = new OperateProgress();
		}
		
		operateProgress.setAddtime(new Date());
		
		operateProgress.setNowOperateFlow(of);
		operateProgress.setOperateType(type);
		operateProgress.setOperateUser(user);
		operateProgress.setOrderNo(orderNo);
		
		OperateFlow nextOf = null;
		String param  = Global.paramLocal.get()==null?null:Global.paramLocal.get().toString();
		Global.paramLocal.remove();
		if(status == 1 || status == 2){//处理成功/审核拒绝
			//根据当前工作流及操作类型，获取下一个可用工作流。
		    nextOf = operateFlowDao.getNextUsefullOperateFlow(of);
			if(status == 1){
				if(nextOf==null){//最后一个流程
					realDetail.append("/已经审核通过，全部流程结束");
					operateProgress.setStatus(1);
				}else{//还有下一个流程
					realDetail.append("/已经审核通过，等待" + nextOf.getName()+ "处理");
					operateProgress.setStatus(0);
				}
			}else if(status == 2){//直接拒绝
				realDetail.append("/审核不通过，全部流程结束");
				operateProgress.setStatus(1);
			}
			operateLog.setOperateDetail(realDetail.toString());
			operateProgress.setNextOperateFlow(nextOf);
		}else  if(status == 0 ){//返回上一步,如果是第一步，则不能进行。
			OperateFlow upOf = operateFlowDao.getUpUsefullOperateFlow(of);
			if(upOf == null){
				throw new ManageBussinessException("您是该环节的第一个流程，不能执行【返回上一个流程】的操作");
			}
			operateProgress.setStatus(0);
			operateProgress.setNowOperateFlow(new OperateFlow(of.getId()));
			operateProgress.setNextOperateFlow(new OperateFlow(upOf.getId()));
			operateLog.setOperateDetail("审核被" +user.getUsername() +"拒绝/等待" + upOf.getName()+"重新处理");
		}
		operateProgress.setParam(param);
		
		if(!StringUtils.isBlank(param)){
			//这里 特殊处理
			if("004".equals(type)){//垫付流程
				String desc = param.equals("1")?"/选择【垫付本期】":"/选择【垫付全部】";
				operateLog.setOperateDetail(operateLog.getOperateDetail() + desc); 
			}else  if("001".equals(type)){//标的初审
				operateLog.setOperateDetail(operateLog.getOperateDetail() + "" + param); 
			}
		}
		operateProgress = operateProgressDao.merge(operateProgress);
		operateLog.setOperateProgress(operateProgress);
		addOperateLog(operateLog);
		
		if( (nextOf==null&&status!=0) || status == 2 ){//最后一步，或拒绝审核 直接返回，并处理业务逻辑
			return true;
		}else{
			return false;
		}
	}

	@Override
	public int getCountOperateByTypeAndStatsu(String type, int status) {
		return operateProgressDao.getCountByStatusAndOperateType(type, status);
	}
	
	/**
	 * 根据当前类型和 id 查询出唯一的进程
	 * @param type
	 * @param id
	 * @return
	 */
	@Override
	public OperateProgress getOperateProgressByTypeAndOrderNo(String type, long id){
		return  operateProgressDao.getOperateProgressByTypeAndOrderNo(type, id);
	}
	
	/**
	 *更新指定流程
	 * @param op
	 */
	@Override
	public void updateOPerateProgress(OperateProgress op){
		operateProgressDao.update(op);
	}
	
	/**
	 * 判断用户能否，取消标
	 * @param orderNo
	 * @return
	 */
	@Override
	public boolean checkBorrowCanCancel(long orderNo){
		OperateProgress pr1 = operateProgressDao.getOperateProgressByTypeAndOrderNo(Constant.VERIFY_BORROW, orderNo);
		if(pr1 == null){
			return true;
		}else{
			return false;
		}
	}
	
	// v1.8.0.4 TGPROJECT-59 lx 2014-04-17 start
	public void updateOperateFlow(List<OperateFlow> operateFlowList){
		for (OperateFlow operateFlow : operateFlowList) {
			operateFlowDao.update(operateFlow);
		}
	}
	// v1.8.0.4 TGPROJECT-59 lx 2014-04-17 end
}
