package com.liangyou.api.chinapnr;

import com.liangyou.util.StringUtils;

/**
 * 商户子账户信息查询
 * @author Administrator
 *
 */
public class QueryAccts extends ChinapnrModel {
	
	public QueryAccts(){
		
		super();
		this.setCmdId("QueryAccts");
	}
	private String[] paramNames = new String[] { "version", "cmdId", "merCustId","chkValue"};
	
	public StringBuffer getMerData() {

		StringBuffer MerData = new StringBuffer();
		MerData.append(StringUtils.isNull(getVersion()))
				.append(StringUtils.isNull(getCmdId()))
				.append(StringUtils.isNull(getMerCustId()));
		return MerData;
	}
	
	
	public String[] getParamNames() {
		return paramNames;
	}
	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}

}
