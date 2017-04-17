package com.liangyou.dao.impl;


import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.BaseDao;
import com.liangyou.dao.IpsRepayDetailDao;
import com.liangyou.domain.IpsRepayDetail;

/** 
*未经授权不得进行修改、复制、出售及商业使用。
* @ClassName:RDtuoguan_P2P
* @Description: 还款调用环迅接口记录
* <b>Copyright (c)</b> 杭州融都科技有限公司-版权所有<br/>
* @author wujing  wj@erongdu.com
* @date 2014-10-29 下午5:26:57 
* @version:1.0 
*/
@Repository("ipsRepayDetailDao")
public class IpsRepayDetailDaoImpl extends ObjectDaoImpl<IpsRepayDetail>
		implements IpsRepayDetailDao {

	@Override
	public IpsRepayDetail getIpsRepayByOrdId(String ordId) {
		String jpql = "from IpsRepayDetail where ordId = ?1";
		Query query = em.createQuery(jpql);
		query.setParameter(1, ordId);
		List list = query.getResultList();
		if (list.size()>0) {
			return (IpsRepayDetail)list.get(0);
		}
		return null;
	}
	
	

}
