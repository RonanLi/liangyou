package com.liangyou.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.dao.CreditDao;
import com.liangyou.dao.RuleDao;
import com.liangyou.dao.StarLogDao;
import com.liangyou.domain.Credit;
import com.liangyou.domain.Rule;
import com.liangyou.domain.StarLog;
import com.liangyou.domain.User;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.service.RuleService;
@Transactional
@Service(value="ruleService")
public class RuleServiceImpl extends BaseServiceImpl implements RuleService {

	@Autowired
	RuleDao ruleDao;
	@Autowired
	CreditDao creditDao;
	@Autowired
	StarLogDao starLogDao;
	
	@Override
	public Rule getRuleByNid(String nid){
		return ruleDao.getRuleByNid(nid);
	}

	@Override
	public void dealRule(Rule rule, String key,User user, double operateScore){//规则处理全部在这里
		
		if(rule!=null&&rule.getStatus()==1 && "star_rank".equals(rule.getNid())){//处理客户星级
			Credit credit = creditDao.getCreditByUser(user);
			double score = 0d;
			String remark = "";
			if("vip_verify".equals(key) || "repay_ontime".equals(key) || "late_less_3".equals(key)||
					"late_than_3".equals(key)){//vip审核添加积分,正常还款，逾期三天，逾期三天以上
			    score = rule.getValueDoubleByKey(key);
			    if("vip_verify".equals(key)){ 
			    	remark = "申请vip成功送积分：" +score ;
			    }else if("repay_ontime".equals(key)){
			    	remark = "正常还款送积分：" +score ;
			    }else if("late_less_3".equals(key)){
			    	remark = "逾期三天以下还款扣积分：" +score ;
			    }else if("late_than_3".equals(key)){
			    	remark = "逾期三天以上还款扣积分：" +score ;
			    };
			}
			if("add_tender".equals(key)||"add_borrow".equals(key)){//投标成功添加积分
			    score = operateScore*rule.getValueDoubleByKey(key);
			    if("add_tender".equals(key)){
			    	remark="投标成功送积分：" + score;
			    }else if("add_borrow".equals(key)){
			    	remark="借款成功送积分：" + score;
			    }
			}
			if(score !=0){
				credit.setStarScore(credit.getStarScore() + score);
				creditDao.update(credit);
				//设置信用记录数据
				StarLog starLog = new StarLog();
				starLog.setUser(new User(user.getUserId()) );
				starLog.setAddtime(new Date());
				starLog.setOperateScore(score);
				starLog.setTotalScore(credit.getStarScore());
				starLog.setType("star_rank");
				starLog.setRemark(remark);
				starLogDao.save(starLog);
			}
		}
	}
	/**
	 * 获取所有的积分记录
	 */
	@Override
	public PageDataList<StarLog> getStarLogList(SearchParam param) {
		return this.starLogDao.findPageList(param);
	}

	/**
	 * 管理员修改积分
	 * @param score
	 * @param credit
	 * @param auth
	 */
	@Override
	public void authUpdateStartScore(int score, Credit credit,User auth){
		creditDao.update(credit);
		//设置信用记录数据
		StarLog starLog = new StarLog();
		starLog.setUser(new User(auth.getUserId()) );
		starLog.setAddtime(new Date());
		starLog.setOperateScore(score);
		starLog.setTotalScore(credit.getStarScore());
		starLog.setType("star_rank");
		starLog.setRemark("管理员修改积分");
		starLogDao.save(starLog);
	}
	
	
}
