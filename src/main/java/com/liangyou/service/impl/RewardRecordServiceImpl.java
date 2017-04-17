package com.liangyou.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.dao.ActivityConfigDao;
import com.liangyou.dao.RewardParamDao;
import com.liangyou.dao.RewardRecordDao;
import com.liangyou.dao.RewardRecordSqlDao;
import com.liangyou.dao.RewardUseRecordDao;
import com.liangyou.domain.ActivityConfig;
import com.liangyou.domain.RewardParam;
import com.liangyou.domain.RewardRecord;
import com.liangyou.domain.RewardUseRecord;
import com.liangyou.domain.User;
import com.liangyou.model.PageDataList;
import com.liangyou.model.RewardRecordSql;
import com.liangyou.model.SearchParam;
import com.liangyou.service.RewardRecordService;
import com.liangyou.util.DateUtils;
import com.liangyou.util.StringUtils;

/**
 * @Desc 奖励记录管理  服务层接口实现类
 * @author yjt_anzi
 *
 */
@Service(value="rewardRecordService")
@Transactional 
public class RewardRecordServiceImpl implements RewardRecordService {

	private final static Logger logger=Logger.getLogger(RewardRecordServiceImpl.class);
	@Autowired
	private RewardRecordDao rewardRecordDao;
	@Autowired
	private RewardParamDao rewardParamDao;
	@Autowired
	private ActivityConfigDao activityConfigDao;
	@Autowired
	private RewardUseRecordDao rewardUseRecordDao;
	@Autowired
	RewardRecordSqlDao rewardRecordSqlDao;
	
	//获取当前登录用户的有效奖励
	@Override
	public List<Map<String, Object>> enableRewardByUser(String rewardType,User uInfo, Integer bRules) {
		List<Map<String, Object>> resultjay = new ArrayList<Map<String, Object>>();
		//用户有效奖励
		List<RewardRecord> reardRList = rewardRecordDao.enableRewardByUser(rewardType, uInfo, bRules);
		/* result JSONArray:
		 [{"rewardObj":rewardRecord,"rules":"","enableDate":""}]
		  */
		if(reardRList!=null && reardRList.size()>0){
			for(int i=0;i<reardRList.size();i++){
				RewardRecord rrinfo = reardRList.get(i);
				ActivityConfig acinfo = rrinfo.getActivityConfig();
				//TODO 当前日期是否符合活动日期
				List<RewardParam> rpList = acinfo.getRewardParam();
				//JSONObject jsonobj = new JSONObject();
				Map<String, Object> jsonobj = new HashMap<String, Object>();
				jsonobj.put("rewarId", rrinfo.getRewardRecordId()); //奖励信息 （红包）
				//奖励有效期
				int rTimeLimit = acinfo.getRewardTimeLimit();  //有效天数
				Date createDate = rrinfo.getCreateDate();	//奖励发放日期
				Calendar c = Calendar.getInstance();  
	            c.setTime(createDate);  
	            c.add(c.DATE, rTimeLimit); 
	            String enableDate = DateUtils.newdateStr6(c.getTime());
	            jsonobj.put("enableDate", enableDate);
	            jsonobj.put("borrowRules", acinfo.getBorrowRules());
	            jsonobj.put("rewardRecordId", rrinfo.getRewardRecordId());
				//使用规则（红包投标规则）
				if(rpList!=null && rpList.size()>0){
					for(RewardParam rpinfo:rpList){
						if(rpinfo.getDenomination() == rrinfo.getDenomination()){
							jsonobj.put("denomination", rrinfo.getDenomination());
							jsonobj.put("rules", rpinfo.getUseRules());
							break;
						}
					}
				}
				
				resultjay.add(jsonobj);
			}
		}
		return resultjay;
	}

