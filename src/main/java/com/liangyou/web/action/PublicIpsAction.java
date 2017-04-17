package com.liangyou.web.action;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.api.ips.IpsAddBorrow;
import com.liangyou.api.ips.IpsModel;
import com.liangyou.api.ips.IpsNewCash;
import com.liangyou.api.ips.IpsRecharge;
import com.liangyou.api.ips.IpsRegister;
import com.liangyou.api.ips.IpsRepaymentNewTrade;
import com.liangyou.api.ips.IpsTenderNBorrow;
import com.liangyou.api.ips.Transfer;
import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.dao.BorrowTenderDao;
import com.liangyou.disruptor.DisruptorUtils;
import com.liangyou.domain.AccountBank;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.IpsPay;
import com.liangyou.domain.User;
import com.liangyou.exception.BussinessException;
import com.liangyou.model.BorrowParam;
import com.liangyou.model.APIModel.AccountCashModel;
import com.liangyou.model.APIModel.RechargeModel;
import com.liangyou.model.borrow.IpsRepaymentModel;
import com.liangyou.service.BorrowService;
import com.liangyou.service.UserService;
import com.liangyou.util.NumberUtils;

/**
 * 环迅回调业务处理
 * @author Administrator
 *
 */
@Namespace("/public/ips")
@ParentPackage("p2p-default")
public class PublicIpsAction extends BaseAction {
	Logger logger = Logger.getLogger(PublicIpsAction.class);
	
	@Autowired
	private BorrowTenderDao borrowTenderDao;
	
	@Autowired
	private UserService userService;
	@Autowired
	private BorrowService borrowService;
	
	@Action(value = "ipsRegisterReturn")
	public String ipsRegisterReturn(){
		String pErrCode = paramString("pErrCode");
		BorrowParam param = new BorrowParam();
		String resultFlag = pErrCode + "-" + System.currentTimeMillis() + "";
		param.setResultFlag(resultFlag);
		request.setAttribute("tenderFlag", resultFlag);
		request.setAttribute("ok_url", "/member/main.html"); // 成功返回地址
		request.setAttribute("back_url", "/member/main.html");// 失败返回地址
		request.setAttribute("r_msg", "恭喜您，开通环迅账号成功，请您返回查看");
		doRegister(param);
		return RESULT;
	}
	
	@Action(value = "ipsRegisterNotify")
	public String ipsRegisterNotify(){
		doRegister(null);
		return null;
	}
	
	/**
	 * 处理环迅开户业务
	 * @param param
	 */
	private void doRegister(BorrowParam param){
		logger.info("环迅开户回调参数打印"+getRequestParams());
		//验签
		
		IpsModel registerModel =doRegisterReturn();
		String xml = registerModel.checkSign();
		IpsRegister register =  new IpsRegister();
		register = register.doReturnCreate(xml);
		logger.info("memo:-------------->"+register.getMemo1());
		logger.info("ipsNo:-------------->"+register.getIpsAcctNo());
		if ((!"MG00000F".equals(registerModel.getErrCode())) || (!register.getStatus().equals("10"))) {   //判断返回码和开户状态，其中有一个失败时，开户就失败
			throw new BussinessException("开通环迅账户失败，原因："+register.getErrMsg(), "/member/index.html");
		}
		long userId = NumberUtils.getLong(register.getMemo1());
		User user = userService.getUserById(userId);
		if (user==null) {
			throw new BussinessException("参数有误，请联系客服！", "/member/index.html");	
		}
		user.setApiId(register.getIpsAcctNo());  //设置环迅账户
		DisruptorUtils.apiUserRegister(user, param);// 调用异步处理
		//处理银行卡
		AccountBank bank = new AccountBank();
		bank.setAccount(register.getBkAccNo());
		bank.setBankName(register.getBankName());
		DisruptorUtils.doIpsAddAccountBank(user, bank);
	}
	
	/**
	 * 发标页面回调处理
	 * @return
	 * @throws IOException 
	 */
	@Action("ipsAddBorrowReturn")
	public String ipsAddBorrowReturn() throws IOException{
	
		String pErrCode = paramString("pErrCode");
		BorrowParam param = new BorrowParam();
		String resultFlag = pErrCode+ "-" + System.currentTimeMillis() + "";
		param.setResultFlag(resultFlag);
		request.setAttribute("tenderFlag", resultFlag);
		request.setAttribute("ok_url", "/admin/borrow/showTrialBorrow.html"); // 成功返回地址
		request.setAttribute("back_url", "/admin/borrow/showTrialBorrow.html");// 失败返回地址
		request.setAttribute("r_msg", "审核成功！请您返回查看");
		doIpsAddBorrow(param);
		return RESULT;
		
	}
	
