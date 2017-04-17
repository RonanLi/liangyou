package com.liangyou.api.ips;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.liangyou.context.Global;
import com.liangyou.domain.AccountCash;
import com.liangyou.domain.AccountRecharge;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowCollection;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.IpsPay;
import com.liangyou.domain.User;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.OrderNoUtils;
import com.liangyou.util.StringUtils;

public class IpsHelper {
	
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(IpsModel.class);
	
	/**
	 * 开户请求
	 * @param createIps
	 * @return
	 */
	public static IpsRegister doCreate(User user){
		IpsRegister createIps = new IpsRegister();
		createIps.setMerBillNo(OrderNoUtils.getInstance().getSerialNumber());
		createIps.setIdentType(1+"");
		createIps.setIdentNo(user.getCardId());
		createIps.setRealName(user.getRealname());
		createIps.setMobileNo(user.getPhone());
		createIps.setEmail(user.getEmail());
		createIps.setSmDate(DateUtils.newdateStr2(new Date()));
		String webUrl = Global.getValue("weburl");
		createIps.setWebUrl(webUrl+"/public/ips/ipsRegisterReturn.html");
		createIps.setS2SUrl(webUrl+"/public/ips/ipsRegisterNotify.html");
		createIps.setMemo1(user.getUserId()+"");
		createIps.createSign();
		return createIps;
	}
	

	/**
	 * 发标接口
	 * @param ipsBorrow
	 * @param borrow
	 * @return
	 */
	public static IpsAddBorrow doCreateIpsAddBorrow(Borrow borrow,User user){
		IpsAddBorrow ipsBorrow = new IpsAddBorrow();
		String ord = OrderNoUtils.getInstance().getSerialNumber();
		ipsBorrow.setMerBillNo(ord);
		ipsBorrow.setBidNo("B"+ord);
		ipsBorrow.setRegDate(DateUtils.newdateStr2(new Date()));
		ipsBorrow.setLendAmt(NumberUtils.format2Str(borrow.getAccount()));
		ipsBorrow.setGuaranteesAmt("0");  //担保费
		ipsBorrow.setTrdLendRate(NumberUtils.format2Str(borrow.getApr()));
		ipsBorrow.setTrdCycleType("3"); //借款周期类型
		ipsBorrow.setTrdCycleValue(borrow.getTimeLimit()+"");
		ipsBorrow.setLendPurpose("借款使用");
		ipsBorrow.setRepayMode(2+"");  //还款方式
		ipsBorrow.setOperationType(1+"");  //操作类型：1、新增，2、结束
		ipsBorrow.setLendFee("0");
		ipsBorrow.setAcctType(1+"");
		ipsBorrow.setIpsAcctNo(user.getApiId());
		ipsBorrow.setIdentNo(borrow.getUser().getCardId());
		ipsBorrow.setRealName(borrow.getUser().getRealname());
		ipsBorrow.setIpsAcctNo(borrow.getUser().getApiId());
		String webUrl = Global.getValue("weburl");
		ipsBorrow.setUrl(ipsBorrow.getSubmitUrl()+"/CreditWeb/registerSubject.aspx");
		ipsBorrow.setWebUrl(webUrl+"/public/ips/ipsAddBorrowReturn.html");
		ipsBorrow.setS2SUrl(webUrl+"/public/ips/ipsAddBorrowNotify.html");
		ipsBorrow.setMemo1(borrow.getId()+"");
		ipsBorrow.createSign();
		return ipsBorrow;
	}

