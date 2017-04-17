package com.liangyou.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.dao.AreaBankDao;
import com.liangyou.dao.AreaDao;
import com.liangyou.dao.AreaMmmDao;
import com.liangyou.dao.CreditDao;
import com.liangyou.dao.CreditRankDao;
import com.liangyou.dao.StarRankDao;
import com.liangyou.dao.UserDao;
import com.liangyou.dao.UserinfoDao;
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
import com.liangyou.service.UserinfoService;

@Service
@Transactional
public class UserinfoServiceImpl extends BaseServiceImpl implements UserinfoService {

	@Autowired
	private UserinfoDao userinfoDao;
	@Autowired
	private UserDao userDao;
	@Autowired 
	private AreaDao areaDao;
	
	@Autowired
	private CreditDao creditDao;
	@Autowired
	private CreditRankDao creditRankDao;
	@Autowired
	private StarRankDao startrRankDao;
	@Autowired
	private AreaBankDao areaBankDao;
	@Autowired
	private AreaMmmDao mmmBankDao;
	
	public Userinfo getUserinfoByUserId(long user_id) {
		User user = userDao.find(user_id);
		return user.getUserinfo();
	}
	
	public void updateUserinfo(Userinfo info,long user_id) {
		User user  = userDao.find(user_id);
		if(user != null) {
			Userinfo userinfo = user.getUserinfo();
			if(info != null && userinfo != null) {
				userinfo.setMarry(info.getMarry());
				userinfo.setChild(info.getChild());
				userinfo.setEducation(info.getEducation());
				userinfo.setIncome(info.getIncome());
				userinfo.setShebao(info.getShebao());
				userinfo.setShebaoid(info.getShebaoid());
				userinfo.setHousing(info.getHousing());
				userinfo.setCar(info.getCar());
				userinfo.setLate(info.getLate());
				userinfoDao.update(userinfo);	
			}			
		}
	}
	
	public void updateBuilding(Userinfo info,long user_id) {
		User user  = userDao.find(user_id);
		if(user != null) {
			Userinfo userinfo = user.getUserinfo();
			if(info != null && userinfo != null) {
				userinfo.setHouseAddress(info.getHouseAddress());
				userinfo.setHouseArea(info.getHouseArea());
				userinfo.setHouseYear(info.getHouseYear());
				userinfo.setHouseStatus(info.getHouseStatus());
				userinfo.setHouseHolder1(info.getHouseHolder1());
				userinfo.setHouseHolder2(info.getHouseHolder2());
				userinfo.setHouseRight1(info.getHouseRight1());
				userinfo.setHouseRight2(info.getHouseRight2());
				userinfo.setHouseLoanyear(info.getHouseLoanyear());
				userinfo.setHouseLoanprice(info.getHouseLoanprice());
				userinfo.setHouseBalance(info.getHouseBalance());
				userinfo.setHouseBank(info.getHouseBank());
				userinfoDao.update(userinfo);	
			}			
		}
	}
	
	public void updateCompany(Userinfo info,long user_id) {
		User user  = userDao.find(user_id);
		if(user != null) {
			Userinfo userinfo = user.getUserinfo();
			if(info != null && userinfo != null) {				
				userinfo.setCompanyName(info.getCompanyName());
				userinfo.setCompanyType(info.getCompanyType());
				userinfo.setCompanyIndustry(info.getCompanyIndustry());
				userinfo.setCompanyOffice(info.getCompanyOffice());
				userinfo.setCompanyJibie(info.getCompanyJibie());
				userinfo.setCompanyWorktime1(info.getCompanyWorktime1());
				userinfo.setCompanyWorktime2(info.getCompanyWorktime2());
				userinfo.setCompanyWorkyear(info.getCompanyWorkyear());
				userinfo.setCompanyAddress(info.getCompanyAddress());
				userinfo.setCompanyWeburl(info.getCompanyWeburl());
				userinfo.setCompanyReamrk(info.getCompanyReamrk());
				userinfo.setCompanyTel(info.getCompanyTel());
				userinfoDao.update(userinfo);
			}			
		}		
	}
	
	public void updateFirm(Userinfo info,long user_id) {
		User user  = userDao.find(user_id);
		if(user != null) {
			Userinfo userinfo = user.getUserinfo();
			if(info != null && userinfo != null) {				
				userinfo.setPrivateType(info.getPrivateType());
				userinfo.setPrivateDate(info.getPrivateDate());
				userinfo.setPrivatePlace(info.getPrivatePlace());
				userinfo.setPrivateRent(info.getPrivateRent());
				userinfo.setPrivateTerm(info.getPrivateTerm());
				userinfo.setPrivateTaxid(info.getPrivateTaxid());
				userinfo.setPrivateCommerceid(info.getPrivateCommerceid());
				userinfo.setPrivateIncome(info.getPrivateIncome());
				userinfo.setPrivateEmployee(info.getPrivateEmployee());
				userinfoDao.update(userinfo);
			}			
		}
	}
	
