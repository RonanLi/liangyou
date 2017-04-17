package com.liangyou.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.liangyou.domain.AccountRechargeParam;
@Repository
public interface AccountRechargeParamDao extends JpaRepository<AccountRechargeParam, Long>{

	AccountRechargeParam findByRechargeId(int id);

}