	public static Map<String,String> queryRechargeBank() throws Exception {
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		IpsModel model = new IpsModel();
		model.setUrl(model.getSubmitUrl()+"/CreditWS/Service.asmx");
		
		//String[][] commitParams = new String[][]{{"argMerCode","argSign"},{model.getMerCode(),IpsCrypto.md5Sign(model.getMerCode()+ model.getCert_md5())}};
		//model.setCommitParams(commitParams);
		String xml = model.doNotifySubmit(model.getUrl(), "GetBankList");
		String errCode =  StringUtils.getXmlNodeValue(xml, "pErrCode");
		if (!"0000".equals(errCode)) {
			return null;
		}
		String bankListStr = StringUtils.getXmlNodeValue(xml, "pBankList");
		System.out.println(bankListStr);
		String[] bankArray = bankListStr.split("#");
		Map<String,String> map = new HashMap<String, String>();
		for (int i = 0; i < bankArray.length; i++) {
			String[] bankMsg = bankArray[i].split("\\|");
			map.put( bankMsg[0], bankMsg[2]);
		}
		return map;
	}
	

	
	/**
	 * 环迅充值方法充值
	 * @param recharge
	 * @return
	 */
	public static IpsRecharge ipsRecharge(AccountRecharge recharge){
		String webUrl = Global.getValue("weburl");
		IpsRecharge ipsRecharge = new IpsRecharge();
		ipsRecharge.setMerBillNo(OrderNoUtils.getInstance().getSerialNumber());
		ipsRecharge.setIdentNo(recharge.getUser().getCardId());
		ipsRecharge.setIpsAcctNo(recharge.getUser().getApiId());
		ipsRecharge.setRealName(recharge.getUser().getRealname());
		ipsRecharge.setTrdDate(DateUtils.newdateStr2(recharge.getAddtime()));
		ipsRecharge.setTrdAmt(NumberUtils.format2Str(recharge.getMoney()));
		ipsRecharge.setChannelType(recharge.getType());
		ipsRecharge.setTrdBnkCode("00004");
		ipsRecharge.setMerFee(NumberUtils.format2Str(recharge.getFee()));
		//是否平台支付手续费（1：平台支付2：用户支付）
		int rechargeWeb = Global.getInt("recharge_web");  //获取平台是否自己垫付充值手续费
		if (rechargeWeb ==1) {//平台垫付充值手续费,不收取任何手续费
			ipsRecharge.setIpsFeeType("1");
		}else{//从用户自己账户扣款
			ipsRecharge.setIpsFeeType("2");
		}
		ipsRecharge.setUrl(ipsRecharge.getSubmitUrl()+"/CreditWeb/doDpTrade.aspx");
		ipsRecharge.setWebUrl(webUrl+"/public/ips/ipsRechgargeReturn.html");
		ipsRecharge.setS2SUrl(webUrl+"/public/ips/ipsRechgargeNotify.html");
		ipsRecharge.createSign();
		recharge.setTradeNo(ipsRecharge.getMerBillNo());
		return ipsRecharge;
	}
	
	/**
	 * 环迅提现方法
	 */
	public static IpsNewCash ipsDoNewCash(AccountCash accountCash,String pMerFee){
		String webUrl = Global.getValue("weburl");
		IpsNewCash ipsNewCash = new IpsNewCash();
		ipsNewCash.setMerBillNo(OrderNoUtils.getInstance().getSerialNumber());
		ipsNewCash.setIdentNo(accountCash.getUser().getCardId());
		ipsNewCash.setRealName(accountCash.getUser().getRealname());
		ipsNewCash.setIpsAcctNo(accountCash.getUser().getApiId());
		ipsNewCash.setDwDate(DateUtils.newdateStr2(accountCash.getAddtime()));
		ipsNewCash.setTrdAmt(NumberUtils.format2Str(accountCash.getTotal()));
		ipsNewCash.setMerFee(pMerFee);
		ipsNewCash.setIpsFeeType(Global.getValue("ips_fee_type"));
		ipsNewCash.setUrl(ipsNewCash.getSubmitUrl()+"/CreditWeb/doDwTrade.aspx");
		ipsNewCash.setWebUrl(webUrl+"/public/ips/ipsNewCashReturn.html");
		ipsNewCash.setS2SUrl(webUrl+"/public/ips/ipsNewCashNotify.html");
		String user_id = accountCash.getUser().getUserId()+"";
		ipsNewCash.setMemo1(user_id);
		ipsNewCash.createSign();
		accountCash.setOrderNo(ipsNewCash.getMerBillNo());
		return ipsNewCash;
	}
	