	//给用户发放奖励
	@Override
	public boolean prize(List<ActivityConfig> activityCLisst,User uInfo,String sessionUid) {
		boolean isPrize = false;
		for(ActivityConfig ac : activityCLisst){
			String rewardTypeCode = ac.getRewardType();	//奖励类型
			if(StringUtils.isNull(rewardTypeCode).equals(""))
				continue;
			List<RewardParam> rewardPList = rewardParamDao.findByProperty("activity", ac);	//奖励面额ac.getRewardParam()
			if(rewardPList!=null && rewardPList.size()>0){
				for(RewardParam rr : rewardPList){
					for(int i=0;i<rr.getCounts();i++){ //张数
						RewardRecord rewardRInfo = new RewardRecord();
						rewardRInfo.setDenomination(rr.getDenomination());
						rewardRInfo.setUser(uInfo);
						rewardRInfo.setCreateDate(new Date());
						rewardRInfo.setCreateBy(sessionUid);
						rewardRInfo.setActivityConfig(ac);
						rewardRInfo.setIsEnable("0");	//默认有效
						
						rewardRecordDao.save(rewardRInfo);
						isPrize = true;
					}
					
				}
			}
			
		}
		return isPrize;
	}

	@Override
	public List<RewardRecord> getRewardRecordById(Long rId, Long uId) {
		return rewardRecordDao.getRewardRecordById(rId, uId);
	}

	@Override
	public void saveRewardUseRecord(RewardUseRecord ru) {
		rewardUseRecordDao.save(ru);
	}

	@Override
	public int findByDenominationCount(SearchParam param) {
		return  rewardRecordDao.findByDenominationCount(param);
	}

	@Override
	public List<RewardRecord> findByDenominationList(SearchParam param) {
		return  rewardRecordDao.findByDenominationList(param);
	}
	
	public PageDataList<RewardRecord> findAll(SearchParam param) {
		return  rewardRecordDao.findAllPageList(param);
	}
	/**
	 * 根根据条件查询各种红包的数量
	 * @param param
	 * @return
	 */
	public List<RewardRecord> findCount(SearchParam param){
		return  rewardRecordDao.findCount(param);
	}
	
	
	/**
	 * 根据有效日期查询数据
	 * @param param
	 * @return
	 */
	public List<RewardRecordSql> findByValidityAll(SearchParam param){
		return  rewardRecordDao.findByValidityAll(param);
	}
	
	/**
	 * 根根据条件查询各种红包的金额
	 * @param param
	 * @return
	 */
	public double findSumDenomination(SearchParam param){
		return  rewardRecordDao.findSumDenomination(param);
	}
	
	@Override
	public void saveRewardRecord(RewardRecord rr) {
		rewardRecordDao.save(rr);
	}
	
	/**
	 * @Desc 后台-奖励记录报表
	 * @param param
	 * @param paramMap
	 * @return
	 * @author yjt_anzi
	 */
	public PageDataList<RewardRecordSql> findByPage(SearchParam param){
		return rewardRecordDao.findByPage(param);
	}
	
	/**
	 * @Desc 根据条件查询所有奖励记录
	 */
	@Override
	public List<RewardRecordSql> getSumRewardRecordList(SearchParam param) {
		//PageDataList<RewardRecordSql> pList = rewardRecordSqlDao.findAllPageList(param);
		PageDataList<RewardRecordSql> pList = rewardRecordDao.findByPage(param);
		return pList.getList();
	}

	
	/**
	 * @Desc 获得用户红包奖励列表
	 * @param param
	 * @return
	 */
	@Override
	public PageDataList<RewardRecordSql> getRewardByUid(SearchParam param) {
		PageDataList<RewardRecordSql> pList = rewardRecordDao.findByUid(param);
		return pList;
	}
	
	/**
	 * @Desc 根据用户ID 统计用户奖励个数
	 * @param param
	 * @return
	 */
	public Map<String,Integer> countsByUid(SearchParam param){
		List countsList = rewardRecordDao.countsByUid(param);
		Map<String,Integer> resultMap = new HashMap<String,Integer>();
		resultMap.put("unusedC", 0);	//未使用
		resultMap.put("usedC", 0);		//已使用
		resultMap.put("overdueC", 0);	//过期
		if(countsList!=null && countsList.size()>0){
			for(int i=0;i<countsList.size();i++){
				RewardRecordSql rrs =  (RewardRecordSql) countsList.get(i);
				String keys = rrs.getIsEnable();
				Integer vals = rrs.getCountsnum();
				if(!StringUtils.isBlank(keys)){
					if("0".equals(keys))
						resultMap.put("unusedC", vals);
					else if("1".equals(keys))
						resultMap.put("usedC", vals);
					else if("2".equals(keys))
						resultMap.put("overdueC", vals);
				}
			}
		}
		
		return resultMap;
	}

