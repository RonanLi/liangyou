package com.liangyou.web.action.member;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.context.Global;
import com.liangyou.dao.BorrowCollectionDao;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowCollection;
import com.liangyou.domain.ExperienceMoney;
import com.liangyou.domain.User;
import com.liangyou.model.SearchParam;
import com.liangyou.service.BorrowService;
import com.liangyou.service.ExperienceMoneyService;
import com.liangyou.util.DateUtils;
import com.liangyou.web.action.BaseAction;

/**
 * Created by Young on 2016/10/13.
 */

@Namespace("/experienceMoney")
@ParentPackage("p2p-default")
public class ExperienceMoneyAction extends BaseAction {
	private static Logger logger = Logger.getLogger(ExperienceMoneyAction.class);

	@Autowired
	private ExperienceMoneyService experienceMoneyService;
	@Autowired
	private BorrowService borrowService;
	@Autowired
	private BorrowCollectionDao borrowCollectionDao;

	/**
	 * 加载体验金活动页面
	 *
	 * @return
	 */
	@Action(value = "experienceMoneyActive", results = { @Result(name = "success", type = "ftl", location = "/experienceMoney/experienceMoneyActive.html") })
	public String experienceMoneyActive() {
		request.setAttribute("experienceMoneyBID", Global.getValue("experienceMoney_borrow_id"));
		return SUCCESS;
	}

	/**
	 * 我的体验金
	 * 
	 * @return
	 */
	@Action(value = "myExperienceMoney", results = { @Result(name = "success", type = "ftl", location = "/experienceMoney/myExperienceMoney.html"), @Result(name = "login", type = "redirect", location = "/user/login.html") })
	public String myExperienceMoney() {
		User user = this.getSessionUser();
		if (user == null) {
			return "login";
		}

		ExperienceMoney em = this.experienceMoneyService.getExperenceMoney(SearchParam.getInstance().addParam("user", user));
		if (em != null) {
			request.setAttribute("em", em);

			if (em.getUseStatus() == 0 && em.getAddTime() != null) {
				request.setAttribute("experienceMoneyDeadline", DateUtils.dateStr4(DateUtils.rollDay(em.getAddTime(), em.getTimeLimit())));
			}

			if (em.getInterestIncomeStatus() == 1 && em.getInterestUseStatus() == 0 && em.getInterestIncomeTime() != null) {
				request.setAttribute("interestDeadline", DateUtils.dateStr4(DateUtils.rollDay(em.getInterestIncomeTime(), 15)));
			}
		}

		List<BorrowCollection> bcList = borrowCollectionDao.getExperienceCollection(user.getUserId(), Long.parseLong(Global.getValue("experienceMoney_borrow_id")));
		if (!bcList.isEmpty()) {
			BorrowCollection bc = bcList.get(0);
			if (bc.getStatus() != 2)
				request.setAttribute("bc", bc);
		}

		Borrow borrow = borrowService.getBorrow(Long.parseLong(Global.getValue("experienceMoney_borrow_id")));
		request.setAttribute("borrow", borrow);
		return SUCCESS;
	}
}