	@Action("ipsAddBorrowNotify")
	public String ipsAddBorrowNotify() throws IOException{
		BorrowParam param = new BorrowParam();
		doIpsAddBorrow(param);
		return null;
	}
	
	/**
	 * 处理环迅发标业务
	 * @param param
	 * @throws IOException 
	 */
	public void doIpsAddBorrow(BorrowParam param) throws IOException{
		logger.info("环迅发标回调参数打印"+getRequestParams());
		IpsModel model =doRegisterReturn();
		String xml = model.checkSign();  //验签
		IpsAddBorrow ipsAddBorrow = new IpsAddBorrow();
		ipsAddBorrow.doIpsAddBorrowXml(xml);// 封装对象
		param.setTenderNo(ipsAddBorrow.getBidNo());
		param.setId(NumberUtils.getLong(ipsAddBorrow.getMemo1()));
		param.setResultCode(model.getErrCode());
		DisruptorUtils.doIpsAddBorrow(param);
	}
	
	@Action("ipsTenderReturn")
	public String ipsTenderReturn(){
		String pErrCode = paramString("pErrCode");
		BorrowParam param = new BorrowParam();
		String resultFlag = pErrCode+ "-" + System.currentTimeMillis() + "";
		param.setResultFlag(resultFlag);
		request.setAttribute("tenderFlag", resultFlag);
		request.setAttribute("ok_url", "/member/main.html"); // 成功返回地址
		request.setAttribute("r_msg", "恭喜您，投标成功，请您返回查看");
		ipsTender(param);
		return RESULT;
		
	}
	
	@Action("ipsTenderNotify")
	public String ipsTenderNotify(){
		BorrowParam param = new BorrowParam();
		ipsTender(param);
		return null;
	}
	
	/**
	 * 环迅投标业务处理
	 * @param param
	 * @throws Exception 
	 */
	private void ipsTender(BorrowParam param){
		logger.info("环迅投标回调参数打印"+getRequestParams());
		IpsModel model =doRegisterReturn();
		String xml = model.checkSign();
		IpsTenderNBorrow ipsTender =new IpsTenderNBorrow();
		ipsTender.doTenderXml(xml, ipsTender);
		request.setAttribute("back_url", "/invest/detail.html?borrowid="
				+ ipsTender.getMemo1());// 失败返回地址
		if (!"MG00000F".equals(model.getErrCode())) {
			throw new BussinessException("投标失败，失败原因："+model.getErrCode(),"/invest/detail.html?borrowid="+ ipsTender.getMemo1());
		}
		//处理投标业务,封装参数
		long borrowId = NumberUtils.getLong(ipsTender.getMemo1());  //获取请求borrowid
		long userId = NumberUtils.getLong(ipsTender.getMemo2());  //用户id
		String pwd = ipsTender.getMemo3();  //是否有交易密码
		Borrow borrow = borrowService.getBorrow(borrowId);  //查询标
		User user = userService.getUserById(userId);  //查询用户
		if (null ==borrow || null == user) {
			throw new BussinessException("投标失败,业务处理异常！请联系客服","/invest/detail.html?borrowid="+ ipsTender.getMemo1());
		}
		if (borrow.getType() ==Constant.TYPE_FLOW) {  //判断是不是流转标，是流转标，将金额转换成分数
			param.setTenderCount(NumberUtils.getInt(ipsTender.getTransferAmt())/borrow.getFlowMoney());
		}else{
			param.setMoney(NumberUtils.getDouble(ipsTender.getTransferAmt()));
		}
		param.setPaypwd(pwd);
		param.setId(borrowId);
		param.setTenderNo(ipsTender.getMerBillNo());
		DisruptorUtils.tender(param, user);
	}
	
	/**
	 * 专门用于转账接口，处理回调最新订单状态
	 * @return
	 */
	@Action("transterNotify")
	public String transterNotify(){
		logger.info("环迅转账回调参数打印"+getRequestParams());
		IpsModel ipsModel =doRegisterReturn();
		String returnXml = ipsModel.checkSign();
		Transfer transfer = new Transfer();
		transfer = transfer.response(returnXml, transfer);
		transfer.setErrCode(ipsModel.getErrCode());
		IpsPay ipsPay = new IpsPay();
		ipsPay.setOrdId(transfer.getMerBillNo());
		ipsPay.setReturnCode(transfer.getErrCode());
		ipsPay.setResultMsg(ipsModel.getErrMsg());
		borrowService.updatePayStatus(ipsPay);
		return null;
	}
	
