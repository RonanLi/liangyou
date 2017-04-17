package com.liangyou.service;

import com.liangyou.domain.Credit;
import com.liangyou.domain.Rule;
import com.liangyou.domain.StarLog;
import com.liangyou.domain.User;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;

/**
 * 规则服务
 * 
 * @author 1432
 *
 */
public interface RuleService {

	/**
	 * 获取对应类型的规则
	 * 
	 * @param nid
	 * @return
	 */
	public Rule getRuleByNid(String nid);

	/**
	 * 处理级别
	 * 
	 * @param rule
	 */
	public void dealRule(Rule rule, String key, User user, double operateScore);

	/**
	 * 查询所有积分记录
	 * 
	 * @param param
	 * @return
	 */
	public PageDataList<StarLog> getStarLogList(SearchParam param);

	/**
	 * 管理员修改积分
	 * 
	 * @param score
	 * @param credit
	 * @param auth
	 */
	public void authUpdateStartScore(int score, Credit credit, User auth);

}
