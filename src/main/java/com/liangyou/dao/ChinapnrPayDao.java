package com.liangyou.dao;

import com.liangyou.domain.ChinaPnrPayModel;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;

public interface ChinapnrPayDao extends BaseDao<ChinaPnrPayModel> {
	public PageDataList<ChinaPnrPayModel> getChinapnrList(SearchParam sp);
	public ChinaPnrPayModel findChinapnrModelByOrd(String ordNo);
}
