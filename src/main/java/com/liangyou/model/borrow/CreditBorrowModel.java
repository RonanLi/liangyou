package com.liangyou.model.borrow;

import com.liangyou.domain.Borrow;
/**
 * 信用标
 * 
 * @author fuxingxing
 * @date 2012-9-5 下午5:18:52
 * @version
 *
 * <b>Copyright (c)</b> 2012-融都rongdu-版权所有<br/>
 *
 */
public class CreditBorrowModel extends BaseBorrowModel {

	private static final long serialVersionUID = 6478298326297026207L;

	private Borrow model;
	

	public CreditBorrowModel(Borrow model) {
		super(model);
		this.model=model;
		init();
	}
	
}
