package com.liangyou.api.moneymoremore;

/**
 * 余额查询
 * //v1.8.0.4  TGPROJECT-25   qj  2014-04-03 start
 * //v1.8.0.4  TGPROJECT-25   qj  2014-04-03 stop
 * @author Qinjun
 */
	

public class MmmBalanceQuery extends MmmModel {

	private String platformId;
	
	private String platformType = "1";//1.托管账户 2.自有账户

	//提交参数列表
	private String[] commitParamNames = new String[] { "platformId", "platformType", "platformMoneymoremore"};

	public MmmBalanceQuery(int operation) {
		super(operation);
	}

	public String getPlatformId() {
		return platformId;
	}

	public void setPlatformId(String platformId) {
		this.platformId = platformId;
	}

	public String getPlatformType() {
		return platformType;
	}

	public void setPlatformType(String platformType) {
		this.platformType = platformType;
	}

	public String[] getCommitParamNames() {
		return commitParamNames;
	}

	public void setCommitParamNames(String[] commitParamNames) {
		this.commitParamNames = commitParamNames;
	}

	

}
