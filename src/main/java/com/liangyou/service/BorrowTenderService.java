package com.liangyou.service;

import com.liangyou.domain.BorrowTender;

public interface BorrowTenderService {

	BorrowTender getListByUserId(long invitatedId);

	void updateBorrowTender(BorrowTender borrowTender);

}