	/**
	 * 投标调用接口
	 * @param tender
	 * @return
	 * @throws Exception 
	 */
	public static IpsTenderNBorrow doIpsTender(BorrowTender tender,User user) throws Exception{
		IpsTenderNBorrow ipsTender = new IpsTenderNBorrow();
		ipsTender.setMerBillNo(OrderNoUtils.getInstance().getSerialNumber());
		ipsTender.setMerDate(DateUtils.newdateStr2(new Date()));
		ipsTender.setUrl(ipsTender.getSubmitUrl()+"/CreditWeb/registerCreditor.aspx");
		ipsTender.setAcctType("1");
		ipsTender.setRealName(user.getRealname());
		ipsTender.setIdentNo(user.getCardId());
		ipsTender.setAccount(user.getApiId());
		ipsTender.setBidNo(tender.getBorrow().getIpsBorrowNo());
		ipsTender.setContractNo("h123548");
		ipsTender.setWebUrl(Global.getValue("weburl")+"/public/ips/ipsTenderReturn.html");
		ipsTender.setS2SUrl(Global.getValue("weburl")+"/public/ips/ipsTenderNotify.html");
		ipsTender.setRegType("1");
		ipsTender.setAuthAmt(NumberUtils.format2Str(tender.getAccount()));
		ipsTender.setTrdAmt(NumberUtils.format2Str(tender.getAccount()));
		ipsTender.setFee(0+"");
		ipsTender.setUse("第三方");
		ipsTender.setMemo1(tender.getBorrow().getId()+"");
		ipsTender.setMemo2(user.getUserId()+"");
		ipsTender.createSign();
		
	//	String[][] str = new String[][]{{"pMerCode","p3DesXmlPara","pSign"},{ipsTender.getMerCode(),ipsTender.getDesXmlPara(),ipsTender.getSign()}};
	//	ipsTender.setCommitParams(str);
		//ipsTender.setCommitParams(str);
		//String reps = ipsTender.httpSubmit();
		return ipsTender;
	}
	
	public static Transfer doIpsTransfer(IpsPay ipspay){
		Transfer transfer = new Transfer();
		//转账基础信息
		transfer.setMerBillNo(OrderNoUtils.getInstance().getSerialNumber());
		transfer.setBidNo(ipspay.getBorrowNo());
		transfer.setDate(DateUtils.newdateStr2(new Date()));
		transfer.setTransferType(ipspay.getTransferType());
		transfer.setTransferMode("1");
		transfer.setS2SUrl(Global.getValue("webrul")+"/public/ips/transterNotify.html");
		//转账详情信息
		List<TransferProw> tlist = new ArrayList<TransferProw>();
		TransferProw tprow = new TransferProw();
		tprow.setOriMerBillNo(ipspay.getTenderNo());
		tprow.setTrdAmt(ipspay.getTransMoney());
		tprow.setFacctType("1");
		tprow.setFipsAcctNo(ipspay.getPayApiId());
		tprow.setFtrdFee("0");
		tprow.setTacctType("1");
		tprow.setTipsAcctNo(ipspay.getToApiId());
		tprow.setTtrdFee("0");
		tlist.add(tprow);
		transfer.setPageList(tlist);
		transfer.doDetailXml();
		transfer.createSign();
		String xml = transfer.getSoapInputStream(transfer.getUrl(), transfer.getMerCode(), transfer.getDesXmlPara(), transfer.getSign());
		logger.info("return-------------->"+xml);
		XmlTool1 Tool = new XmlTool1();
		Tool.SetDocument(xml);
		String ss = Tool.getNodeValue("TransferResult");
		logger.info("xml = "+ss);
		XmlTool1 Too2 = new XmlTool1();
		Too2.SetDocument(ss);
		System.out.println("aaaaaaaaa----->"+Too2.getNodeValue("pErrMsg"));
		transfer.setErrCode(Too2.getNodeValue("pErrCode"));
		transfer.setErrMsg(Too2.getNodeValue("pErrMsg"));
		transfer.setSign(Too2.getNodeValue("pSign"));
		transfer.setDesXmlPara(Too2.getNodeValue("p3DesXmlPara"));
		String desXml = transfer.checkSign();  //返回解码后的xml
		return transfer;
	}
	
