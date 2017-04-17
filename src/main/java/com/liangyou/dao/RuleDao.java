package com.liangyou.dao;

import com.liangyou.domain.Rule;


public interface RuleDao extends BaseDao<Rule> {
	/**
	 * 
	 * @param nid
	 * @return
	 */
	public Rule getRuleByNid(String nid);
	
}
