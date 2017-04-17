package com.liangyou.service.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.dao.AccountDao;
import com.liangyou.dao.AccountLogDao;
import com.liangyou.dao.AccountRechargeDao;
import com.liangyou.dao.InviteUserDao;
import com.liangyou.dao.InviteUserRebateDao;
import com.liangyou.dao.RebateProportionDao;
import com.liangyou.domain.Account;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.AccountRecharge;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.ChinaPnrPayModel;
import com.liangyou.domain.InviteUser;
import com.liangyou.domain.InviteUserRebate;
import com.liangyou.domain.RebateProportion;
import com.liangyou.domain.User;
import com.liangyou.domain.YjfPay;
import com.liangyou.exception.ManageBussinessException;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.service.ApiService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.InviteUserRebateService;
import com.liangyou.service.MsgService;
import com.liangyou.util.OrderNoUtils;
import com.liangyou.util.StringUtils;
/**
 * 
 * @author lx TGPROJECT-302 add
 *
 */
@Service(value="inviteUserRebateService")
@Transactional
public class InviteUserRebateServiceImpl extends BaseServiceImpl implements InviteUserRebateService {

	private Logger logger=Logger.getLogger(InviteUserRebateServiceImpl.class);
	@Autowired
	InviteUserRebateDao inviteUserRebateDao;
	@Autowired
	private ApiService apiService;
	@Autowired
    private BorrowService borrowService;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private AccountLogDao accountLogDao;
	@Autowired
	private InviteUserDao inviteUserDao;
	@Autowired
	private MsgService msgService;
	@Autowired
	AccountRechargeDao accountRechargeDao;
	@Autowired
	RebateProportionDao rebateProportionDao; 
	
	@Override
	public PageDataList<InviteUserRebate> getInviteUserRebateListBySearchParam(SearchParam param){
		return inviteUserRebateDao.findPageList(param);
	}
	@Override
	public List<InviteUserRebate> getExportInviteUserRebateList(SearchParam param){
		return inviteUserRebateDao.findAllPageList(param).getList();
	}
	@Override
	public InviteUserRebate getInviteUserRebateById(long id){
		return inviteUserRebateDao.find(id);
	}
	@Override
	public void cancelInviteRebate(InviteUserRebate iur){
		inviteUserRebateDao.update(iur);
	}
	@Override
	public void verify(InviteUserRebate iur,AccountLog log,AccountRecharge ar) throws Exception{
		List<Object> taskList = new ArrayList<Object>();
		int apiType = Global.getInt("api_code");
		User rechargeUser = ar.getUser();
		if(rechargeUser.getApiStatus()==0 || StringUtils.isBlank(rechargeUser.getApiId())){
			throw new ManageBussinessException("该用户没有开通或激活"+Global.getValue("api_name")+"账户！！", "/admin/inviteuserrebate/inviterebatelist.html");
		}
		apiService.webRecharge(ar,taskList);//第三方接口
		if(borrowService.doApiTask(taskList)){//调用转账功能
			//修改账户金额
			Account act=accountDao.getAcountByUser(rechargeUser);
			accountDao.updateAccount(ar.getMoney(), ar.getMoney(), 0,rechargeUser.getUserId() );
			//修改订单状态
			ar.setStatus(1);
			String tradeNo = "";
			switch (apiType) {
			case 1://汇付
				ChinaPnrPayModel cpm = (ChinaPnrPayModel)taskList.get(0);
				tradeNo = cpm.getOrdId();
				break;
			case 2://易极付
				YjfPay yjf = (YjfPay)taskList.get(0);
				tradeNo = yjf.getOrderno();
				break;

			default:
				tradeNo = OrderNoUtils.getInstance().getSerialNumber();
				break;
			}
			ar.setTradeNo(tradeNo);
			ar.setRemark("获取推荐提成成功!");
			accountRechargeDao.save(ar);
			
			//插入资金记录表
			log.setUser(ar.getUser());
			User toUser = new User();
			log.setToUser(toUser);
			toUser.setUserId(Constant.ADMIN_ID);
			log.setMoney(ar.getMoney());
			log.setTotal(act.getTotal());
			log.setUseMoney(act.getUseMoney());
			log.setNoUseMoney(act.getNoUseMoney());
			log.setCollection(act.getCollection());
			log.setRepay(act.getRepay());
			accountLogDao.save(log);
			
			//更新
			inviteUserRebateDao.update(iur);
			//充值成功，发消息通知
			/*MsgReq req = new MsgReq();
			req.setReceiver(ar.getUser());
			req.setSender(new User(Constant.ADMIN_ID));
			req.setMsgOperate(this.msgService.getMsgOperate(8));
			req.setTime(DateUtils.dateStr4(new Date()));
			req.setMoney(""+ar.getMoney());
			DisruptorUtils.sendMsg(req);*/
		}else{
			throw new ManageBussinessException(Global.getString("api_name")+"处理出错！！！");
		}
	}
	/**
	 * 计算推荐人的回扣比例和金额
	 */
	@Override
	public Map<String, Double> calculateRebate(InviteUser iu,BorrowTender bt){
		List<InviteUser> inviteUserList=inviteUserDao.getInvitreUser(iu.getInviteUser().getUserId());
		Date d=bt.getBorrow().getVerifyTime();
		double rebateProportion=0.0;
		double rebateAmount=0.0;
		double calculateRebate=0.0;
		for (InviteUser iUser:inviteUserList) {
			calculateRebate+=inviteUserDao.getInvitreUser(iUser.getUser(), bt.getBorrow());
		}
		List<RebateProportion> rpList=rebateProportionDao.getRebateProportionAllList();
		for (RebateProportion rp:rpList) {
			if(rp.getBeginAccount()<calculateRebate && rp.getEndAccount()>=calculateRebate){
				rebateProportion=rp.getRebate();
				break;
			}
		}
		Map<String, Double> map=new HashMap<String, Double>();
		map.put("rebateProportion", rebateProportion);
		return map;
	}
	
	/**
	 * 计算推荐人的回扣比例和金额
	 */
	@Override
	public double calculateRebate(InviteUserRebate iur){
		List<InviteUser> inviteUserList=inviteUserDao.getInvitreUser(iur.getInviteUser().getUserId());
		double rebateProportion=0.0;
		double calculateRebate=0.0;
		for (InviteUser iUser:inviteUserList) {
			calculateRebate+=inviteUserDao.getInvitreUser(iUser.getUser(), iur.getBorrowTender().getBorrow());
		}
		List<RebateProportion> rpList=rebateProportionDao.getRebateProportionAllList();
		for (RebateProportion rp:rpList) {
			if(rp.getBeginAccount()<calculateRebate && rp.getEndAccount()>calculateRebate){
				rebateProportion=rp.getRebate();
				break;
			}
		}
		return rebateProportion;
	}
	
	@Override
	public void update(InviteUserRebate iur){
		inviteUserRebateDao.update(iur);
	}
}
