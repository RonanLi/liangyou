package com.liangyou.model.borrow;

import com.liangyou.domain.Borrow;

/**
 * 慈善标
 * 
 * @author fuxingxing
 * @date 2012-9-5 下午5:18:32
 * @version
 *
 * <b>Copyright (c)</b> 2012-融都rongdu-版权所有<br/>
 *
 */
public class CharityBorrowModel extends BaseBorrowModel{
	private static final long serialVersionUID = -965497211520156565L;
	private Borrow model;

	public CharityBorrowModel(Borrow model) {
		super(model);
		this.model=model;
		init();
	}

}
