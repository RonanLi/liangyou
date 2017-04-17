package com.liangyou.model.borrow;

import com.liangyou.domain.Borrow;


/**
 * 秒还标
 * 
 * @author fuxingxing
 * @date 2012-9-5 下午4:35:59
 * @version
 *
 * <b>Copyright (c)</b> 2012-融都rongdu-版权所有<br/>
 *
 */
public class SecondBorrowModel extends BaseBorrowModel {
	
	private static final long serialVersionUID = 7375703874958748525L;

	private Borrow model;

	public SecondBorrowModel(Borrow model) {
		super(model);
		this.model=model;
		init();
	}
}
