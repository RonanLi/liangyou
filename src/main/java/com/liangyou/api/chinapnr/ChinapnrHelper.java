package com.liangyou.api.chinapnr;

import java.util.Date;

import org.apache.log4j.Logger;

import chinapnr.Base64;

import com.liangyou.context.Global;
import com.liangyou.domain.ChinaPnrPayModel;
import com.liangyou.domain.User;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.OrderNoUtils;
import com.liangyou.util.StringUtils;
public class ChinapnrHelper {
	private static final Logger logger = Logger.getLogger(ChinapnrHelper.class);

	private static ChinapnrModel doSubmit(ChinapnrModel mod) {
		try {
			mod.submit();
		} catch (Exception e) {
			logger.error(e.getStackTrace());
		}
		return mod;
	}
	/**
	 * 汇付2.0中冻结接口
	 * */ 
	public static ChinapnrModel usrFreezeBg(String usrid, String tranAmt,
			String ordId, String date) {
		UsrFreezeBg mod = new UsrFreezeBg(usrid, tranAmt, ordId, date);
		return doSubmit(mod);
	}
	
    /**
     * 汇付2.0中解冻接口
     * */
	public static ChinapnrModel newUsrUnFreeze(String usrId, String date,
			String ordId, String trxId) {
		UsrUnFreeze usr = new UsrUnFreeze(usrId, date, ordId, trxId);
		return doSubmit(usr);
	}

	/**
	 * 用户满标放款接口 outCustId:出账账户,inUustId:入账账户,transAmt:交易金额,fee:费率,userid:用户id
	 * */
	public static ChinapnrModel loans(String outCustId, String inCnstId,
			String transAmt, String fee, String ordId,
			String ordDate, String subOrdId, String subOrdDate, String isdefault,String args) {

		Loans loan = new Loans(outCustId, inCnstId, transAmt, ordId, ordDate,
				subOrdId, subOrdDate, fee);
		/*if (NumberUtils.getDouble2(fee)>0) {
			loan.newDivDetails(args);
		}else{
			loan.setDivDetails("");
		}*/
		loan.setDivDetails("");
		loan.setIsDefault(isdefault);
		return doSubmit(loan);
	}
	/**
	 * 自动扣款(还款接口)
	 * outCustId:出账账户,inCnstId:进账账户,transAmt:交易金额,
	 * chinapnr_manageid:金账户在汇付平台上的一个账号，在1.0版本中无此参数
	 * MDT000023类似这中格式
	 * */
	public static ChinapnrModel repayment(String outCustId, String inCnstId,
			String transAmt, String fee,String ordId,
			String ordDate, String subOrdId, String subOrdDate,String args){
		String chinapnr_manageid="";
		Repayment repay=null;
		if (inCnstId.equals("1")){
			inCnstId=Global.getValue("chinapnr_merid");
			String inAcctId ="";
			if (StringUtils.isBlank(args)) {   //校验平台收取管理费账户,若为空，默认划账到平台专属账户，否则
				inAcctId = Global.getValue("merAcctId");
			}else{
				inAcctId=args;
			}
			logger.info("划账给平台获取系统专属账户-->"+inAcctId+"平台账户"+"-->"+inCnstId);
			repay=new Repayment(outCustId,inCnstId,transAmt,ordId,ordDate, subOrdId, subOrdDate,fee);
			repay.setInAcctId(inAcctId);
		}else{
			fee=NumberUtils.format2Str(NumberUtils.getDouble(fee));
			repay=new Repayment(outCustId,inCnstId,transAmt,ordId,ordDate, subOrdId, subOrdDate,fee);
			repay.setInAcctId("");
		}
		if (NumberUtils.getDouble2(fee)>0) {
			String[][] divArg = new String[][]{{}};
			divArg=new String[][]{{Global.getValue("merAcctId"),fee}};
				String s=ChinapnrModel.newDivDetails(divArg);
				repay.setDivDetails(s);
		}else{
			repay.setDivDetails("");
		}
		return doSubmit(repay);	
	}
	/**
	 * 汇付2.0查询订单状态
	 * */
	public static ChinapnrModel queryTransStat(String ordId,String ordDate,String querytransType){
		QueryStatus query=new QueryStatus(ordId, ordDate, querytransType);
		return doSubmit(query);
	}
	/**
	 * 汇付2.0用户余额查询
	 * */
	public static ChinapnrModel queryBalanceBg(String usrCustId){
		ChinapnrQueryBalance querybalance=new ChinapnrQueryBalance(usrCustId);
		return doSubmit(querybalance);
		
	}
	/**
	 * 汇付2.0自动投标业务接口//后台数据流方式
	 * args:借款人信息，格式借款人usrcustid，投标金额，利率
	 * */
	public static ChinapnrModel autoTender(User user,String[][] args,long borrowid,double transmoney){
//		InitiativeTender auto=new InitiativeTender(usrCustId, transAmt,maxTenderRate);
//		auto.fildBorrowerDetails(args);
		String ordId=OrderNoUtils.getInstance().getSerialNumber();
		String ordDate=DateUtils.newdateStr2(new Date());
		String transAmt=NumberUtils.format2Str(transmoney);
		
		AutoTender auto=new AutoTender(user.getApiUsercustId(), transAmt, "0.01");
		auto.setOrdId(ordId);
		auto.setOrdDate(ordDate);
		auto.fildBorrowerDetails(args);
		auto.setMerPriv(user.getUserId()+","+borrowid);
		return doSubmit(auto);
	}
	/**
	 * 汇付2.0中后台划账接口，仅限平台划账给用户
	 * tranAmt:划账金额，inCustId:划账给用户接收方的用户好
	 * ordid：订单号
	 * */
	public static ChinapnrModel transfer(String transAmt,String inCustId,String ordId,String inAcctId,String outAcctId){
		Transfer tran=new Transfer(transAmt, inCustId, ordId,outAcctId,inAcctId);
		return doSubmit(tran);
	}
	
