package com.liangyou.context;

import com.liangyou.exception.BussinessException;
import com.liangyou.model.OrderFilter.OrderType;



/**
 * 网站特殊排序问题解决，这里对不同的平台，配置不同。
 * 融都系统默认是1，其他系统自己设定
 * @date 2012-7-10-上午10:26:20
 * @version  
 * <b>Copyright (c)</b> 2012-51融都-版权所有<br/>
 *
 */
public class SortType {
	
  private static String showBorrowing_order_name_1="id";
  private static OrderType showBorrowing_order_type_1=OrderType.DESC;
	
  private static String showBorrowing_order_name_2="borrowProperty.endTime";
  private static OrderType showBorrowing_order_type_2=OrderType.ASC;
  
  public static String Get_showBorrowing_order_name(){
		int excelType = Global.getInt("order_type");
		switch (excelType) {
		case 1://系统默认
			return showBorrowing_order_name_1;
		case 2://国控小微专用
			return showBorrowing_order_name_2;
		default:
			throw new BussinessException("导出excel类型没有设置。");
		}
	}
	
	public static OrderType Get_showBorrowing_order_type(){
		int excelType = Global.getInt("order_type");
		switch (excelType) {
		case 1://系统默认
			return showBorrowing_order_type_1;
		case 2://国控小微专用
			return showBorrowing_order_type_2;
		default:
			throw new BussinessException("system表--order_type类型没有设置。");
		}
	}
  
  
  
}
