package com.liangyou.model.borrow;

import com.liangyou.context.Constant;
import com.liangyou.domain.Borrow;
import com.liangyou.tool.interest.EndInterestCalculator;
import com.liangyou.tool.interest.InterestCalculator;


/**
 * 流转标
 * 
 * @author fuxingxing
 * @date 2012-11-2 下午4:27:51
 * @version
 *
 * <b>Copyright (c)</b> 2012-融都rongdu-版权所有<br/>
 *
 */
public class FlowBorrowModel extends BaseBorrowModel {
	
	private static final long serialVersionUID = 7375703874958748525L;

	private Borrow model;

	public FlowBorrowModel(Borrow model) {
		super(model);
		this.model=model;
		this.model.setType(Constant.TYPE_FLOW);
		init();
	}

	@Override
	public double calculateInterest() {
		InterestCalculator ic= interestCalculator();
		double interest=ic.getMoneyPerMonth()*ic.getPeriod()-getModel().getFlowMoney()*getModel().getFlowCount();
		return interest;
	}

	@Override
	public InterestCalculator interestCalculator() {
		Borrow model=getModel();
		double account=model.getFlowMoney()*model.getFlowCount();
		InterestCalculator ic=interestCalculator(account);
		return ic;
	}
	//ZTCAIFU-15 wsl 流转标还款方式处理 start
	/*@Override
	public InterestCalculator interestCalculator(double validAccount) {
		Borrow model=getModel();
		InterestCalculator ic=null;
		double apr=model.getApr()/100;
		if(model.getIsday()==1){
			int time_limit_day=model.getTimeLimitDay();
			ic =new EndInterestCalculator(validAccount,apr,time_limit_day);
		}else{
			int period=model.getTimeLimit();
			ic =new EndInterestCalculator(validAccount,apr,period,InterestCalculator.TYPE_MONTH_END);
		}
		ic.each();
		return ic;
	}*/
	@Override
	public InterestCalculator interestCalculator(double validAccount) {
		return super.interestCalculator(validAccount);
	}
	//ZTCAIFU-15 wsl 流转标还款方式处理 end
	
}