	/**
	 * 债权转让接口
	 * @return
	 */
	public static ChinapnrModel creditAssign(ChinaPnrPayModel cpm){
		String sellCustId = cpm.getAddtime();//转让债权的用户(收款方)
		String creditAmt = NumberUtils.format2Str(cpm.getOrdamt());
		//ChinaPnrPayModel中fee里边封装(CreditDealAmt实际转让金额\Fee转让手续费\PrinAmt已还本金\BorrowerCustId原始借款人汇付账号),以逗号隔开
		String[] feeArray = cpm.getFee().split(",");
		String creditDealAmt = feeArray[0];
		String fee = feeArray[1];
        String printAmt = feeArray[2];
        String borrowerCustId = feeArray[3];
        
        StringBuffer bidDetailsBuffer = new StringBuffer();
        bidDetailsBuffer.append("{\"BidDetails\":[{");
        bidDetailsBuffer.append("\"BidOrdId\":\""+cpm.getSubordId() +"\"," );
        bidDetailsBuffer.append("\"BidOrdDate\":\""+cpm.getSuborddate()+"\"," );
        bidDetailsBuffer.append("\"BidCreditAmt\":\""+creditAmt+"\",");
        bidDetailsBuffer.append("\"BorrowerDetails\":[{" );
        bidDetailsBuffer.append("\"BorrowerCustId\":\""+borrowerCustId+"\"," );
        bidDetailsBuffer.append("\"BorrowerCreditAmt\":\""+creditAmt+"\"," );
        bidDetailsBuffer.append("\"PrinAmt\":\""+printAmt+"\"" );
        bidDetailsBuffer.append("}]" );
        bidDetailsBuffer.append("}]}" );
        
        String bidDetails = bidDetailsBuffer.toString();
        String divDetails = "";
        String buyCustId = cpm.getUsrCustId();//承接人(付款方)
        
		CreditAssign creditAssign =  new CreditAssign(sellCustId, creditAmt, creditDealAmt, bidDetails, fee, divDetails, buyCustId);
		
		return doSubmit(creditAssign);
		
	}
	
