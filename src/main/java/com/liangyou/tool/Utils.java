package com.liangyou.tool;

import com.liangyou.tool.interest.InterestCalculator;
import com.liangyou.tool.interest.MonthInterestCalculator;


public class Utils {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		InterestCalculator ic =new MonthInterestCalculator(100,0.12,3);
		ic.each();
		System.out.println(ic.toString());
	}
	
	/**
	 * 
	 * @param p 本金
	 * @param r 月利率
	 * @param n 还款月数
	 * @return  每月还款金额
	 */

	public static double Mrpi(double p,double r,int mn){
		double mr=r/12;
		
		double aprPow=Math.pow(1+mr,mn);
		
		double monPay=p*mr*aprPow/(aprPow-1);
		
		return monPay;
		
	}
	
	/**
	 * 
	 * 三个变量x,y,z  
     *      x代表提现金额 y代表现在净资产减15天内的充值总值z代表提现手续费
     *      1.  0≤x ≤1500 或者 y<15000
     * 无论y为何值    z=0.002x
     *       2.  y≥x
     *      1500<x ≤30000     z=3
     *       30000<x ≤50000    z=5
     *       3. y <x
     *       1500<y ≤30000     z=3+(x-y)0.002
     *       30000<y ≤50000    z=5+(x-y)0.002
	 * 
	 * 
	 * @param x x代表提现金额 
	 * @param y y代表现在净资产减15天内的充值总值
	 * @param r r代表提现手续费率
	 * @param maxCash
	 * @return 提现手续费
	 */
	public static double GetCashFee(double x,double y,double r,double maxCash){
		if(x<=1500||y<=1500){
			return r*x;
		}else if(y>=x){
			if(x>1500&&x<=30000){
				return 3.0;
			}else{
				return 5;
			}
		}else{
			if(y<=30000){
				return 3+(x-y)*r;
			}else{
				return 5+(x-y)*r;
			}
		}
	}
	/**
	 * 莲花财富提现规则
	 */
	public static double getCashFeeForlhd(double x,double y,double r,double maxCash){
		 if(y>=x){
			if(x>0&&x<=30000){
				return 3.0;
			}else{
				return 5;
			}
		}else{
			if(y<=30000){
				return 3+(x-y)*r;
			}else{
				return 5+(x-y)*r;
			}
		}
	}
	/**
	 * 中融资本的提现费用计算公式
	 * @param x
	 * @param y
	 * @param r
	 * @param maxCash
	 * @return
	 */
	public static double getCashFeeForZRZB(double x,double y,double r,double maxCash){
		if(x<=1500||y<=1500){
			return r*x;
		}else if(y>=x){
			if(x>1500&&x<=30000){
				return 3.0;
			}else{
				return 5;
			}
		}else{
			if(y<=30000){
				return 3+(x-y)*r;
			}else{
				return 5+(x-y)*r;
			}
		}
	}
	/**
	 * 及时雨提现规则
	 */
	public static double getCashFeeForJJY(double x,double y,double r,double maxCash){
		/*if(x<y){
			if (x>=0) {
				return (y-x)*r;
			}else {
				return y*r;
			}
		}else{
			return 0;
		}*/
		if(y>=x){
			if(x>0&&x<=30000){
				return 3.0;
			}else{
				return 5;
			}
		}else{
			if(y<=30000){
				return 3+(x-y)*r;
			}else{
				return 5+(x-y)*r;
			}
		}
	}
	/**
	 * 山水聚宝提现规则
	 */
	public static double getCashFeeForSSJB(double x,double y,double r,double maxCash){
		 if(y>=x){
			return 0;
		}else{
			if(y>0){
				return (x-y)*r;
			}else{
				return x*r;
			}
		}
	}
	/**
	 * 
	 * @param x
	 * @param y
	 * @param r
	 * @param maxCash
	 * @return
	 */
	public static double getCashFeeForZrzbZero(double x,double y,double r,double maxCash){
		if(y>=x){
			if(x>1500&&x<=30000){
				return 0;
			}else{
				return 0;
			}
		}else{
			if(y<=30000){
				return 3+(x-y)*r;
			}else{
				return 5+(x-y)*r;
			}
		}
	}
	/**
	 * 中融资本新提现规则  
	 * @param x
	 * @param r
	 * @param money
	 * @return
	 */
	public static double getCashFeeForZrzbZero(double x,double r,double money){
		if(x<money){
			if (x>=0) {
				return (money-x)*r;
			}else {
				return money*r;
			}
		}else{
			return 0;
		}
	}
	
	public static double GetLargeCashFee(double x,double y,double r,double maxCash){
		if(y>=500000){
			return x/10000;
		}else if(y<20000){
			if(y<0){
				y=0;
			}
			return y/10000+(x-y)*r;
		}else{
			if(y>=x){
				return x/10000;
			}else{
				return y/10000+(x-y)*r;
			}
		}
	}
	
	/**
	 * 徽贷提现规则  
	 * @param x
	 * @param r
	 * @param money
	 * @return
	 */
	public static double getCashFeeForHuidai(double money,double x,double r){
		if(x<money){
			if (x>=0) {
				return (money-x)*r;
			}else {
				return money*r;
			}
		}else{
			return 0;
		}
	}
	
}
