package com.liangyou.service;

import java.util.List;

import com.liangyou.domain.Area;
import com.liangyou.domain.AreaBank;
import com.liangyou.domain.AreaMmm;
import com.liangyou.domain.Credit;
import com.liangyou.domain.CreditRank;
import com.liangyou.domain.StarRank;
import com.liangyou.domain.User;
import com.liangyou.domain.Userinfo;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;

/**
 * 借款用户信息服务
 * 
 * @author 1432
 *
 */
public interface UserinfoService {

	public Userinfo getUserinfoByUserId(long user_id);

	public void updateUserinfo(Userinfo userinfo, long user_id);

	public void updateBuilding(Userinfo userinfo, long user_id);

	public void updateCompany(Userinfo userinfo, long user_id);

	public void updateFirm(Userinfo userinfo, long user_id);

	public void updateFinance(Userinfo userinfo, long user_id);

	public void updateContact(Userinfo userinfo, long user_id);

	public void updateMate(Userinfo userinfo, long user_id);

	public void updateEducation(Userinfo userinfo, long user_id);

	public void updateAll(Userinfo userinfo);

	public List<Area> getAreaListByPid(int pid);

	public Area getAreaById(int id);

	public PageDataList<Userinfo> getUserinfoList(SearchParam p);

	public void updateUserCredit(Credit credit); // 跟新用户积分信息

	public Credit findUserCredit(User user); // 获取用户积分信息

	public CreditRank getCreditRankByName(String name); // 根据信用等级名称获取信用等级对象

	public StarRank getStarRankByRank(int rank);

	List<AreaBank> getAreaBankListByPid(long pid);

	List<AreaMmm> getMmmBankListByPid(long pid);

}