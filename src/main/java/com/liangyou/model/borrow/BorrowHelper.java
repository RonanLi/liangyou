package com.liangyou.model.borrow;

import com.liangyou.domain.Borrow;

public class BorrowHelper {
	public static BorrowModel getHelper(int type, Borrow model) {
		switch (type) {
		case 100:
			return new BaseBorrowModel(model);
		case 101:
			return new SecondBorrowModel(model);
		case 102:
			return new CreditBorrowModel(model);
		case 103:
			return new MortgageBorrowModel(model);
		case 104:
			return new PropertyBorrowModel(model);
		case 110:
			return new FlowBorrowModel(model);
		default:
			throw new RuntimeException("不正确的借款类型:" + type);
		}
	}

	public static BorrowModel getHelper(Borrow model) {
		switch (model.getType()) {
		case 100:
			return new BaseBorrowModel(model);
		case 101:
			return new SecondBorrowModel(model);
		case 102:
			return new CreditBorrowModel(model);
		case 103:
			return new MortgageBorrowModel(model);
		case 104:
			return new PropertyBorrowModel(model);
		case 105:// 担保标
			return new MortgageBorrowModel(model);
		case 110:
			return new FlowBorrowModel(model);
		case 107:
			return new CharityBorrowModel(model);// 慈善标
		case 108: // 工薪信用标
			return new CreditBorrowModel(model);
		case 109:// 网商信用标
			return new CreditBorrowModel(model);
		case 111:// 学业信用标
			return new CreditBorrowModel(model);
		case 112:// 经营信用标
			return new CreditBorrowModel(model);
		case 115:// 体验标 -- add by gy 2016-10-14 09:32:03
			return new CreditBorrowModel(model);
		default:
			throw new RuntimeException("不正确的Borrow类型:" + model.getType());
		}

	}

}