	/**
	   @Desc 更新过期的红包状态
	 * @param param
	 * @return
	 */
	@Override
	public void updateStatusByDate(Date date) {
		rewardRecordDao.updateStatusByDate(date);
		
	}
	
	/**
	 * @Desc 判断用户是否拥有过某类型的红包奖励
	 * @param userinfo
	 * @param type
	 * @return
	 */
	public boolean isLMByType(User userinfo,String type,ActivityConfig acinfo){
		SearchParam param = new SearchParam();
		param.addParam("user", userinfo);
		//param.addParam("fromBeType", StringUtils.isNull(userinfo.getFromBe())+type);
		/*if(!StringUtils.isBlank(userinfo.getFromBe()) && "gtgj".equals(userinfo.getFromBe()))
			param.addParam("fromBe", "9");	//高铁管家 标示
*/		if(acinfo!=null)
			param.addParam("activityConfig", acinfo);	//活动
		param.addParam("rewardType", "0");
		List<RewardRecord> rrList = rewardRecordDao.findByCriteria(param);
		if(rrList!=null && rrList.size()>0)
			return true;
		else
			return false;
		
	}
	
	/**
	 * @Desc 根据活动 给用户发放奖励
	 * @param acList
	 * @param userinfo
	 * @param type
	 * @return
	 */
	public boolean prizeForActivity(List<ActivityConfig> acList,User userinfo,String type){
		for (ActivityConfig aConfig : acList) {
			// 再次验证 该用户是否拥有参加此次活动资格
			SearchParam param = new SearchParam();
			param.addParam("activityConfig", aConfig);
			param.addParam("user", userinfo);
			param.addGroupBy("createDate");
			List<RewardRecord> rrList = rewardRecordDao.findByCriteria(param);
			//若活动生效次数上限则不发奖励
			if(rrList!=null && rrList.size()>0 && rrList.size()>=Integer.parseInt(aConfig.getEnableNumber()))
				continue;
			rewardRecordDao.save(this.getRewardList(aConfig, userinfo));
		}
		return true;
	}
	
	
	private List<RewardRecord> getRewardList(ActivityConfig aConfig, User user){
		List<RewardRecord> list = new ArrayList<RewardRecord>();
		if(null != aConfig.getRewardParam() && aConfig.getRewardParam().size() > 0){
			for (RewardParam rp : aConfig.getRewardParam()) {
				RewardRecord re = new RewardRecord();
				re.setDenomination(rp.getDenomination());
				re.setUser(user);
				re.setCreateDate(new Date());
				re.setActivityConfig(aConfig);
				re.setIsEnable(String.valueOf(0));
				re.setFromBe(String.valueOf(0));
				re.setRewardType(String.valueOf(0));
			/*	if(!StringUtils.isBlank(user.getFromBe()) && "gtgj".equals(user.getFromBe()))
					re.setFromBe("9");*/	//高铁管家 标示
				
				list.add(re);
			}
		}
		return list;
	}

	@Override
	public void prizeForWeixin(ActivityConfig ac, User user, String[] money) {
		if(!isLMByType(user, "8", ac)){
			if(null != money && money.length>0){
				List<RewardRecord> rlist = new ArrayList<RewardRecord>();
				for(int i = 0 ;i<money.length;i++){
					RewardRecord record = new RewardRecord();
					record.setActivityConfig(ac);
					record.setUser(user);
					record.setDenomination(Double.parseDouble(money[i]));
					record.setCreateBy(String.valueOf(user.getUserId()));
					record.setIsEnable(String.valueOf(0));
					record.setRewardType(String.valueOf(0));
					record.setFromBe(String.valueOf(8));//微信标识
					record.setCreateDate(new Date());
					rlist.add(record);
				}
				rewardRecordDao.save(rlist);
			}
		}
		
	}
}
