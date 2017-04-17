package com.liangyou.api.chinapnr;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import org.apache.log4j.Logger;
import chinapnr.SecureLink;

import com.liangyou.context.Global;
import com.liangyou.util.StringUtils;


/**
 * 汇付债权转让2.0接口
 * @author zxc
 *
 */
public class CreditAssign extends ChinapnrModel {
	
	Logger logger = Logger.getLogger(CreditAssign.class);

	private String SellCustId;   //转让用户客户号
	private String CreditAmt;    //承接金额
	private String CreditDealAmt;  //实际转让金额
	private String BidDetails;   //债权转让明细
	private String Fee;   //转让手续费
	private String DivDetails; //手续费分账账户明细
	private String BuyCustId; //承接用户客户号
	
	public CreditAssign() {
		super();
	}

	public CreditAssign(String sellCustId,String creditAmt,String creditDealAmt,
			String bidDetails,String fee,String divDetails,String buyCustId){
		
		this.setMerId("CreditAssign");
		this.setSellCustId(sellCustId);
		this.setCreditAmt(creditAmt);
		this.setCreditDealAmt(creditDealAmt);
		this.setBidDetails(bidDetails);
		this.setFee(fee);
		this.setDivDetails(divDetails);
		this.setBuyCustId(buyCustId);
		this.setBgRetUrl(Global.getValue("weburl") + "/public/chinapnr/creditassign.html");
		
	}
	
	private String[] paramNames = new String[] { 
		"version", "cmdId", "merCustId",
		"sellCustId","creditAmt", 
		"creditDealAmt","bidDetails","fee","divDetails","buyCustId","ordId","ordDate","retURL","bgRetURL",
		"merPriv","chkValue"
	};
	
	public StringBuffer getMerData() throws UnsupportedEncodingException{
		//chkValue不能当做签名参数
		StringBuffer MerData =super.getMerData();
		MerData.append(getSellCustId())
        .append(getCreditAmt())
        .append(getCreditDealAmt())
        .append(getBidDetails())
        .append(getFee())
        .append(getDivDetails())
        .append(getBuyCustId())
        .append(getOrdId())
        .append(getOrdDate())
        .append(getRetUrl())
        .append(getBgRetUrl())
        .append(getMerPriv());
		            
		return MerData;	
	}
	  
	 //债权转让回调参数封装
	@Override
	public StringBuffer getCallbackMerData() {
		StringBuffer returData = new StringBuffer();
					 try {
						 returData.append(StringUtils.isNull(getCmdId()))
						 		.append(StringUtils.isNull(getRespCode()))
						 		.append(StringUtils.isNull(getMerCustId()))
						 		.append(StringUtils.isNull(getSellCustId()))
						 		.append(StringUtils.isNull(getCreditAmt()))
						 		.append(StringUtils.isNull(getCreditDealAmt()))
						 		.append(StringUtils.isNull(getFee()))
						 		.append(StringUtils.isNull(getBuyCustId()))
						 		.append(StringUtils.isNull(getOrdId()))
						 		.append(StringUtils.isNull(getOrdDate()))
						 		.append(StringUtils.isNull(getRetUrl()))
						 		.append(StringUtils.isNull(getBgRetUrl()))
						 		.append(StringUtils.isNull(getMerPriv()));
						 
					}catch (Exception e) {
						logger.error(e);
						e.printStackTrace();
					}
					
		return returData;
	}
		
	public int callback(){
		logger.info("进入用户债权转让回调验证");
		String merKeyFile=createPubKeyFile();
		SecureLink sl = new SecureLink( ) ;
		String retData = doCallbackURLDecoder(this.getCallbackMerData());
		logger.info("pubKeyFile:"+merKeyFile);
		logger.info("CallbackMerData:"+retData);
		logger.info("getChkValue:"+getChkValue());
		int ret = sl.VeriSignMsg(merKeyFile ,retData, getChkValue());
		logger.info("债权转签名："+ret);
		return ret;
	}
	
	

	public String getSellCustId() {
		return SellCustId;
	}


	public void setSellCustId(String sellCustId) {
		SellCustId = sellCustId;
	}


	public String getCreditAmt() {
		return CreditAmt;
	}


	public void setCreditAmt(String creditAmt) {
		CreditAmt = creditAmt;
	}


	public String getCreditDealAmt() {
		return CreditDealAmt;
	}


	public void setCreditDealAmt(String creditDealAmt) {
		CreditDealAmt = creditDealAmt;
	}


	public String getBidDetails() {
		return BidDetails;
	}


	public void setBidDetails(String bidDetails) {
		BidDetails = bidDetails;
	}


	public String getFee() {
		return Fee;
	}


	public void setFee(String fee) {
		Fee = fee;
	}


	public String getDivDetails() {
		return DivDetails;
	}


	public void setDivDetails(String divDetails) {
		DivDetails = divDetails;
	}


	public String getBuyCustId() {
		return BuyCustId;
	}


	public void setBuyCustId(String buyCustId) {
		BuyCustId = buyCustId;
	}
	
}
