package com.liangyou.dao;

import com.liangyou.domain.Attestation;

public interface AttestationDao extends BaseDao<Attestation>{
	
	public int getAttestations(long userid,int typeid);
	
	// v1.0.8.4 TGPROJECT-55 lx 2014-04-14 start
	public Attestation getAttestationByUserIdAndId(long userid,int attestationId);
	// v1.0.8.4 TGPROJECT-55 lx 2014-04-14 end
	
}
