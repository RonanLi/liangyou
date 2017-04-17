package com.liangyou.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Enumeration;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import chinapnr.Base64;

import com.liangyou.api.chinapnr.AutoTenderPlan;
import com.liangyou.api.chinapnr.AutoTenderPlanClose;
import com.liangyou.api.chinapnr.CardCashOut;
import com.liangyou.api.chinapnr.CashOut;
import com.liangyou.api.chinapnr.ChinapnrModel;
import com.liangyou.api.chinapnr.ChinapnrRegister;
import com.liangyou.api.chinapnr.CorpRegister;
import com.liangyou.api.chinapnr.FssTrans;
import com.liangyou.api.chinapnr.InitiativeTender;
import com.liangyou.api.chinapnr.NetSave;
import com.liangyou.api.chinapnr.UsrAcctPay;
import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.disruptor.DisruptorUtils;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowAuto;
import com.liangyou.domain.User;
import com.liangyou.domain.UserCache;
import com.liangyou.domain.WebGlodLog;
import com.liangyou.exception.BussinessException;
import com.liangyou.model.BorrowParam;
import com.liangyou.model.APIModel.AccountCashModel;
import com.liangyou.model.APIModel.RechargeModel;
import com.liangyou.service.AccountService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.ChinapnrPayModelService;
import com.liangyou.service.UserService;
import com.liangyou.tool.coder.BASE64Decoder;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;

/**
 * 汇付回调处理类，使用disruptor 前台回调和后台回调都处理业务,处理业务的时候进行判断
 */
@Namespace("/public/chinapnr")
@ParentPackage("p2p-default")
public class PublicChinapnrAction extends BaseAction {
	private static final Logger logger = Logger
			.getLogger(PublicChinapnrAction.class);

	@Autowired
	private UserService userService;
	@Autowired
	private ChinapnrPayModelService chinapnrPayModelService;
	@Autowired
	private AccountService accountService;
	
	//1.8.0.4_u3   TGPROJECT-zhaiquanzhuanrang  qinjun 2014-06-26  start
	@Autowired
	private BorrowService borrowService;
	//1.8.0.4_u3   TGPROJECT-zhaiquanzhuanrang  qinjun 2014-06-26  end

	/**
	 * 汇付开户页面回调
	 */
	@Action(value = "userRegisterReturn")
	public String userRegisterReturn() {
		String usrCustid = paramString("UsrCustId");
		BorrowParam param = new BorrowParam();
		String resultFlag = usrCustid + "-" + System.currentTimeMillis() + "";
		param.setResultFlag(resultFlag);
		request.setAttribute("tenderFlag", resultFlag);
		request.setAttribute("ok_url", "/member/main.html"); // 成功返回地址
		request.setAttribute("back_url", "/member/main.html");// 失败返回地址
		request.setAttribute("r_msg", "恭喜您，开通汇付账号成功，请您返回查看");
		dealChinapnrRegister(param);
		return RESULT;
	}

	/**
	 * 汇付开户后台回调
	 */
	@Action(value = "userRegisterNotify")
	public String userRegisterNotify() {
		ChinapnrRegister reg = dealChinapnrRegister(null);
		printTrxId(reg.getTrxId());
		return null;
	}

	/**
	 * 开户业务处理
	 * 
	 * @param type
	 *            ：1=页面回调；0=后台回调
	 * @param param
	 * @return
	 */
	private ChinapnrRegister dealChinapnrRegister(BorrowParam param) {
		logger.info("进入用户开户回调" + getRequestParams());
		ChinapnrRegister reg = this.regchina();
		int ret = reg.callback(); // 进行验签操作
		logger.info("用户开户验签结果：………………" + ret);
		if (ret == 0) {
			if ("000".equals(reg.getRespCode())) {
				long userid = NumberUtils.getLong(reg.getMerPriv());// 获取开户时用户的userid
				User user = userService.getUserById(userid);
				user.setApiId(reg.getUsrId());
				user.setApiUsercustId(reg.getUsrCustId());
				//v1.8.0.3_u3 TGPROJECT-330 qj 2014-06-04 start 
				user.setPhone(reg.getUsrMp());
				//v1.8.0.3_u3 TGPROJECT-330 qj 2014-06-04 end
				DisruptorUtils.apiUserRegister(user, param);// 调用异步处理
			} else {
				logger.info("开户汇付处理失败…………" + reg.getRespDesc());
				throw new BussinessException(reg.getRespDesc(),
						"/member/index.html");
			}
		} else {
			logger.info("开户验签失败！");
		}
		return reg;
	}

