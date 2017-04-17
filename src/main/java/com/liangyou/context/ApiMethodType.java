package com.liangyou.context;
/**
 * 此类中只定义，autoServiceImpl,borrowServiceImpl中使用的参数， 在 apiServiceImpl 中判断对应的方法是否调用。
 * @author zxc
 * 1:汇付，2：易极付，...
 * 格式如下："1"触发汇付，"2"触发易极付，"1,2"触发汇付和易极付。
 */
public class ApiMethodType {

	/**
	 * 放款，调用2,3类型。易极付、双乾接口触发
	 */
	public static String FULL_SUCCESS_C="2,3";
	/**
	 * 投标，调用类型3，触发易极付、双乾冻结接口,添加环迅接口，只添加投标订单号
	 */
	public static String BORROW_TENDER_C = "2,3,4";
	/**
	 * 还款，调用类型3，触发易极付、汇付接口、双乾接口
	 */
	public static String BORROW_REPAY_D = "1,2,3";
	/**
	 *撤标都调用 
	 */
	public static String FAILL_BORROW_D = "1,2,3";
	
}
