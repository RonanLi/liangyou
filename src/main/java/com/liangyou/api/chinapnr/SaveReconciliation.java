package com.liangyou.api.chinapnr;

public class SaveReconciliation extends CashReconciliation {

	public SaveReconciliation(String beginDate, String endDate, String pageNum,
			String pageSize) {
		super(beginDate, endDate, pageNum, pageSize);
		this.setCmdId("SaveReconciliation");
		// TODO Auto-generated constructor stub
	}

}