	/**
	 * 后台审核用户取现专用接口
	 * @param ordId:申请提现时的订单号
	 * @param usrCustId：申请提现时的用户号
	 * @param transAmt：提现金额
	 * @param userid：用户id
	 * @param flag: 审核状态
	 * @return
	 */
	public static ChinapnrModel cashAudit(String ordId,String usrCustId,String transAmt,long userid,String flag){
		CashAudit cash=new CashAudit(ordId, usrCustId, transAmt);
		cash.setAuditFlag(flag);
		cash.setMerPriv(userid+"");
		return doSubmit(cash);
	}
	
	/**
	 * 用于汇付1.0升级2.0时补录订单号使用接口
	 * @param usrCustid
	 * @param ordId
	 * @param ordDate
	 * @param transAmt
	 * @param maxTenderRet
	 * @param args
	 * @return
	 */
	public static ChinapnrModel impOrd(String usrCustid,String ordId,String ordDate,String transAmt,String maxTenderRet,String[][] args){
		ImpOrd impord=new ImpOrd(ordId, ordDate, usrCustid, transAmt);
		impord.setMaxTenderRate(maxTenderRet);
		impord.fildBorrowerDetails(args);
		return doSubmit(impord);
	}
	
	/**
	 * 商户子账户信息查询
	 * @return
	 */
	public static ChinapnrModel queryAcctschin(){
		QueryAccts qu=new QueryAccts();
		return doSubmit(qu);
	}
	
	/**
	 * 投标对账
	 * @param beginDate
	 * @param endDate
	 * @param pageNum
	 * @param pageSize
	 * @param queryTransType
	 * @return
	 */
	public static ChinapnrModel reconciliationchin(String beginDate,String endDate,String pageNum,String pageSize,String queryTransType){
		Reconciliation re=new Reconciliation(beginDate, endDate, pageNum, pageSize, queryTransType);
		return doSubmit(re);
	}
	
	/**
	 * 取现对账
	 * @param beginDate
	 * @param endDate
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public static ChinapnrModel cashReconciliationchin(String beginDate,String endDate,String pageNum,String pageSize){
		 CashReconciliation ca=new CashReconciliation(beginDate, endDate, pageNum, pageSize);
		 return doSubmit(ca);
			 
	}
	
	/**
	 * 充值对账
	 * @param beginDate
	 * @param endDate
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public static ChinapnrModel saveReconchin(String beginDate,String endDate,String pageNum,String pageSize){
		SaveReconciliation save=new SaveReconciliation(beginDate, endDate, pageNum, pageSize);
		return doSubmit(save);
	}
	
	/**
	 * 商户扣款对账
	 * @param beginDate
	 * @param endDate
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public static ChinapnrModel trfReconchina(String beginDate,String endDate,String pageNum,String pageSize){
		 TrfReconciliation trf=new TrfReconciliation(beginDate, endDate, pageNum, pageSize);
		 return doSubmit(trf);
	}
	
	/**
	 * 用户汇付开户
	 * @param user
	 * @return
	 */
	public static ChinapnrRegister userRegister(User user){
		ChinapnrRegister reg=new ChinapnrRegister();
		reg.setUsrId(String.valueOf(DateUtils.newdateStr2(new Date())+user.getUserId()));
		reg.setUsrMp(user.getPhone());
		reg.setIdType("00");
		reg.setIdNo(user.getCardId());
		reg.setUsrName(user.getRealname());
	    logger.info("汇付开户真实姓名："+user.getRealname());
		reg.setUsrEmail(user.getEmail());
		reg.setMerPriv(user.getUserId()+"");
		try {
			reg.sign();
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("生成签名失败，失败原因："+e);
		}
		return reg;
	}
	
	/**
	 * 用户绑卡
	 * @param user
	 * @return
	 */
	public static CardCashOut chinaCard(User user){
		CardCashOut card=new CardCashOut(user.getApiUsercustId());
		card.setMerPriv(user.getUserId()+"");
		try {
			card.sign();
		} catch (Exception e) {
			logger.info("");
		}
		return card;
	}
	
