package com.liangyou.context;

import com.liangyou.exception.BussinessException;

/**
 * 导出excel，这里对不同的平台，配置不同。
 * 融都系统默认是1，其他系统自己设定
 * @date 2012-7-10-上午10:26:20
 * @version  
 * <b>Copyright (c)</b> 2012-51融都-版权所有<br/>
 *
 */
public class ExcelType {
	//系统默认
	public static String[] manage_borrow_names_1=new String[]{"id","user/.username","name",
		"account","accountYes","lowestAccount","mostAccount","apr","timeStr","addtime","statusStr"};
	public static String[] manage_borrow_titles_1 = new String[]{"ID","用户名","标名","借款金额","已筹集金额",
		"最低出借金额","最高出借金额","利率","借款期限","发布时间","状态"};
	
	//国控小微
	public static String[] manage_borrow_names_2=new String[]{"id","user/.username","name",
		"account","accountYes","lowestMoney","apr","timeStr","addtime","statusStr","operateDetail"};
	public static String[] manage_borrow_titles_2=new String[]{"ID","用户名","标名","借款金额","已筹集金额",
		"最低筹集金额","利率","借款期限","发布时间","状态","审核进度"};
	
	//HAOLIP-111  lx  2014-04-24 start
	//好利贷
	public static String[] manage_borrow_names_3=new String[]{"id","user/.username","name",
		"account","accountYes","lowestAccount","mostAccount","apr","timeStr","addtime","statusStr"};
	public static String[] manage_borrow_titles_3 = new String[]{"ID","用户名","标名","借款金额","已筹集金额",
		"最低出借金额","最高出借金额","利率","借款期限","发布时间","状态"};
	public static String[] manage_allborrow_names_3=new String[]{"id","user/.username","user/.credit/.value","name",
		"account","accountYes","flowMoney","apr","timeStr","addtime","statusStr"};
	public static String[] manage_allborrow_titles_3 = new String[]{"ID","用户名","用户积分","标名","借款金额","实际入账金额","最小认购单位",
		"利率","借款期限","发布时间","状态"};
	
	public static String[] Get_manage_borrow_names(){
		int excelType = Global.getInt("excel_type");
		switch (excelType) {
		case 1://系统默认
			return manage_borrow_names_1;
		case 2://国控小微专用
			return manage_borrow_names_2;
		case 3://好利贷专用
			return manage_borrow_names_3;
		default:
			throw new BussinessException("导出excel类型没有设置。");
		}
	}
	
	public static String[] Get_manage_borrow_titles(){
		int excelType = Global.getInt("excel_type");
		switch (excelType) {
		case 1://系统默认
			return manage_borrow_titles_1;
		case 2://国控小微专用
			return manage_borrow_titles_2;
		case 3://好利贷专用
			return manage_borrow_titles_3;
		default:
			throw new BussinessException("system表--excel_type类型没有设置。");
		}
	}
	
	/**查询所有借款
	 */
	public static String[] Get_manage_allborrow_names(){
		int excelType = Global.getInt("excel_type");
		switch (excelType) {
		case 1://系统默认
			return manage_borrow_names_1;
		case 2://国控小微专用
			return manage_borrow_names_2;
		case 3://好利贷专用
			return manage_allborrow_names_3;
		default:
			throw new BussinessException("导出excel类型没有设置。");
		}
	}
	
	public static String[] Get_manage_allborrow_titles(){
		int excelType = Global.getInt("excel_type");
		switch (excelType) {
		case 1://系统默认
			return manage_borrow_titles_1;
		case 2://国控小微专用
			return manage_borrow_titles_2;
		case 3://好利贷专用
			return manage_allborrow_titles_3;
		default:
			throw new BussinessException("system表--excel_type类型没有设置。");
		}
	}
	/** 查询已经还款的明细****/
	
	public static String[] manager_borrow_repaid_names_1 = new String[]{"id","borrow/.user/.username","borrow/.name",
		"period","repaymentTime","repaymentYestime","repaymentAccount","capital","interest","statusStr"};
	public static String[] manager_borrow_repaid_titles_1 = new String[]{"ID","用户名","标名","期数","到期时间","还款时间","还款金额","还款本金","还款利息","状态"};
	
	public static String[] manager_borrow_repaid_names_2 = new String[]{"id","borrow/.user/.username","borrow/.name",
		"repaymentTime","repaymentYestime","allMoney","repaymentYescapital","repaymentYesinterest",
		"lateDays","lateInterest","prioDay","compensation","statusStr"};
	public static String[] manager_borrow_repaid_titles_2 = new String[]{"ID","用户名","标名","应还款时间","已还款时间","已还总额",
		"已还本金","已还利息","逾期天数","罚息","提前天数","已还补偿金","状态"};
	
	//互联贷
	public static String[] manager_borrow_repaid_names_3 = new String[]{"id","borrow/.user/.username","borrow/.name",
		"repaymentTime","repaymentAccount","capital","interest","borrow/.account*borrow/.borrowFee/.managefee","borrow.account*0.0015","repaymentYestime","statusStr"};
	public static String[] manager_borrow_repaid_titles_3 = new String[]{"ID","用户名","借款标题","应还时间","还款金额","还款本金","还款利息","已收管理费","保证金","还款时间","状态"};
	
	//已经还款导出列表
	public static String[] get_manager_borrow_repaid_names(){
		int excelType = Global.getInt("excel_type");
		switch (excelType) {
		case 1://系统默认
			return manager_borrow_repaid_names_1;
		case 2://国控小微专用
			return manager_borrow_repaid_names_2;
		case 3://好利贷
			return manager_borrow_repaid_names_1;
		default:
			throw new BussinessException("导出excel类型没有设置。");
		}
	}
	
	public static String[] get_manager_borrow_repaid_titles(){
		int excelType = Global.getInt("excel_type");
		switch (excelType) {
		case 1://系统默认
			return manager_borrow_repaid_titles_1;
		case 2://国控小微专用
			return manager_borrow_repaid_titles_2;
		case 3://好利贷
			return manager_borrow_repaid_titles_1;
		default:
			throw new BussinessException("导出excel类型没有设置。");
		}
	}
	//HAOLIP-111  lx  2014-04-24 end
	
	
	
	//v1.8.0.4_u2 TGPROJECT-302  lx 2014-05-22 start
	public static String[] manager_invite_rebate_names = new String[]{"inviteUser/.username","borrowTender/.user/.username","borrowTender/.account",
		"rebateAmount","addtime","verifyUser/.username","verifytime","statusStr"};
	public static String[] manager_invite_rebate_titles = new String[]{"推荐人","被推荐出借人","被推荐人出借金额","提成金额","新增时间","审核人","审核时间","状态"};
	
	public static String[] get_manager_invite_rebate_names(){
		int excelType = Global.getInt("excel_type");
		switch (excelType) {
		case 1://系统默认
			return manager_invite_rebate_names;
		default:
			throw new BussinessException("导出excel类型没有设置。");
		}
	}
	
	public static String[] get_manager_invite_rebate_titles(){
		int excelType = Global.getInt("excel_type");
		switch (excelType) {
		case 1://系统默认
			return manager_invite_rebate_titles;
		default:
			throw new BussinessException("导出excel类型没有设置。");
		}
	}
	//v1.8.0.4_u2 TGPROJECT-302  lx 2014-05-22 end
	
}
