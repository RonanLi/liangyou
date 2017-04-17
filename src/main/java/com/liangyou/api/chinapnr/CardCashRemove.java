package com.liangyou.api.chinapnr;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.log4j.Logger;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liangyou.util.StringUtils;
public class CardCashRemove extends ChinapnrModel{
	private static final Logger logger=Logger.getLogger(CardCashRemove.class);
	/**
	 * 
	 * 取现银行卡解绑
	 * */
	
	private String cardId;//取现银行的账户号（银行卡号）
		
	public CardCashRemove() {
		
	}

	public CardCashRemove(String usrCusId,String cardId) {
		super();
		this.setUsrCustId(usrCusId);
		this.setCardId(cardId);
		this.setCmdId("DelCard");
		this.setParamNames(paramNames);
	}

	
	//后台实时返回解绑银行卡
	private String[] paramNames = new String[] {
			"version","cmdId","merCustId","usrCustId","cardId","chkValue"
			};
	
	public StringBuffer getMerData() throws UnsupportedEncodingException{
		StringBuffer MerData = super.getMerData();
		MerData.append(StringUtils.isNull(getUsrCustId()))
				.append(getCardId());
		return MerData;
	}
	
	@Override
	public ChinapnrModel response(String res) throws IOException {
		super.response(res);
		try {
			JSONObject json = JSON.parseObject(res);
			this.setMerCustId("MerCustId");
			this.setCardId(json.getString("CardId"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String[] getParamNames() {
		return paramNames;
	}

	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}
	
}