	/**
	 * 用户取现
	 * 汇付取现2.0接口
	 * 添加 网站收取服务费的功能
	 * @param user
	 * @param transAmt：交易金额
	 * @return
	 */
	//v1.8.0.4_u4  TGPROJECT-357 qinjun  2014-07-07  start
	public static CashOut chinacash(User user,String transAmt,double servFee){
	//v1.8.0.4_u4  TGPROJECT-357 qinjun  2014-07-07  end
		CashOut cash=new CashOut(user.getApiUsercustId());
		cash.setVersion("20");
		String ordId = OrderNoUtils.getInstance().getSerialNumber();
		cash.setOrdId(ordId);
		cash.setUsrCustId(user.getApiUsercustId());
		cash.setMerPriv(user.getUserId()+"");
		cash.setTransAmt(transAmt);
		//v1.8.0.4_u4  TGPROJECT-357 qinjun  2014-07-07  start
		cash.setServFee(servFee == 0 ? "": NumberUtils.format2Str(servFee)); //取现费用
		//v1.8.0.4_u4  TGPROJECT-357 qinjun  2014-07-07  end
		cash.setServFeeAcctId(Global.getValue("serv_fee_acctId")); //收费账户
		cash.setRetUrl(Global.getValue("weburl")+"/public/chinapnr/cashReturn.html");
		cash.setBgRetUrl(Global.getValue("weburl")+"/public/chinapnr/cashNotify.html");
		try {
			cash.sign();
		} catch (Exception e) {
			logger.info("生成签名失败！"+e);
		}
		return cash;
	}
	/**
	 * 用户向平台主动支付
	 * @param user
	 * @param transAmt
	 * @return
	 */
	public static UsrAcctPay chinaacctPay(User user,String transAmt){
		String ordId=OrderNoUtils.getInstance().getSerialNumber();
		 UsrAcctPay pnr=new UsrAcctPay(ordId, user.getApiUsercustId(), transAmt);
		   pnr.setMerPriv(user.getUserId()+"");
		   try {
			   pnr.sign();
		} catch (Exception e) {
			logger.info("生成签名失败，失败原因："+e);
		}
		   return pnr;
	}
	

	/**
	 * 用户投标计划
	 * @param user
	 * @param args 投标json格式的二维数组
	 * @param borrowid
	 * @param transmoney
	 * @return
	 */
	//v1.8.0.4  TGPROJECT-209   lx  2014-04-23 start
	//v1.8.0.4  TGPROJECT-227   qj  2014-04-28 start
	public static InitiativeTender chinaTender(User user,String[][] args,long borrowid,double transmoney,String pwd,int tenderCount){
		String ordId=OrderNoUtils.getInstance().getSerialNumber();
		String ordDate=DateUtils.newdateStr2(new Date());
		String transAmt=NumberUtils.format2Str(transmoney);
		InitiativeTender auto=new InitiativeTender(user.getApiUsercustId(), transAmt, "0.01");
		auto.setOrdId(ordId);
		auto.setOrdDate(ordDate);
		auto.fildBorrowerDetails(args);
		char[] str = Base64.encode(pwd.getBytes());
		auto.setMerPriv(user.getUserId()+","+borrowid+","+str+","+tenderCount);
		try {
			auto.sign();	
		} catch (Exception e) {
			logger.info("生成签名失败，失败原因："+e);
		}
		return auto;
	}
	//v1.8.0.4  TGPROJECT-227   qj  2014-04-28 end
	//v1.8.0.4  TGPROJECT-209   lx  2014-04-23 end
	/**
	 * 自动投标计划
	 * @param user
	 * @param args 投标json格式的二维数组
	 * @param borrowid
	 * @param transmoney
	 * @return
	 */
	public static AutoTenderPlan autoTenderPlan(User user,long autoTenderId){
		String ordId=OrderNoUtils.getInstance().getSerialNumber();
		String ordDate=DateUtils.newdateStr2(new Date());
	
		AutoTenderPlan autoTender = new AutoTenderPlan(user.getApiUsercustId());
		autoTender.setOrdId(ordId);
		autoTender.setOrdDate(ordDate);
		autoTender.setMerPriv(user.getUserId()+","+autoTenderId);
		try {
			autoTender.sign();	
		} catch (Exception e) {
			logger.info("生成签名失败，失败原因："+e);
		}
		return autoTender;
	}
	
