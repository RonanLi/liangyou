package com.liangyou.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.liangyou.dao.BorrowIncomeLadderDao;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowIncomeLadder;

@Repository
public class BorrowIncomeLadderDaoImpl extends ObjectDaoImpl<BorrowIncomeLadder> implements BorrowIncomeLadderDao {

	@Override
	public List<BorrowIncomeLadder> getList(Borrow borrow) {
		String jpql=" from BorrowIncomeLadder where borrow=?1";
		List<BorrowIncomeLadder> list = new ArrayList<BorrowIncomeLadder>();
		Query q = em.createQuery(jpql).setParameter(1, borrow);
		list = q.getResultList();
		return list;
	}

}
