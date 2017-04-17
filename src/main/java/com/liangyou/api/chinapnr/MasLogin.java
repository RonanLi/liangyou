package com.liangyou.api.chinapnr;

public class MasLogin extends ChinapnrModel {
	
	/**
	 * 用户从1.0迁移到2.0时1.0账户在2.0中激活
	 * @param usrCustId
	 */
	public MasLogin(String usrCustId){
		super();
		this.setCmdId("MasLogin");
		this.setUsrCustId(usrCustId);
		}

}
