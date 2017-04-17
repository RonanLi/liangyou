package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowIncomeLadder;

public interface BorrowIncomeLadderDao extends BaseDao<BorrowIncomeLadder> {

	public List<BorrowIncomeLadder> getList(Borrow borrow);

	
	
}