	/**
	 * 自动投标计划
	 * @param user
	 * @param transmoney
	 * @return
	 */
	public static AutoTenderPlanClose autoTenderPlanClose(User user,long autoTenderId){
		AutoTenderPlanClose tpClose = new AutoTenderPlanClose(user.getApiUsercustId());
		tpClose.setMerPriv(user.getUserId()+","+autoTenderId);
		try {
			tpClose.sign();	
		} catch (Exception e) {
			logger.info("生成签名失败，失败原因："+e);
		}
		return tpClose;
	}
	
	/**
	 * 用户充值
	 * @param user
	 * @param money
	 * @return
	 */
	public static NetSave chinaSave(User user,String money){
		String dcFlag="C";
		String ordId=OrderNoUtils.getInstance().getSerialNumber();
		String ordDate=DateUtils.newdateStr2(new Date());
		NetSave save=new NetSave(ordId, ordDate, dcFlag, money, user.getApiUsercustId());
		save.setRetUrl(Global.getValue("weburl")+"/public/chinapnr/netSaveReturn.html");
		save.setMerPriv(user.getUserId()+"");
		try {
			save.sign();
		} catch (Exception e) {
			logger.error("生成签名失败！"+e);
		}
		return save;
	}
	/**
	 * 用户登陆汇付
	 * @param user
	 * @return
	 */
	public static ChinapnrLogin chinapnrUserLogin(User user){
		ChinapnrLogin usrlogin=new ChinapnrLogin(user.getApiUsercustId());
		return usrlogin;
	}
	
	//v1.8.0.4_u2  TGPROJECT-314   qj  2014-05-30 start
	public static FssTrans interestGeneratedWealth(User user){
		FssTrans ft = new FssTrans(user.getApiUsercustId());
		ft.setOrdId(OrderNoUtils.getInstance().getSerialNumber());
		ft.setOrdDate(DateUtils.newdateStr2(new Date()));
		try {
			ft.sign();
		} catch (Exception e) {
			logger.error("生成签名失败！"+e);
		}
		return ft;
	}
	//v1.8.0.4_u2  TGPROJECT-314   qj  2014-05-30 end
	
	//v1.8.0.4_u3   TGPROJECT-340   qinjun  2014-06-23 start
	/**
	 * 企业开户
	 * @param user
	 * @param guarType
	 * @return
	 */
	public static CorpRegister corpRegister(User user,String guarType){
		CorpRegister reg=new CorpRegister();
		reg.setUsrId(String.valueOf(DateUtils.newdateStr2(new Date())+user.getUserId()));
		reg.setMerPriv(user.getUserId()+"");
		reg.setBusiCode(user.getCardId());
		reg.setGuarType(guarType);
		try {
			reg.sign();
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("生成签名失败，失败原因："+e);
		}
		return reg;
	}
	
	/**
	 * 企业开户
	 * @param user
	 * @param guarType
	 * @return
	 */
	public static CorpRegisterQuery corpRegisterQuery(User user){
		CorpRegisterQuery reg=new CorpRegisterQuery();
		reg.setBusiCode(user.getCardId());
		return (CorpRegisterQuery)doSubmit(reg);
	}
	//v1.8.0.4_u3   TGPROJECT-340   qinjun  2014-06-23 end
	
	
	/**
	 * 用户提现银行卡解绑
	 * @param user
	 * @return
	 */
	public static ChinapnrModel chinaCardRemove(String usrCusId,String cardId){
		CardCashRemove cr=new CardCashRemove(usrCusId,cardId);
		return doSubmit(cr);
		
	}

	
}