	/**
	 * 还款页面回调处理
	 * @return
	 */
	@Action("ipsRepaymentReturn")
	public String ipsRepaymentReturn(){
		String pErrCode = paramString("pErrCode");
		BorrowParam param = new BorrowParam();
		String resultFlag = pErrCode+ "-" + System.currentTimeMillis() + "";
		param.setResultFlag(resultFlag);
		request.setAttribute("tenderFlag", resultFlag);
		request.setAttribute("ok_url", "/member/borrow/repaying.html"); // 成功返回地址
		//IPS受理中
		if (pErrCode=="MG00008F") {
			request.setAttribute("r_msg", "本次还款操作成功，环迅受理中，请您稍后查看");
		}else {
			request.setAttribute("r_msg", "本次还款操作成功，请您返回查看");	
		}
		ipsRepay(param);
		return RESULT;
		
	}
	
	private void ipsRepay(BorrowParam param){
		logger.info("环迅还款回调参数打印"+getRequestParams());
		IpsRepaymentNewTrade repayment = new IpsRepaymentNewTrade();
		IpsModel ipsModel = doRegisterReturn();
		String xml = ipsModel.checkSign();
		repayment.doReturnRepayment(repayment, xml);
		IpsRepaymentModel ipsrepayModel = new IpsRepaymentModel();
		ipsrepayModel.setOrdId(repayment.getMerBillNo());
		ipsrepayModel.setResultCode(ipsModel.getErrCode());
		ipsrepayModel.setResultMsg(ipsModel.getErrMsg());
		ipsrepayModel.setMoney(repayment.getOutAmt());
		
		DisruptorUtils.ipsRepay(ipsrepayModel, param);
	}
	
	
	@Action("testloan")
	public String testloan(){
		long id = paramLong("tid");
		BorrowTender tender = borrowTenderDao.find(id);
		try {
			
			//IpsHelper.doIpsTransfer(tender.getBorrow(), tender, "1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	private IpsModel doRegisterReturn(){
		IpsModel ipsMode  = new IpsModel();
		String pErrMsg = paramString("pErrMsg");
		String pErrCode = paramString("pErrCode");
		String desXmlPara = paramString("p3DesXmlPara");
		String sign = paramString("pSign");
		ipsMode.setErrMsg(pErrMsg);
		ipsMode.setErrCode(pErrCode);
		ipsMode.setDesXmlPara(desXmlPara);
		ipsMode.setSign(sign);
		return ipsMode;
	}
	
	@Action(value = "ipsNewCashReturn")		
	public String ipsNewCashReturn() throws IOException{		//页面回调处理
		AccountCashModel accountCash = new AccountCashModel();
		
		String pErrCode = paramString("pErrCode");
		String resultFlag = pErrCode+"-"+System.currentTimeMillis()+"";
		BorrowParam borrowParam = new BorrowParam();
		borrowParam.setResultFlag(resultFlag);
		
		request.setAttribute("tenderFlag", resultFlag);
		request.setAttribute("ok_url", "/member/main.html"); // 成功返回地址
		request.setAttribute("r_msg", "提现成功，请您返回查看");
		ipsDoCash(accountCash,borrowParam);
		return RESULT;
	}
	
	@Action(value="ipsNewCashNotify")
	public String ipsNewCashNotify() throws IOException{
		AccountCashModel accountCash = new AccountCashModel();
		BorrowParam borrowParam = new BorrowParam();
		ipsDoCash(accountCash,borrowParam);
		return null;
	}
	
	/**
	 * 环迅提现业务处理  
	 * @param accountCash
	 * @param ipsCash
	 * @return
	 * @throws IOException 
	 */
	public void ipsDoCash(AccountCashModel accountCash,BorrowParam borrowParam) throws IOException{
		logger.info("环迅提现回调参数打印"+getRequestParams());
//		IpsNewCash ipsCash =doNewCashReturn();

		IpsModel ipsMo = doRegisterReturn();
		
		if (!("MG00000F").equals(ipsMo.getErrCode())) {
			throw new BussinessException("提现处理失败！","/member/account/newcash.html");
		}
		String xml = ipsMo.checkSign();
		IpsNewCash ipsCash = new IpsNewCash();
		ipsCash = ipsCash.doCashCreate(xml);
		
		long userId = NumberUtils.getLong(ipsCash.getMemo1()); 
		User user = userService.getUserById(userId);
		if (null == user) {
			throw new BussinessException("提现处理失败！","/member/account/newcash.html");
		}
		
		try {
			String orderNo = ipsCash.getMerBillNo(); //订单号。
			logger.info( "订单号：" + ipsCash.getMerBillNo() + " 金额：" + ipsCash.getTrdAmt() 
					     + " 结果：" + ipsCash.getErrCode() + " 流水号：" + ipsCash.getIpsBillNo() );
			accountCash.setOrderId(ipsCash.getMerBillNo());
			accountCash.setOrderAmount(ipsCash.getTrdAmt());
			accountCash.setUserId(user.getUserId());
			accountCash.setResult(true);
			accountCash.setServFee(0);
			
			DisruptorUtils.doIpsCashBackTask(accountCash,borrowParam);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PrintWriter p=response.getWriter();  //返回通知
		p.print("success");

	}
	
	private IpsNewCash doNewCashReturn(){
		IpsNewCash ipsMode  = new IpsNewCash();
		String pMercode = paramString("pMerCode");
		String pErrMsg = paramString("pErrMsg");
		String pErrCode = paramString("pErrCode");
		String desXmlPara = paramString("p3DesXmlPara");
		String sign = paramString("pSign");
		ipsMode.setMerCode(pMercode);
		ipsMode.setErrMsg(pErrMsg);
		ipsMode.setErrCode(pErrCode);
		ipsMode.setDesXmlPara(desXmlPara);
		ipsMode.setSign(sign);
		return ipsMode;
	}
	//环迅提现回调  haungjun 2014/10/22 end
	
	//环迅充值回调   huangjun 2014/10/22 start
	@Action(value = "ipsRechgargeReturn")
	public String ipsRecahrgeReturn() throws IOException{
		String pErrCode = paramString("pErrCode");
		BorrowParam bparam = new BorrowParam();
		String resultFlag = pErrCode+ "-" + System.currentTimeMillis() + "";
		bparam.setResultFlag(resultFlag);
		request.setAttribute("tenderFlag", resultFlag);
		request.setAttribute("ok_url", "/member/main.html"); // 成功返回地址
		request.setAttribute("r_msg", "充值成功，请您返回查看");
		RechargeModel param = new RechargeModel();
		doRecharge(param,bparam);
		return RESULT;
	}
	@Action("ipsRechgargeNotify")
	public String ipsRechgargeNotify() throws IOException{	//异步回调
		RechargeModel param = new RechargeModel();
		doRecharge(param,null);
		
		return null;
		
	}
	
	/**
	 * 环迅充值业务处理
	 * @param param
	 * @throws IOException
	 */
	public void doRecharge(RechargeModel param,BorrowParam bparam) throws IOException{
		logger.info("环迅充值回调参数打印"+getRequestParams());
		IpsModel ipsMo = doRegisterReturn();
		String xml = ipsMo.checkSign();
		IpsRecharge ipsRe = new IpsRecharge();
		ipsRe = ipsRe.doRechargeCreate(xml);
		
		AccountLog log=new AccountLog(1L,Constant.RECHARGE,1L,getTimeStr(), getRequestIp());
		String orderNo = ipsRe.getMerBillNo(); //订单号。
		logger.info( "订单号：" + ipsRe.getMerBillNo() + " 金额：" + ipsRe.getTrdAmt() 
				     + " 结果：" + ipsRe.getErrCode() + " 流水号：" + ipsRe.getIpsBillNo() );
		log.setRemark("网上充值,"+Global.getValue("api_name")+"充值,订单号:"  +  orderNo);
		try {
			if(orderNo != null) {
				log.setRemark(getRequestParams());//返回的参数包含 进去
				RechargeModel reModel = new RechargeModel();  //对通用javabean进行参数封装
				reModel.setOrderAmount(ipsRe.getTrdAmt());
				reModel.setOrderId(ipsRe.getMerBillNo());
				reModel.setResultMsg(ipsMo.getErrMsg());
				reModel.setResult(ipsMo.getErrCode());
				reModel.setSerialNo(ipsRe.getIpsBillNo());
				reModel.setReturnParam(getRequestParams());
				DisruptorUtils.doRechargeBackTask(reModel, log,bparam);
			}else {
				logger.info( "**********"+Global.getValue("api_name")+"充值 回调返回订单为空:" + orderNo );
			}
		} catch (Exception e) {
			logger.error(e);
			logger.error("充值失败："+orderNo + "   " +  e );
		}
		PrintWriter p=response.getWriter();  //返回通知
		p.print("success");
	}
	
	public IpsRecharge doRechargeReturn(){
		IpsRecharge ipsRe = new IpsRecharge();
		String pErrMsg = paramString("pErrMsg");
		String pErrCode = paramString("pErrCode");
		String desXmlPara = paramString("p3DesXmlPara");
		String sign = paramString("pSign");
		ipsRe.setErrMsg(pErrMsg);
		ipsRe.setErrCode(pErrCode);
		ipsRe.setDesXmlPara(desXmlPara);
		ipsRe.setSign(sign);
		return ipsRe;
	}
	//环迅充值回调   huangjun 2014/10/22 end
}
