package com.liangyou.dao;

import com.liangyou.domain.IpsPay;

/** 
*未经授权不得进行修改、复制、出售及商业使用。
* @ClassName:RDtuoguan_P2P
* @Description: 环迅操作记录持久层
* <b>Copyright (c)</b> 杭州融都科技有限公司-版权所有<br/>
* @author wujing  wj@erongdu.com
* @date 2014-10-29 上午10:19:18 
* @version:1.0 
*/
public interface IpsPayDao extends BaseDao<IpsPay> {
	
	/**
	 * 根据订单号查询环迅操作记录
	 * @param ordId
	 * @return
	 */
	public IpsPay getIpsPayByOrdId(String ordId);
	
	/**
	 * 根据订单号修改环迅操作记录状态
	 * @param status
	 * @param ordId
	 */
	public void updateIpsStatus(String status,String ordId,String code,String remsg);
	
	

}
