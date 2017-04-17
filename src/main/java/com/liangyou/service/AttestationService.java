package com.liangyou.service;

import com.liangyou.domain.Attestation;
import com.liangyou.domain.Credit;
import com.liangyou.domain.CreditLog;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;

/**
 * 证明服务
 * 
 * @author 1432
 *
 */
public interface AttestationService {

	/**
	 * 用户认证
	 * 
	 * @param p
	 * @return
	 */
	public PageDataList getSearchUserCertify(SearchParam p);

	public PageDataList getAttestationBySearchParam(SearchParam p);

	public void updateCredit(Credit c);

	public void addCreditLog(CreditLog cl);

	public Attestation getAttestationById(int id);

	public void updateAttestation(Attestation att);

	public Attestation addAttestation(Attestation att);

	public int getAttestations(long userid, int typeid);

	// v1.8.0.4 TGPROJECT-58 lx start
	public void deleteAttestation(long userid, int attestationId);
	// v1.8.0.4 TGPROJECT-58 lx start
}
