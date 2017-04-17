package com.liangyou.model.borrow;

import com.liangyou.context.Constant;
import com.liangyou.domain.Borrow;
/**
 * 资产标或者净值标
 * 
 * @author fuxingxing
 * @date 2012-9-5 下午4:37:46
 * @version
 *
 * <b>Copyright (c)</b> 2012-融都rongdu-版权所有<br/>
 *
 */
public class PropertyBorrowModel extends BaseBorrowModel {
	
	private Borrow model;
	
	private static final long serialVersionUID = -1490035608742973452L;

	public PropertyBorrowModel(Borrow model) {
		super(model);
		this.model=model;
		if( model.getType() ==0){
			this.model.setType(Constant.TYPE_PROPERTY);
		}
		init();
	}
}
