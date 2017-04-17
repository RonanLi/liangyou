package com.liangyou.dao.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.liangyou.dao.RewardParamDao;
import com.liangyou.domain.RewardParam;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;

/**
 * @Desc 奖励参数（面额）   数据层接口实现类
 * @author yjt_anzi
 *
 */
@Repository(value = "rewardParamDao")
public class RewardParamDaoImpl extends ObjectDaoImpl<RewardParam> implements
		RewardParamDao {

	

}