	public void updateFinance(Userinfo info,long user_id) {
		User user  = userDao.find(user_id);
		if(user != null) {
			Userinfo userinfo = user.getUserinfo();
			if(info != null && userinfo != null) {				
				userinfo.setFinanceRepayment(info.getFinanceRepayment());
				userinfo.setFinanceProperty(info.getFinanceProperty());
				userinfo.setFinanceAmount(info.getFinanceAmount());
				userinfo.setFinanceCar(info.getFinanceCar());
				userinfo.setFinanceCaramount(info.getFinanceCaramount());
				userinfo.setFinaceCreditcard(info.getFinaceCreditcard());
				userinfoDao.update(userinfo);
			}			
		}
	}
	
	public void updateContact(Userinfo info,long user_id) {
		User user  = userDao.find(user_id);
		if(user != null) {
			Userinfo userinfo = user.getUserinfo();
			if(info != null && userinfo != null) {				
				userinfo.setTel(info.getTel());
				userinfo.setPhone(info.getPhone());
				userinfo.setPost(info.getPost());
				userinfo.setAddress(info.getAddress());
				userinfo.setProvince(info.getProvince());
				userinfo.setCity(info.getCity());
				userinfo.setArea(info.getArea());
				userinfo.setLinkman1(info.getLinkman1());
				userinfo.setRelation1(info.getRelation1());
				userinfo.setTel1(info.getTel1());
				userinfo.setPhone1(info.getPhone1());
				userinfo.setLinkman2(info.getLinkman2());
				userinfo.setRelation2(info.getRelation2());				
				userinfoDao.update(userinfo);
			}			
		}
	}
	public void updateMate(Userinfo info,long user_id) {
		User user  = userDao.find(user_id);
		if(user != null) {
			Userinfo userinfo = user.getUserinfo();
			if(info != null && userinfo != null) {				
				userinfo.setMateName(info.getMateName());
				userinfo.setMateSalary(info.getMateSalary());
				userinfo.setMatePhone(info.getMatePhone());
				userinfo.setMateType(info.getMateType());
				userinfo.setMateOffice(info.getMateOffice());
				userinfo.setMateAddress(info.getMateAddress());
				userinfo.setMateIncome(info.getMateIncome());
				userinfo.setMateTel(info.getMateTel());			
				userinfoDao.update(userinfo);
			}			
		}
	}
	public void updateEducation(Userinfo info,long user_id) {
		User user  = userDao.find(user_id);
		if(user != null) {
			Userinfo userinfo = user.getUserinfo();
			if(info != null && userinfo != null) {				
				userinfo.setEducationRecord(info.getEducationRecord());
				userinfo.setEducationSchool(info.getEducationSchool());
				userinfo.setEducationStudy(info.getEducationStudy());
				userinfo.setEducationTime1(info.getEducationTime1());
				userinfo.setEducationTime2(info.getEducationTime2());			
				userinfoDao.update(userinfo);
			}			
		}
	}
	
	public List<Area> getAreaListByPid(int pid) {
		return areaDao.getListByPid(pid);
	}
	
	@Override
	public List<AreaBank> getAreaBankListByPid(long pid) {
		AreaBank ab = areaBankDao.find(pid);
		return areaBankDao.getListByPid(ab.getNid());
	}
	@Override
	public List<AreaMmm> getMmmBankListByPid(long pid) {
		AreaMmm ab = mmmBankDao.find(pid);
		return mmmBankDao.getListByPid(ab.getNid());
	}
	@Override
	public Area getAreaById(int id) {
		return areaDao.find(id);
	}
	
	@Override
	public PageDataList<Userinfo> getUserinfoList(SearchParam p) {
		return userinfoDao.findPageList(p);
	}

	@Override
	public void updateAll(Userinfo userinfo) {
		userinfoDao.merge(userinfo);
	}

	@Override
	public void updateUserCredit(Credit credit) {
		creditDao.update(credit);
		
	}

	@Override
	public Credit findUserCredit(User user) {
		return creditDao.getCreditByUser(user);
	}

	@Override
	public CreditRank getCreditRankByName(String name) {
		return creditRankDao.getCreditRankByName(name);
	}

	@Override
	public StarRank getStarRankByRank(int rank) {
		return startrRankDao.getStartRankByRank(rank);
	}
	
	
}