	// 用户开户回调参数校验
	public ChinapnrRegister regchina() {
		ChinapnrRegister regist = new ChinapnrRegister();
		try {
			regist.setCertId(URLDecoder.decode(paramString("CmdId"),"utf-8"));
			regist.setRespCode(URLDecoder.decode(paramString("RespCode"),"utf-8"));
			regist.setRespDesc(URLDecoder.decode(paramString("RespDesc"),"utf-8"));
			regist.setMerCustId(URLDecoder.decode(paramString("MerCustId"),"utf-8"));
			regist.setUsrId(URLDecoder.decode(paramString("UsrId"),"utf-8"));
			regist.setUsrCustId(URLDecoder.decode(paramString("UsrCustId"),"utf-8"));
			regist.setBgRetUrl(URLDecoder.decode(paramString("BgRetUrl"),"utf-8"));
			regist.setTrxId(URLDecoder.decode(paramString("TrxId"),"utf-8"));
			regist.setRetUrl(URLDecoder.decode(paramString("RetUrl"),"utf-8"));
			//v1.8.0.3_u3 TGPROJECT-330 qj 2014-06-04 start 
			regist.setUsrMp(URLDecoder.decode(paramString("UsrMp"),"utf-8"));
			//v1.8.0.3_u3 TGPROJECT-330 qj 2014-06-04 end 
			regist.setMerPriv(URLDecoder.decode(paramString("MerPriv"), "utf-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
			e.printStackTrace();
		}
		regist.setChkValue(paramString("ChkValue"));
		return regist;
	}

	/**
	 * ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓用户充值业务处理↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	 * ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	 */
	/**
	 * 充值页面回调
	 * 
	 * @return
	 */
	@Action(value = "netSaveReturn")
	public String netSaveReturn() throws Exception {
		BorrowParam param = new BorrowParam();
		String trxId = paramString("TrxId");
		String resultFlag = trxId + "-" + System.currentTimeMillis() + "";
		param.setResultFlag(resultFlag);
		request.setAttribute("tenderFlag", resultFlag);
		request.setAttribute("ok_url", "/member/account/recharge.html"); // 成功返回地址
		request.setAttribute("back_url", "/member/account/newrecharge.html");// 失败返回地址
		request.setAttribute("r_msg", "恭喜您，用户充值成功，请您返回查看");
		dealChinapnrNetSave(param);
		return RESULT;
	}

	/**
	 * 充值后台回调
	 * 
	 * @return
	 */
	@Action(value = "netSaveNotify")
	public String netSaveNotify() throws Exception {
		NetSave save = dealChinapnrNetSave(null);
		printTrxId(save.getTrxId());
		return null;
	}

	/**
	 * 充值业务处理
	 * 
	 * @param type
	 *            ：1=页面回调；0=后台回调
	 * @param param
	 * @return
	 */
	private NetSave dealChinapnrNetSave(BorrowParam param) throws Exception {
		logger.info("用户充值回调进入…………" + getRequestParams());
		NetSave netSave = this.chpnrNatSaveCallback();
		int ret = netSave.callback(); // 验签操作
		logger.info("用户 充值验签结果：" + ret);
		if (ret == 0) { // 校验参数
			logger.info("用户充值，汇付处理成功，进入本地处理……注：成功失败都在后台处理");
			logger.info("订单号：" + netSave.getOrdId() + " 金额："
					+ netSave.getTransAmt() + " 结果：" + netSave.getRespDesc()
					+ " 流水号：" + netSave.getTrxId());
			AccountLog log = new AccountLog(Constant.ADMIN_ID,
					Constant.RECHARGE,
					NumberUtils.getLong(netSave.getMerPriv()), getTimeStr(),
					getRequestIp());

			logger.info("user_id:" + Constant.ADMIN_ID + "touser:"
					+ netSave.getMerPriv());
			log.setRemark(getRequestParams());
			RechargeModel rem = new RechargeModel();
			rem.setOrderAmount(netSave.getTransAmt());
			rem.setFeeAmt(Double.parseDouble(netSave.getFeeAmt()));
			rem.setSerialNo(netSave.getTrxId());
			rem.setResult("000".equals(netSave.getRespCode()) ? "true"
					: "false");
			rem.setResultMsg(netSave.getRespDesc());
			rem.setOrderId(netSave.getOrdId());
			logger.info("充值回调信息：金额" + netSave.getTransAmt() + "费率："
					+ netSave.getFeeAmt() + "扣款账户：" + netSave.getFeeCustId()
					+ "扣款子账户：" + netSave.getFeeAcctId());
			DisruptorUtils.doRechargeBackTask(rem, log, param);
		} else {
			logger.info("充值验签失败！");
		}
		return netSave;
	}

	private NetSave chpnrNatSaveCallback() {
		NetSave n = new NetSave();
		n.setTransAmt(paramString("TransAmt"));
		n.setRespCode(paramString("RespCode"));
		try {
			n.setMerPriv(URLDecoder.decode(paramString("MerPriv"), "utf-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
			e.printStackTrace();
		} // 此参数用户获取到用户的id，便于回调时查找到具体的用户
		n.setTrxId(paramString("TrxId")); // 钱管家交易唯一标示
		n.setChkValue(paramString("ChkValue")); // 签名信息
		n.setOrdId(paramString("OrdId")); // 订单号
		n.setMerCustId(paramString("MerCustId"));
		n.setOrdDate(paramString("OrdDate"));
		n.setRetUrl(paramString("RetUrl"));
		n.setBgRetUrl(paramString("BgRetUrl"));
		n.setCmdId(paramString("CmdId"));
		n.setUsrCustId(paramString("UsrCustId"));
		n.setGateBusiId(paramString("GateBusiId"));
		n.setGateBankId(paramString("GateBankId"));
		n.setFeeAmt(paramString("FeeAmt"));
		n.setFeeAcctId(paramString("FeeAcctId"));
		n.setFeeCustId(paramString("FeeCustId"));
		return n;
	}

	/**
	 * 自动投标页面回调
	 * 
	 * @return
	 */
	@Action(value = "autoTenderPlanReturn")
	public String autoTenderPlanReturn() throws Exception {
		logger.info("进入自动投标任务回调处理…………返回参数：" + getRequestParams());
		AutoTenderPlan atp = this.autoTenderPlanCall();
		int ret = atp.callback();
		if (ret == 0) {
			if ("000".equals(atp.getRespCode())) {
				String merPiv = atp.getMerPriv(); // 获取投标传递参数，以","隔开
				int checkMer = merPiv.indexOf(",");
				if (checkMer != -1) {
					String[] args = merPiv.split(",");
					long userid = NumberUtils.getLong(args[0]); // 投标用户id
					int autoTenderId = NumberUtils.getInt(args[1]); // 获取所投标的id
					BorrowAuto ba = userService.getBorrowAutoById(autoTenderId);
					if (ba.getUser().getUserId() == userid) {
						ba.setStatus(1);
						userService.updateBorrowAuto(ba);
						message("设置成功", "/member/security/myAutoInvest.html");
						return MSG;
					} else {
						throw new BussinessException("自动投标设置异常");
					}
				} else {
					logger.info("自动投标设置异常：参数异常");
					throw new BussinessException("自动投标设置异常，请联系管理员…………");
				}
			} else {
				logger.info("自动投标设置汇付处理失败，原因：" + atp.getRespDesc());
				throw new BussinessException(atp.getRespDesc());
			}
		} else {
			logger.info("投标验签失败");
		}
		return "";
	}

	/**
	 * 关闭投标计划回调
	 * 
	 * @return
	 */
	@Action(value = "autoTenderCloseReturn")
	public String autoTenderCloseReturn() {
		logger.info("进入自动投标关闭计划回调处理…………返回参数：" + getRequestParams());
		AutoTenderPlanClose tpClose = this.autoTenderPlanCloseCall();
		int ret = tpClose.callback();
		if (ret == 0) {
			if ("000".equals(tpClose.getRespCode())) {
				String merPiv = tpClose.getMerPriv(); // 获取投标传递参数，以","隔开
				int checkMer = merPiv.indexOf(",");
				if (checkMer != -1) {
					String[] args = merPiv.split(",");
					long userid = NumberUtils.getLong(args[0]); // 投标用户id
					int autoTenderId = NumberUtils.getInt(args[1]); // 获取所投标的id
					BorrowAuto ba = userService.getBorrowAutoById(autoTenderId);
					if (ba.getUser().getUserId() == userid) {
						userService.deleteBorrowAutoById(autoTenderId);
						message("投标删除成功", "/member/security/myAutoInvest.html");
						return MSG;
					} else {
						throw new BussinessException("自动投标设置异常");
					}
				} else {
					logger.info("自动投标设置异常：参数异常");
					throw new BussinessException("自动投标设置异常，请联系管理员…………");
				}
			} else {
				logger.info("自动投标设置汇付处理失败，原因：" + tpClose.getRespDesc());
				throw new BussinessException(tpClose.getRespDesc());
			}
		} else {
			logger.info("投标验签失败");
		}
		return "";
	}

	/**
	 * ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓用户投标业务处理↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	 * ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	 */

	/**
	 * 用户投标页面回调
	 * 
	 * @return
	 */
	@Action(value = "autoTenderReturn")
	public String autoTenderReturn() throws Exception {
		BorrowParam param = new BorrowParam();
		String ordid = paramString("OrdId");
		String resultFlag = ordid + "-" + System.currentTimeMillis() + "";
		param.setResultFlag(resultFlag);
		String[] arg = paramString("MerPriv").split(",");// 获取请求接口时参数中保存的userid和borrowid
		request.setAttribute("tenderFlag", resultFlag);
		request.setAttribute("ok_url", "/member/invest/tendering.html"); // 成功返回地址
		request.setAttribute("back_url", "/invest/detail.html?borrowid="
				+ arg[1]);// 失败返回地址
		request.setAttribute("r_msg", "恭喜您，用户投标成功，请您返回查看");
		dealChinapnrAutoTender(param);
		return RESULT;
	}

	/**
	 * 用户投标后台回调
	 * 
	 * @return
	 */
	@Action(value = "autoTenderNotify")
	public String autoTenderNotify() throws Exception {
		BorrowParam param = new BorrowParam();
		InitiativeTender auto = dealChinapnrAutoTender(param);
		printTrxId(auto.getOrdId());
		return null;
	}

	/**
	 * 用户投标业务处理方法
	 * 
	 * @param type
	 * @param param
	 * @return
	 */
	private InitiativeTender dealChinapnrAutoTender(BorrowParam param)
			throws Exception {
		logger.info("进入投标回调处理…………返回参数：" + getRequestParams());
		InitiativeTender autoTender = this.tendercall();
		int ret = autoTender.callback();
		if (ret == 0) {
			if ("000".equals(autoTender.getRespCode())) {
				String merPiv = autoTender.getMerPriv(); // 获取投标传递参数，以","隔开，一般是用户id，borrowid
				int checkMer = merPiv.indexOf(",");
				if (checkMer != -1) {
					String[] args = merPiv.split(",");
					long userid = NumberUtils.getLong(args[0]); // 投标用户id
					long borrowid = NumberUtils.getLong(args[1]); // 获取所投标的id
					String pwd = "";
					String tenderCount = "";
					if (args.length == 4) {
						BASE64Decoder dec = new BASE64Decoder();
						pwd = new String(dec.decodeStr(args[2]));
						tenderCount = args[3];
					} else if (args.length == 3) {
						pwd = args[2];
					}
					
					//1.8.0.4_u3   TGPROJECT-zhaiquanzhuanrang  qinjun 2014-06-26  start
					Borrow borrow = borrowService.getBorrow(borrowid);
					double money =0;
					if (borrow.getIsAssignment() ==1) {   //判断是否是债权转让标
						money = NumberUtils.getDouble(autoTender.getTransAmt())/2;
					}else{
						money = NumberUtils.getDouble(autoTender.getTransAmt());
					}
				
					//1.8.0.4_u3   TGPROJECT-zhaiquanzhuanrang  qinjun 2014-06-26  start
					User user = userService.getUserById(userid);
					
					param.setId(borrowid);
					param.setMoney(money);
					param.setPwd(pwd);
					param.setTenderCount(NumberUtils.getInt(tenderCount));
					param.setTenderNo(autoTender.getOrdId());
					param.setTenderDate(autoTender.getOrdDate());

					DisruptorUtils.tender(param, user);
				} else {
					logger.info("投标传输回调参数异常");
					throw new BussinessException("投标异常，请联系管理员…………");
				}
			} else {
				logger.info("投标汇付处理失败，原因：" + autoTender.getRespDesc());
				throw new BussinessException(autoTender.getRespDesc());
			}
		} else {
			logger.info("投标验签失败");
		}
		return autoTender;
	}

	// 用户投标回调参数
	public InitiativeTender tendercall() {
		InitiativeTender auto = new InitiativeTender();
		auto.setCmdId(paramString("CmdId"));
		auto.setRespCode(paramString("RespCode"));
		auto.setRespDesc(paramString("RespDesc"));
		auto.setMerCustId(paramString("MerCustId"));
		auto.setOrdId(paramString("OrdId"));
		auto.setOrdDate(paramString("OrdDate"));
		auto.setTransAmt(paramString("TransAmt"));
		auto.setUsrCustId(paramString("UsrCustId"));
		auto.setTrxId(paramString("TrxId"));
		auto.setRetUrl(paramString("RetUrl"));
		auto.setBgRetUrl(paramString("BgRetUrl"));
		auto.setChkValue(paramString("ChkValue"));
		try {
			auto.setMerPriv(URLDecoder.decode(paramString("MerPriv"), "utf-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
			e.printStackTrace();
		}
		return auto;
	}

	public AutoTenderPlan autoTenderPlanCall() {
		AutoTenderPlan auto = new AutoTenderPlan();
		auto.setCmdId(paramString("CmdId"));
		auto.setRespCode(paramString("RespCode"));
		auto.setRespDesc(paramString("MerCustId"));
		auto.setUsrCustId(paramString("UsrCustId"));
		auto.setTenderPlanType(paramString("TenderPlanType"));
		auto.setTransAmt(paramString("TransAmt"));
		auto.setRetUrl(paramString("RetUrl"));
		try {
			auto.setMerPriv(URLDecoder.decode(paramString("MerPriv"), "utf-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
			e.printStackTrace();
		}
		auto.setChkValue(paramString("ChkValue"));
		return auto;
	}

	public AutoTenderPlanClose autoTenderPlanCloseCall() {
		AutoTenderPlanClose auto = new AutoTenderPlanClose();
		auto.setCmdId(paramString("CmdId"));
		auto.setRespCode(paramString("RespCode"));
		auto.setRespDesc(paramString("MerCustId"));
		auto.setUsrCustId(paramString("UsrCustId"));
		auto.setRetUrl(paramString("RetUrl"));
		try {
			auto.setMerPriv(URLDecoder.decode(paramString("MerPriv"), "utf-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
			e.printStackTrace();
		}
		auto.setChkValue(paramString("ChkValue"));
		return auto;
	}

	/**
	 * ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓用户取现回调处理↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	 * ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	 */
	/**
	 * 取现页面回调
	 * 
	 * @return
	 */
	@Action(value = "cashReturn")
	public String cashReturn() throws Exception {
		BorrowParam param = new BorrowParam();
		String ordid = paramString("OrdId");
		String resultFlag = ordid + "-" +"cash" + "";
		param.setResultFlag(resultFlag);
		param.setIp(this.getRequestIp());
		request.setAttribute("tenderFlag", resultFlag);
		request.setAttribute("ok_url", "/member/account/cash.html"); // 成功返回地址
		request.setAttribute("back_url", "/member/account/newcash.html");// 失败返回地址
		String cashIsVerify = Global.getValue("cash_is_verify"); // 获取取现是否需要校验
		if (cashIsVerify.equals("1")) {
			request.setAttribute("r_msg", "恭喜您，用户取现成功，请您返回查看");
		} else {
			request.setAttribute("r_msg", "恭喜您已申请提现成功，请等待审核！");
		}
		dealChinapnrCash(param);
		return RESULT;
	}

	/**
	 * 取现后台回调
	 * 
	 * @return
	 */
	@Action(value = "cashNotify")
	public String cashNotify() throws Exception {
		BorrowParam param = new BorrowParam();
		param.setIp(this.getRequestIp());
		CashOut cash = dealChinapnrCash(param);
		printTrxId(cash.getOrdId());
		return null;
	}

	/**
	 * 取现业务逻辑处理
	 * 
	 * @param type
	 * @param parm
	 * @return
	 */
	private CashOut dealChinapnrCash(BorrowParam param) throws Exception {
		logger.info("进入取现…………" + getRequestParams());
		CashOut cash = this.cashcall();
		int ret = cash.callback();// 验签操作
		if (ret == 0) {
			if ("000".equals(cash.getRespCode())||"999".equals(cash.getRespCode())) {
				logger.info("取现汇付处理成功，进入本地处理…………" + cash.getOrdId() + "-------");
				AccountCashModel cashModel = new AccountCashModel();// 封装取现共用bean
				cashModel.setCardNo(cash.getOpenAcctId());
				cashModel.setOrderId(cash.getOrdId());
				cashModel.setUserId(NumberUtils.getLong(cash.getMerPriv()));
				cashModel.setFeeAmt(NumberUtils.getDouble(cash.getFeeAmt()));
				cashModel.setServFee(NumberUtils.getDouble(cash.getServFee()));
				cashModel.setReturnCode(cash.getRespCode());
				DisruptorUtils.doVerifyCashBackTask(param, cashModel);
			} else {
				logger.info("取现失败，失败原因："
						+ URLDecoder.decode(cash.getRespDesc(), "utf-8"));
				throw new BussinessException(URLDecoder.decode(
						cash.getRespDesc(), "utf-8"),
						"/member/account/newcash.html");
			}
		} else {
			logger.info("取现验签失败   " + "orderId:" + cash.getOrdAmt());
		}
		return cash;
	}

	// 用户取现操作回调参数拼接
	public CashOut cashcall() {
		CashOut cash = new CashOut();
		cash.setCmdId(paramString("CmdId"));
		cash.setRespCode(paramString("RespCode"));
		cash.setMerCustId(paramString("MerCustId"));
		cash.setOrdId(paramString("OrdId"));
		cash.setUsrCustId(paramString("UsrCustId"));
		cash.setTransAmt(paramString("TransAmt"));
		cash.setOpenAcctId(paramString("OpenAcctId"));
		cash.setOpenBankId(paramString("OpenBankId"));
		cash.setFeeAmt(paramString("FeeAmt"));
		cash.setFeeCustId(paramString("FeeCustId"));
		cash.setFeeAcctId(paramString("FeeAcctId"));
		cash.setServFee(paramString("ServFee"));
		cash.setServFeeAcctId(paramString("ServFeeAcctId"));
		cash.setRetUrl(paramString("RetUrl"));
		cash.setBgRetUrl(paramString("BgRetUrl"));
		try {
			cash.setMerPriv(URLDecoder.decode(paramString("MerPriv"), "utf-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
			e.printStackTrace();
		}
		cash.setChkValue(paramString("ChkValue"));
		cash.setRespDesc(paramString("RespDesc"));
		cash.setReqExt(paramString("RespExt"));
		return cash;
	}

	/** ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ */

	/**
	 * 用户支付回调处理 如果涉及不同类别的操作用户给平台划款， 以后可以传类别参数来判断，在资金记录表中插入相关的 数据 （前台回调）
	 * 
	 * @return
	 */
	@Action(value = "payCall")
	public String payCall() throws Exception {
		logger.info("进入用户给平台支付回调处理…………");
		ChinapnrModel cModel = this.usrCallBack();
		String resultFlag = cModel.getOrdId() + "-"
				+ System.currentTimeMillis() + "";
		BorrowParam param = new BorrowParam();
		param.setResultFlag(resultFlag);
		request.setAttribute("tenderFlag", resultFlag);
		request.setAttribute("ok_url", "/member/index.html"); // 成功返回地址
		request.setAttribute("back_url", "/member/index.html");// 失败返回地址
		request.setAttribute("r_msg", "恭喜您，用户付款成功，请您返回查看");
		usrAcctPayCall(cModel, param);
		return RESULT;
	}

	/**
	 * 用户支付回调处理 如果涉及不同类别的操作用户给平台划款， 以后可以传类别参数来判断，在资金记录表中插入相关的 数据 (后台回调)
	 * 
	 * @return
	 */
	@Action(value = "usrAcctPayNotify")
	public String usrAcctPayCallBack() throws Exception {
		logger.info("进入用户给平台支付回调处理…………");
		ChinapnrModel cModel = this.usrCallBack();
		logger.info("回调订单号" + cModel.getOrdId());
		usrAcctPayCall(cModel, null);
		printTrxId(cModel.getOrdId());
		return null;
	}

	private void usrAcctPayCall(ChinapnrModel cModel, BorrowParam param) {
		logger.info("进入用户付款回调处理…………返回参数：" + getRequestParams());
		int result = cModel.callback();
		logger.info("回调参数处理" + result);
		int index = cModel.getMerPriv().indexOf(",");
		long userId = 0;
		int kefu_userId = 0;
		String type = "";
		if (result == 0) {
			if (cModel.getRespCode().equals("000")) {
				if (index != -1) {
					String[] str = cModel.getMerPriv().split(",");
					userId = NumberUtils.getLong(str[0]);
					kefu_userId = NumberUtils.getInt(str[1]);
					type = str[2];
				}
				User user = userService.getUserById(userId);
				// vip推荐客服userId
				UserCache uc = user.getUserCache();
				logger.info("用户支付，vip时，获取usercache对象" + uc.getId());
				uc.setKefuAddtime(new Date());
				uc.setVipVerifyTime(new Date());
				uc.setUser(user);
				if (kefu_userId != 0) {
					uc.setKefuUserid(kefu_userId);
					User kefu_user = userService.getUserById(kefu_userId);
					uc.setKefuUsername(kefu_user.getUsername());
				}
				logger.info("付款回调结果：" + userId + ":" + type);
				if ("vip".equals(type)) {
					AccountLog accountLog = new AccountLog(userId,
							Constant.VIP_FEE, Constant.ADMIN_ID,
							this.getTimeStr(), this.getRequestIp());
					DisruptorUtils.payVipCallBack(uc, accountLog, param);
					// 审核成功后，发送邮件
				} else if (type.equals("payAdmin")) {
					// message("支付成功！", "/member/payAdmin.html");
				} else {
					logger.info("支付失败！支付类型 type:" + type);
				}
			}
		} else {
			logger.info("用户支付回调异常");
			logger.info("回调验签不通过，数据异常");
		}
	}

	// 用户支付回调参数支付
	public ChinapnrModel usrCallBack() {
		UsrAcctPay uc = new UsrAcctPay();
		uc.setCmdId(paramString("CmdId"));
		uc.setRespCode(paramString("RespCode"));
		uc.setRespDesc(paramString("RespDesc"));
		uc.setOrdId(paramString("OrdId"));
		uc.setUsrCustId(paramString("UsrCustId"));
		uc.setMerCustId(paramString("MerCustId"));
		uc.setTransAmt(paramString("TransAmt"));
		uc.setInAcctId(paramString("InAcctId"));
		uc.setInAcctType(paramString("InAcctType"));
		uc.setRetUrl(paramString("RetUrl"));
		uc.setBgRetUrl(paramString("BgRetUrl"));
		try {
			uc.setMerPriv(URLDecoder.decode(paramString("MerPriv"), "utf-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
			e.printStackTrace();
		}
		uc.setChkValue(paramString("ChkValue"));
		return uc;
	}

	/** ---------------用户绑卡-------------- */
	/**
	 * 用户绑卡回调
	 * 
	 * @return
	 */
	@Action(value = "cardCashOutNotify")
	public String cardCashOutNotify() {
		CardCashOut card = dealChinapnrCardCashOut();
		printTrxId(card.getTrxId());
		return null;
	}

	// 用户绑卡回调参数拼接
	public CardCashOut cardcall() {
		CardCashOut ca = new CardCashOut();
		ca.setCmdId(paramString("CmdId"));
		ca.setRespCode(paramString("RespCode"));
		ca.setMerCustId(paramString("MerCustId"));
		ca.setOpenAcctId(paramString("OpenAcctId"));
		ca.setOpenBankId(paramString("OpenBankId"));
		ca.setUsrCustId(paramString("UsrCustId"));
		ca.setTrxId(paramString("TrxId"));
		ca.setBgRetUrl(paramString("BgRetUrl"));
		try {
			ca.setMerPriv(URLDecoder.decode(paramString("MerPriv"), "utf-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
			e.printStackTrace();
		}
		ca.setChkValue(paramString("ChkValue"));
		return ca;
	}

	/**
	 * 绑卡业务逻辑处理
	 * 
	 * @return
	 */
	private CardCashOut dealChinapnrCardCashOut() {
		logger.info("进入绑卡回调处理…………返回参数： " + getRequestParams());
		CardCashOut cardcash = this.cardcall();
		int ret = cardcash.callback();
		logger.info("取现验签结果：" + ret);
		if (ret == 0) {
			if ("000".equals(cardcash.getRespCode())) {
				logger.info("绑卡汇付处理成功，进入本地处理…………汇付id： "
						+ cardcash.getUsrCustId());
				DisruptorUtils.cardCashOutNotify(cardcash);
				logger.info("绑卡添加入disruptor成功");
			} else {
				logger.info("绑卡失败，原因：" + cardcash.getRespDesc());
				throw new BussinessException(cardcash.getRespDesc());
			}
		} else {
			logger.info("验签失败！");
		}
		return cardcash;
	}

	/** ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓一下为共用方法↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ */
	/**
	 * 汇付回调打印掉报文
	 * 
	 * @param trxId
	 * @throws Exception
	 */
	private void printTrxId(String order) {
		try {
			PrintWriter p = response.getWriter();
			p.print("RECV_ORD_ID_" + order);
			p.flush();
			p.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}



	/**
	 * 需要在页面上打印ordid的回调方法
	 * 
	 * @return
	 * @throws IOException
	 */
	@Action("chinapnrBgRet")
	public String chinapnrBgRet() throws IOException {
		logger.info("进入后台汇付处理打印ordid处理");
		String respCode = this.paramString("RespCode");
		String ordId = this.paramString("OrdId");
		String cmdid = this.paramString("CmdId");
		String merCustId = this.paramString("MerCustId");
		String usercustid = this.paramString("UsrCustId");
		logger.info("请求回调打印的ordid" + "merCustId:" + merCustId + "cmdid" + cmdid
				+ "respCode" + respCode + "ordId" + ordId + "" + usercustid);
		PrintWriter p = response.getWriter();
		if (respCode.equals("000") && !StringUtils.isBlank(ordId)) {
			logger.info(this.paramString("MerPriv"));
			logger.info("ordId" + ordId);
			p.print("RECV_ORD_ID_" + ordId);
		} else {
			p.print("ERROR");
		}
		p.flush();
		p.close();
		return null;
	}

	@Action("loanAndrepay")
	public String loanAndrepay() {
		logger.info("放款还款进入后台处理方法");
		String respCode = this.paramString("RespCode");
		String ordId = this.paramString("OrdId");
		String cmdid = this.paramString("CmdId");
		String respDesc = paramString("RespDesc");
		logger.info("接口：" + cmdid + "订单号：" + ordId + "处理结果:" + respCode
				+ "返回信息");
		chinapnrPayModelService.dealChinapnrBack(ordId, respCode, respDesc);
		printTrxId(ordId);
		return null;
	}

	@Action("webGlodReturn")
	public String webGlodReturn() {
		String respCode = paramString("RespCode");
		String respdesc = "";
		try {
			respdesc = URLDecoder.decode(paramString("RespDesc"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
			e.printStackTrace();
		}
		logger.info("金账户操作充值或者取现回调处理：-------");
		if (respCode.equals("000")) {
			double money = paramDouble("TransAmt");
			WebGlodLog wgLog = null;
			String merPriv = "";
			try {
				merPriv = URLDecoder.decode(paramString("MerPriv"), "utf-8");
			} catch (UnsupportedEncodingException e) {
				logger.error(e);
				e.printStackTrace();
			}// 附加信息
			String type = "";// 回调类型
			String ordid = paramString("OrdId");// 订单号
			long userid = 0;
			// 判断该订单是否处理过
			int sum = accountService.sumWebLogByOrdid(ordid);
			if (sum != 0) {
				throw new BussinessException("重复处理的订单" + ordid);
			}
			int checkMer = merPriv.indexOf(",");
			if (checkMer != -1) {
				String[] args = merPriv.split(",");
				userid = NumberUtils.getLong(args[0]); // 充值管理员
				type = args[1];
			}
			logger.info("金账户操作：订单号：" + ordid + "   ，附加信息：" + merPriv);
			if (type.equals("recharge")) {// 充值回调
				NetSave netSave = chpnrNatSaveCallback();
				int ret = netSave.callback(); // 验签操作
				logger.info("金账户充值验签结果：" + ret);
				if (ret == 0) { // 校验参数
					if (userid != 0 && !StringUtils.isBlank(type)) {
						wgLog = new WebGlodLog(new User(userid), new User(1),
								type, money, getRequestIp(), ordid);
						wgLog.setMemo(respdesc);
						wgLog.setFee(NumberUtils.getDouble(netSave.getFeeAmt()));
						// v1.8.0.3 TGPROJECT-64 qj 2014-04-16 start
						DisruptorUtils.webGlodLogBack(wgLog);
						// v1.8.0.3 TGPROJECT-64 qj 2014-04-16 start
						printTrxId(paramString("TrxId"));
					}
				} else {
					logger.info("充值验签失败   " + "orderId:" + netSave.getOrdAmt());
					throw new BussinessException("取现验签失败   " + "orderId:"
							+ netSave.getOrdAmt());
				}
			} else if (type.equals("cash")) {// 取现回调
				CashOut cash = this.cashcall();
				int ret = cash.callback(); // 验签操作
				logger.info("金账户取现验签结果：" + ret);
				if (ret == 0) {
					if (userid != 0 && !StringUtils.isBlank(type)) {
						wgLog = new WebGlodLog(new User(userid), new User(1),
								type, money, getRequestIp(), ordid);
						wgLog.setMemo(respdesc);
						wgLog.setFee(NumberUtils.getDouble(cash.getFeeAmt()));
						DisruptorUtils.glodCashSuccess(cash, wgLog, null);
						printTrxId(paramString("OrdId"));
					}
				} else {
					logger.info("取现验签失败   " + "orderId:" + cash.getOrdAmt());
					throw new BussinessException("取现验签失败   " + "orderId:"
							+ cash.getOrdAmt());
				}
			}
		} else {
			logger.info("金账户操作失败，失败原因：" + respdesc);
		}
		return null;

	}

	// v1.8.0.4_u3 TGPROJECT-314 qj 2014-05-30 start
	/**
	 * 操作生利宝页面回调
	 */
	@Action(value = "interestGenerateReturn")
	public String interestGenerateReturn() throws Exception {
		BorrowParam param = new BorrowParam();
		String ordid = paramString("OrdId");
		String resultFlag = ordid + "-" + System.currentTimeMillis() + "";
		param.setResultFlag(resultFlag);
		request.setAttribute("tenderFlag", resultFlag);
		request.setAttribute("ok_url", "/member/index.html"); // 成功返回地址
		request.setAttribute("back_url", "/member/index.html");// 失败返回地址
		request.setAttribute("r_msg", "恭喜您，操作成功");
		dealInterestGenerate(param);
		return RESULT;
	}
	// v1.8.0.4_u3 TGPROJECT-314 qj 2014-05-30 end

	// v1.8.0.4_u3 TGPROJECT-314 qj 2014-05-30 start
	/**
	 * 操作生利宝后台回调
	 */
	@Action(value = "interestGenerateNotify")
	public String interestGenerateNotify() throws Exception {
		BorrowParam param = new BorrowParam();
		FssTrans ft = dealInterestGenerate(param);
		printTrxId(ft.getOrdId());
		return null;
	}
	// v1.8.0.4_u3 TGPROJECT-314 qj 2014-05-30 end
	
	// v1.8.0.4_u3 TGPROJECT-314 qj 2014-05-30 start
	/**
	 * 操作生利宝后台回调
	 */
	private FssTrans dealInterestGenerate(BorrowParam param)throws Exception {
		logger.info("进入操作生利宝回调处理…………返回参数：" + getRequestParams());
		FssTrans ft = this.fssTranscall();
		int ret = ft.callback();
		if (ret == 0) {
			if ("000".equals(ft.getRespCode())) {
				DisruptorUtils.interestGenerateCall(ft,param);
			} else {
				logger.info("操作生利宝汇付处理失败，RespCode：" + ft.getRespCode());
				throw new BussinessException("操作失败！");
			}
		} else {
			logger.info("操作生利宝验签失败");
		}
		return ft;
	}
	// v1.8.0.4_u3 TGPROJECT-314 qj 2014-05-30 end
	// v1.8.0.4_u3 TGPROJECT-314 qj 2014-05-30 start
	public FssTrans fssTranscall() {
		FssTrans ft = new FssTrans();
		ft.setCmdId(paramString("CmdId"));
		ft.setRespCode(paramString("RespCode"));
		ft.setMerCustId(paramString("MerCustId"));
		ft.setUsrCustId(paramString("UsrCustId"));
		ft.setOrdId(paramString("OrdId"));
		ft.setOrdDate(paramString("OrdDate"));
		ft.setTransType(paramString("TransType"));
		ft.setTransAmt(paramString("TransAmt"));
		ft.setRetUrl(paramString("RetUrl"));
		ft.setRespExt(paramString("RespExt"));
		ft.setBgRetUrl(paramString("BgRetUrl"));
		ft.setChkValue(paramString("ChkValue"));
		try {
			ft.setMerPriv(URLDecoder.decode(paramString("MerPriv"), "utf-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
			e.printStackTrace();
		}
		return ft;
	}
	// v1.8.0.4_u3 TGPROJECT-314 qj 2014-05-30 end
	
	//v1.8.0.4_u3   TGPROJECT-340   qinjun  2014-06-23 start
	/**
	 * 汇付开户后台回调
	 */
	@Action(value = "corpRegisterNotify")
	public String userCorpRegisterNotify() {
		logger.info("进入用户开户回调" + getRequestParams());
		CorpRegister reg = this.corpRegchina();
		int ret = reg.callback(); // 进行验签操作
		logger.info("用户开户验签结果：………………" + ret);
		if (ret == 0) {
			if ("000".equals(reg.getRespCode())) {
				long userid = NumberUtils.getLong(reg.getMerPriv());// 获取开户时用户的userid
				User user = userService.getUserById(userid);
				user.setApiId(reg.getUsrId());
				user.setApiUsercustId(reg.getUsrCustId());
				DisruptorUtils.huifuUserCorpRegister(user, reg);
			} else {
				logger.info("开户汇付处理失败…………" + reg.getRespDesc());
				throw new BussinessException(reg.getRespDesc(),
						"/member/index.html");
			}
		} else {
			logger.info("开户验签失败！");
		}
		printTrxId(reg.getTrxId());
		return null;
	}
	
	// 用户开户回调参数校验
	public CorpRegister corpRegchina() {
		CorpRegister reg = new CorpRegister();
		reg.setCmdId(paramString("CmdId"));
		reg.setRespCode(paramString("RespCode"));
		reg.setRespDesc(paramString("RespDesc"));
		reg.setMerCustId(paramString("MerCustId"));
		reg.setUsrId(paramString("UsrId"));
		reg.setUsrCustId(paramString("UsrCustId"));
		reg.setAuditStat(paramString("AuditStat"));
		reg.setAuditDesc(paramString("AuditDesc"));
		reg.setOpenBankId(paramString("OpenBankId"));
		reg.setCardId(paramString("CardId"));
		reg.setBgRetUrl(paramString("BgRetUrl"));
		reg.setTrxId(paramString("TrxId"));
		try {
			//v1.8.0.4_u4  TGPROJECT-347  qinjun 2014-07-01  start 
			reg.setUsrName(URLDecoder.decode(StringUtils.isNull(paramString("UsrName")), "utf-8"));
			//v1.8.0.4_u4  TGPROJECT-347  qinjun 2014-07-01  end 
			reg.setMerPriv(URLDecoder
					.decode(paramString("MerPriv"), "utf-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
			e.printStackTrace();
		}
		reg.setChkValue(paramString("ChkValue"));
		return reg;
	}
	//v1.8.0.4_u3   TGPROJECT-340   qinjun  2014-06-23 start

}
