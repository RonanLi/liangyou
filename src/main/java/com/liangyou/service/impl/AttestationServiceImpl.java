package com.liangyou.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.api.pay.RealNameCertQuery;
import com.liangyou.dao.AttestationDao;
import com.liangyou.dao.CreditDao;
import com.liangyou.dao.CreditLogDao;
import com.liangyou.domain.Attestation;
import com.liangyou.domain.Credit;
import com.liangyou.domain.CreditLog;
import com.liangyou.domain.User;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.service.AttestationService;

@Service
@Transactional
public class AttestationServiceImpl extends BaseServiceImpl implements AttestationService {

	@Autowired
	private AttestationDao attestationDao;
	@Autowired
	private CreditDao creditDao;
	@Autowired
	private CreditLogDao creditLogDao;
	
	@Override
	public PageDataList getSearchUserCertify(SearchParam p) {
		return attestationDao.findPageList(p);
	}
	
	@Override
	public void updateCredit(Credit c) {
		creditDao.update(c);
	}

	@Override
	public void addCreditLog(CreditLog cl) {
		creditLogDao.save(cl);
	}

	@Override
	public Attestation getAttestationById(int id) {
		return attestationDao.find(id);
	}

	@Override
	public void updateAttestation(Attestation att) {
		attestationDao.merge(att);
	}
	
	@Override
	public Attestation addAttestation(Attestation att) {
		return attestationDao.merge(att);
	}

	@Override
	public PageDataList getAttestationBySearchParam(SearchParam p) {
		return attestationDao.findPageList(p);
	}

	public int getAttestations(long userid,int typeid){
		return attestationDao.getAttestations(userid,typeid);
	}
	
	//v1.8.0.4 TGPROJECT-58 lx start
	@Override
	public void deleteAttestation(long userid, int attestationId){
		Attestation attestation=attestationDao.getAttestationByUserIdAndId(userid, attestationId);
		if(attestation!=null){
			attestation.setStatus(0);
			attestationDao.save(attestation);;
		}
	}
	//v1.8.0.4 TGPROJECT-58 lx start
}
