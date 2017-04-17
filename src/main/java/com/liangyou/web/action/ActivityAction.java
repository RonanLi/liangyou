package com.liangyou.web.action;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import com.liangyou.domain.ScrollPic;
import com.liangyou.service.ScrollPicService;
import com.liangyou.util.NumberUtils;

/**
 * Created by Young on 2016/12/30.
 */

@SuppressWarnings("all")
@Scope("prototype")
public class ActivityAction extends BaseAction {
	
	private static Logger logger = Logger.getLogger(ActivityAction.class);
	
	@Autowired
	private ScrollPicService scrollPicService;
    public String requestMethod() {
        return SUCCESS;
    }


    public String gift4_1() {
        request.setAttribute("invitateCode", paramString("invitateCode"));
        return SUCCESS;
    }
    
    //add by lxm 非文章类（流动儿童）首页banner点击次数统计 2017-3-27 17:19:13
	public String charitable() {
		String path = request.getServletPath();
		long typeId = NumberUtils.getLong(request.getParameter("typeId"));
		scrollPicService.updatePicHits(path);
		ScrollPic sp = scrollPicService.showScrollPic(path,(long)1);
		if(sp==null){
			message("没有此文章！","/");
			return MSG;
		}
		request.setAttribute("sp", sp);
		logger.info("该活动页ID"+sp.getId()+"点击量：" + sp.getHits());
		return SUCCESS;
	}
}
