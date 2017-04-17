package com.liangyou.api.chinapnr;

/**
 * 商户扣款对账
 * @author Administrator
 *
 */
public class TrfReconciliation extends CashReconciliation {

	public TrfReconciliation(String beginDate, String endDate, String pageNum,
			String pageSize) {
		super(beginDate, endDate, pageNum, pageSize);
		this.setCmdId("TrfReconciliation");
	}

}
