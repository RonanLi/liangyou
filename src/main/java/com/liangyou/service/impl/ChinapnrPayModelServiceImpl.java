package com.liangyou.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.dao.ChinapnrPayDao;
import com.liangyou.domain.ChinaPnrPayModel;
import com.liangyou.service.ChinapnrPayModelService;

@Service(value="chinapnrPayModelService")
@Transactional
public class ChinapnrPayModelServiceImpl extends BaseServiceImpl implements
		ChinapnrPayModelService {
	private Logger logger=Logger.getLogger(ChinapnrPayModelServiceImpl.class);
	@Autowired
	private ChinapnrPayDao chinapnrPayDao;
	@Override
	public void dealChinapnrBack(String ordNo, String respcode, String respdesc) {
		ChinaPnrPayModel chinaPnrPayModel  = chinapnrPayDao.findChinapnrModelByOrd(ordNo);
		if (respcode.equals("000")) {  //判断是否处理成功
			if (chinaPnrPayModel !=null &&chinaPnrPayModel.getStatus()!=null ) {
				if (chinaPnrPayModel.getStatus().equals("2")) {
					chinaPnrPayModel.setStatus("1");
					chinaPnrPayModel.setErrorMsg("SUCCESS");
				}else{//状态不未2 的不做处理
					logger.info("此订单已经处理成功，状态已经修改！ordNo"+ordNo);
				}
			}
		}
		
	}

}