	/**
	 * 环迅还款
	 * @param repayment
	 * @param collectionList
	 * @return
	 */

	public static Map<String, Double> ipsRepayment(BorrowRepayment repayment ,List<BorrowCollection> collectionList,IpsRepaymentNewTrade ipsRepayment){
		Map<String, Double> map = new HashMap<String, Double>();
		Borrow borrow = repayment.getBorrow();
		ipsRepayment.setBidNo(borrow.getIpsBorrowNo());
		ipsRepayment.setRepaymentDate(DateUtils.newdateStr2(new Date()));
		ipsRepayment.setMerBillNo(OrderNoUtils.getInstance().getSerialNumber());
		ipsRepayment.setRepayType("1");
		ipsRepayment.setOutAcctNo(borrow.getUser().getApiId());
		ipsRepayment.setWebUrl(Global.getValue("weburl")+"/public/ips/ipsRepaymentReturn.html");
		ipsRepayment.setS2SUrl(Global.getValue("weburl")+"/public/ips/ipsRepaymentNotify.html");
		List<RepaymentDetail> detailList = new ArrayList<RepaymentDetail>();
		ipsRepayment.setMemo1(repayment.getId()+"");  //设置repaymentid,便于回调处理业务
		StringBuffer colltionIdStr = new StringBuffer();
		double outSumAccount = 0;  //本次还款本金总和
		double outFee = 0;  //本次还款转出方手续费总和
		double copitalSum = 0;  //本金
		double interestSum = 0; //利息
		double lateSum = 0; //罚息
		
		for (BorrowCollection con : collectionList) {
			RepaymentDetail redetail = new RepaymentDetail();
			double repayMoney = con.getCapital()+con.getInterest(); //计算本金加利息
			double intefee = Global.getDouble("borrow_fee")*con.getInterest();//利息管理费
			double late = con.getBorrowTender().getAccount()/repayment.getBorrow().getAccount()*repayment.getLateInterest(); //罚息
			redetail.setCreMerBillNo(con.getBorrowTender().getIpsTenderNo());
			redetail.setInAcctNo(con.getBorrowTender().getUser().getApiId());
			double repayAccount = repayMoney+late;
			redetail.setInAmt(NumberUtils.format2Str(repayAccount));
			
			redetail.setInFee(NumberUtils.format2Str(intefee));
			redetail.setOutInfoFee("0.00");
			detailList.add(redetail);
			String cid= con.getId()+",";
			outSumAccount +=repayAccount; //计算本金、利、罚息
			colltionIdStr.append(cid);
			copitalSum+=con.getCapital();
			interestSum+=con.getInterest();
			lateSum +=late;
		}
		ipsRepayment.setOutAmt(NumberUtils.format2Str(outSumAccount));  //转出方本金总和
		DecimalFormat df = new DecimalFormat("######0.00");
		ipsRepayment.setOutFee(df.format(outFee));  //转出方手续费总和
		ipsRepayment.setMemo2(colltionIdStr.toString());
		ipsRepayment.doDetaisXml(detailList);
		ipsRepayment.createSign();
		map.put("copitalSum", copitalSum);
		map.put("interestSum", interestSum);
		map.put("lateSum", lateSum);
		return map;
	}
	
	
	
	
}
