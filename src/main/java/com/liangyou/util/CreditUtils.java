package com.liangyou.util;

import java.util.Date;

import com.liangyou.context.Global;
import com.liangyou.domain.UserCredit;
import com.liangyou.domain.UserCreditLog;
import com.liangyou.domain.UserCreditType;

public class CreditUtils {
	
	/**
	 * 获取指定精度的积分值
	 * @param credit
	 * @param type
	 * @return
	 */
	public static int  getDecimalCredit(double credit,UserCreditType type){
		double decimalManager = type.getDecimalManager();
		String num = credit + "";
		String[] array = num.split("\\.");
		double decimal = Double.parseDouble("0." + array[1]) ;
		int intNum = Integer.parseInt(array[0]);
		if(decimal>= decimalManager){//小数部分大于 decimalManager
			return intNum+1;
		}else{
			return intNum;
		}
	}
	
	/**
	 * 封装积分日志
	 * @param log
	 * @param operateValue
	 * @param operateValidValue
	 * @param userCredit
	 * @param memo
	 * @return
	 */
	public static UserCreditLog fillUserCreditLog(UserCreditLog log, long operateValue,long operateValidValue
			,UserCredit userCredit,String memo){
		log.setOperateValue(operateValue);
		log.setValidValue(operateValidValue);
		log.setRemark(memo);
		log.setValue(userCredit.getValue());
		log.setBbsValue(userCredit.getBbsValue());
		log.setTenderValue(userCredit.getTenderValue());
		log.setBbsValue(userCredit.getBorrowValue());
		log.setAddtime(new Date());
		log.setExpenseValue(userCredit.getExpenseValue());
		log.setAddip(Global.ipThreadLocal.get()+"");
		return log;
	}
	

}
