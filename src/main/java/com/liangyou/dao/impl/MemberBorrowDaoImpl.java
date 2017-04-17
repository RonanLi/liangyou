package com.liangyou.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.liangyou.dao.MemberBorrowDao;
import com.liangyou.domain.Borrow;
import com.liangyou.model.SearchParam;
import com.liangyou.util.DateUtils;

/**
 * 借款相关的Dao操作类
 * 
 * @author fuxingxing
 * @date 2012-7-10-下午3:07:56
 * @version
 * 
 *  <b>Copyright (c)</b> 2012-51融都-版权所有<br/>
 * 
 */
@Repository
public class MemberBorrowDaoImpl extends ObjectDaoImpl<Borrow> implements MemberBorrowDao {

	private static Logger logger = Logger.getLogger(MemberBorrowDaoImpl.class);

	/**
	 * 根据类型、排序、分页进行查找未发布的借款的数据
	 */
	public List getBorrowList(String type,long user_id, int start,int end, SearchParam param) {
		
		return null;
	}

	/**
	 * 查找未发布的借款的数据的数量
	 * @param types
	 * @param user_id
	 * @param param
	 * @return
	 */
	public int getBorrowCount(String type,long user_id, SearchParam param) {
		int total = 0;

		String sql = "form Borrow as p1 where p1.userId =?1 ";
		logger.debug("SQL:" + sql);
		String typeSql = getTypeSql(type);
		String searchSql="";
		sql = sql + typeSql+searchSql;
		// 拼装SQL
		sql = sql + typeSql;
		logger.debug("SQL:" + sql);
		
		Query query = em.createQuery(sql);
		query.setParameter(1, user_id);
		total  = query.getMaxResults();
		
		return total;
	}
	
	
	/**
	 * 根据借款的类型拼装SQL
	 * 
	 * @param type
	 * @return
	 */
	private String getTypeSql(String type) {
		String typeSql = " and (p1.status=1 or p1.status=15) and p1.account>p1.accountYes+0";//正在招标
		//String typeSql = " and p1.status=1 or p1.status=15 p1.status=5 or  and p1.account>p1.account_yes+0";//正在招标,撤回的标，正在撤回的标
		if ("unpublish".equals(type)) {
			typeSql = " and p1.status=0";// 尚未发布的借款
		} else if ("repayment".equals(type)) {
			typeSql = " and (p1.status=3 or p1.status=6 or p1.status=7)";// 正在还款的借款
		} else if ("repaymentyes".equals(type)) {
			typeSql = " and p1.status=8";// 已还完的借款
		}else if("waitmoney".equals(type)){
			typeSql = " and (p1.status=1 or p1.status = 15)  and p1.account=p1.accountYes+0";// 已还完的借款
		}else if("failrepayment".equals(type)){
			typeSql = " and (p1.status=5 or p1.status=59 or p1.status=4 " +
					" or p1.status=49 or p1.status=2  " +
					" or (p1.status=1 and (p1.account=p1.accountYes+0 and link.value*24*60*60+p1.addtime>"+DateUtils.getNowTimeStr()+")) " +
					" )";// 标的垃圾箱:5,同意撤回的标.59,。4,。49,
		}
		return typeSql;
	}
	
	public List getRepamentList(String type, long user_id){
		return null;
	}
}
