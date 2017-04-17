package com.liangyou.model.borrow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.dao.RuleDao;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowCollection;
import com.liangyou.domain.BorrowConfig;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.Rule;
import com.liangyou.domain.User;
import com.liangyou.exception.BussinessException;
import com.liangyou.service.RuleService;
import com.liangyou.tool.interest.EndInterestCalculator;
import com.liangyou.tool.interest.InterestCalculator;
import com.liangyou.tool.interest.MonthEqualCalculator;
import com.liangyou.tool.interest.MonthInterest;
import com.liangyou.tool.interest.MonthInterestCalculator;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;

public class BaseBorrowModel extends BorrowModel {

	private static final long serialVersionUID = 5486891425298146179L;

	private Borrow model;
	private BorrowConfig config;
	Logger logger = Logger.getLogger(BaseBorrowModel.class);

	public BaseBorrowModel(Borrow model) {
		this.model = model;
		init();
	}

	public void init() {
		this.config = Global.getBorrowConfig(model.getType());
	}

	public boolean isTrial() {
		BorrowConfig config = getConfig();
		if (config.getIsTrail() == 0) {
			return false;
		} else {
			return true;
		}
	}

	public boolean isReview() {
		BorrowConfig config = getConfig();
		if (config == null || config.getIsReview() == 0) {
			return false;
		} else {
			return true;
		}
	}

	public BorrowConfig getConfig() {
		return config;
	}

	public void setConfig(BorrowConfig config) {
		this.config = config;
	}

	public Borrow getModel() {
		return model;
	}

	public void setModel(Borrow model) {
		this.model = model;
	}

	/**
	 * 根据系统参数中的配置决定是否跳过初审
	 */
	@Override
	public void skipTrial() {
		if (this.isTrial()) {
			model.setStatus(1);
			model.setVerifyTime(new Date());
		}
	}

	/**
	 * 根据系统参数中的配置决定是否跳过复审
	 */
	@Override
	public void skipReview() {
		if (this.isReview()) {
			model.setStatus(3);
		}
	}

	@Override
	public void skipStatus() {
		model.setStatus(model.getStatus() + 1);
	}

	@Override
	public void verify(int status, int verifyStatus) {
		if (verifyStatus == 2) {
			if (status == 0)
				model.setStatus(2);
		} else if (verifyStatus == 1) {
			if (status == 0) {
				model.setStatus(1);
			}
		} else if (verifyStatus == 3) {
			if (status == 1)
				model.setStatus(3);
		} else if (verifyStatus == 4) {
			if (status == 1)
				model.setStatus(4);
		} else if (verifyStatus == 5) {
			model.setStatus(5);
		}
	}

	/**
	 * 计算借款标的利息
	 *
	 * @return
	 */
	@Override
	public double calculateInterest() {
		InterestCalculator ic = interestCalculator();
		double interest = ic.getMoneyPerMonth() * ic.getPeriod() - getModel().getAccount();
		if (interest < 0) {
			return 0;
		}
		return interest;
	}

	/*
	 * @Override public double calculateInterest(double validAccount) { InterestCalculator ic= interestCalculator(validAccount); double interest=ic.getMoneyPerMonth()*ic.getPeriod()-validAccount; return interest; }
	 */
	// ZTCAIFU-15 wsl 流转标还款方式处理 start
	@Override
	public double calculateInterest(double validAccount) {
		InterestCalculator ic = interestCalculator(validAccount);
		double interest = 0;
		if ("3".equals(model.getStyle())) {
			interest = ic.getMoneyPerMonth() * ic.getPeriod();
		} else {
			interest = ic.getMoneyPerMonth() * ic.getPeriod() - validAccount;
		}
		if (interest < 0) {
			return 0;
		}
		return interest;
	}

	@Override
	public double calculateInterest(double validAccount, BorrowTender tender) {
		InterestCalculator ic = interestCalculator(validAccount, tender);
		double interest = 0;
		if ("3".equals(model.getStyle())) {
			interest = ic.getMoneyPerMonth() * ic.getPeriod();
		} else {
			interest = ic.getMoneyPerMonth() * ic.getPeriod() - validAccount;
		}
		return interest;
	}

	// ZTCAIFU-15 wsl 流转标还款方式处理 end
	@Override
	public InterestCalculator interestCalculator() {
		Borrow model = getModel();
		double account = model.getAccount();
		InterestCalculator ic = interestCalculator(account);
		return ic;
	}

	@Override
	public InterestCalculator interestCalculator(double validAccount) {
		Borrow model = getModel();
		InterestCalculator ic = null;
		double apr = model.getApr() / 100;
		if (model.getIsday() == 1) {// 天标
			int time_limit_day = model.getTimeLimitDay();
			ic = new EndInterestCalculator(validAccount, apr, time_limit_day);
		} else if (StringUtils.isNull(model.getStyle()).equals("2")) {// 一次性还款
			int time_limit = model.getTimeLimit();
			ic = new EndInterestCalculator(validAccount, apr, time_limit, InterestCalculator.TYPE_MONTH_END);
		} else if (StringUtils.isNull(model.getStyle()).equals("3")) {// 每月还息，到期还本
			int time_limit = model.getTimeLimit();
			ic = new MonthInterestCalculator(validAccount, apr, time_limit);
		} else if (model.getType() == 107 && (model.getStyle().equals("4") || model.getStyle().equals("5"))) {
			// 公益标 两种方式，一个是不还款，一个一次性还款，这里都用一次性还款模式
			int time_limit = model.getTimeLimit();
			ic = new EndInterestCalculator(validAccount, apr, time_limit, InterestCalculator.TYPE_MONTH_END);
		} else {// 按月份还款
			int time_limit = model.getTimeLimit();
			ic = new MonthEqualCalculator(validAccount, apr, time_limit);
		}
		ic.each();
		return ic;
	}

	@Override
	public InterestCalculator interestCalculator(double validAccount, BorrowTender tender) {
		Borrow model = getModel();
		InterestCalculator ic = null;
		double apr = tender.getApr() / 100;
		if (model.getIsday() == 1) {// 天标
			int time_limit_day = model.getTimeLimitDay();
			ic = new EndInterestCalculator(validAccount, apr, time_limit_day);
		} else if (StringUtils.isNull(model.getStyle()).equals("2")) {// 一次性还款
			int time_limit = model.getTimeLimit();
			ic = new EndInterestCalculator(validAccount, apr, time_limit, InterestCalculator.TYPE_MONTH_END);
		} else if (StringUtils.isNull(model.getStyle()).equals("3")) {// 每月还息，到期还本
			int time_limit = model.getTimeLimit();
			ic = new MonthInterestCalculator(validAccount, apr, time_limit);
		} else if (model.getType() == 107 && (model.getStyle().equals("4") || model.getStyle().equals("5"))) {
			// 公益标 两种方式，一个是不还款，一个一次性还款，这里都用一次性还款模式
			int time_limit = model.getTimeLimit();
			ic = new EndInterestCalculator(validAccount, apr, time_limit, InterestCalculator.TYPE_MONTH_END);
		} else {// 按月份还款
			int time_limit = model.getTimeLimit();
			ic = new MonthEqualCalculator(validAccount, apr, time_limit);
		}
		ic.each();
		return ic;
	}

	@Override
	public InterestCalculator interestCalculator(double validAccount, double remainRepayment) {
		Borrow model = getModel();
		InterestCalculator ic = null;

		double apr = model.getApr() / 100;

		if (model.getType() == Constant.TYPE_EXPERIENCE) { // add by gy
															// 2016-10-19
															// 14:29:24 体验标 计算利息
			ic = new EndInterestCalculator(validAccount, apr, 1, remainRepayment);
		} else if (model.getIsday() == 1) {// 天标
			int time_limit_day = model.getTimeLimitDay();
			ic = new EndInterestCalculator(validAccount, apr, time_limit_day, remainRepayment);
		} else if (StringUtils.isNull(model.getStyle()).equals("2")) {// 一次性还款
			int time_limit = model.getTimeLimit();
			ic = new EndInterestCalculator(validAccount, apr, time_limit, InterestCalculator.TYPE_MONTH_END, remainRepayment);
		} else if (StringUtils.isNull(model.getStyle()).equals("3")) {// 每月还息，到期还本
			int time_limit = model.getTimeLimit();
			ic = new MonthInterestCalculator(validAccount, apr, time_limit, remainRepayment);
		} else if (model.getType() == 107 && model.getStyle().equals("4")) {
			// 公益标 不还款
			int time_limit = model.getTimeLimit();
			ic = new EndInterestCalculator(validAccount, apr, time_limit, InterestCalculator.TYPE_MONTH_END, remainRepayment);
		} else {// 按月份还款
			int time_limit = model.getTimeLimit();
			ic = new MonthEqualCalculator(validAccount, apr, time_limit, remainRepayment);
		}
		ic.each();
		return ic;
	}

	@Override
	public InterestCalculator interestCalculator(double validAccount, double remainRepayment, BorrowTender t) {
		Borrow model = getModel();
		InterestCalculator ic = null;

		double apr = t.getApr() / 100;

		if (model.getIsday() == 1) {// 天标
			int time_limit_day = model.getTimeLimitDay();
			ic = new EndInterestCalculator(validAccount, apr, time_limit_day, remainRepayment);
		} else if (StringUtils.isNull(model.getStyle()).equals("2")) {// 一次性还款
			int time_limit = model.getTimeLimit();
			ic = new EndInterestCalculator(validAccount, apr, time_limit, InterestCalculator.TYPE_MONTH_END, remainRepayment);
		} else if (StringUtils.isNull(model.getStyle()).equals("3")) {// 每月还息，到期还本
			int time_limit = model.getTimeLimit();
			ic = new MonthInterestCalculator(validAccount, apr, time_limit, remainRepayment);
		} else if (model.getType() == 107 && model.getStyle().equals("4")) {
			// 公益标 不还款
			int time_limit = model.getTimeLimit();
			ic = new EndInterestCalculator(validAccount, apr, time_limit, InterestCalculator.TYPE_MONTH_END, remainRepayment);
		} else {// 按月份还款
			int time_limit = model.getTimeLimit();
			ic = new MonthEqualCalculator(validAccount, apr, time_limit, remainRepayment);
		}
		ic.each();
		return ic;
	}

	/**
	 * 计算借款标的手续费
	 *
	 * @return
	 */
	@Override
	public double calculateBorrowFee() {
		return 0;
	}

	/**
	 * 计算奖励资金
	 */
	@Override
	public double calculateBorrowAward(BorrowTender bt) {
		double account = model.getAccount();
		if (model.getAward() == 1) { // 按投标金额比例
			return (model.getPartAccount() / 100) * (bt.getAccount()); // 投资金额
		} else if (model.getAward() == 2) {// 按投资金额分奖励
			return (bt.getAccount() / account) * model.getFunds();
		} else {
			return 0.0;
		}
	}

	/**
	 * 发布秒标，计算就冻结金额
	 *
	 * @return
	 */
	@Override
	public double calculateBorrowSecondFreeze() {
		double interest = NumberUtils.format2(model.getRepaymentAccount() - model.getAccount());
		double fee = NumberUtils.format2(getManageFeeWithRule(model.getTimeLimit()));
		double award = NumberUtils.format2(calculateBorrowAward());
		if (award > 0) {
			award += 1;
		} // 防止误差，造成错误。
		return NumberUtils.format2(interest + fee + award);
	}

	/**
	 * 计算奖励资金
	 */
	public double calculateBorrowAward() {
		if (model.getAward() == 1) {
			double account = model.getAccount();
			return model.getPartAccount() / 100 * account;
		} else if (model.getAward() == 2) {
			return model.getFunds();
		} else {
			return 0.0;
		}
	}

	private boolean toBool(String identify) {
		if (getConfig() == null || getConfig().getIdentify() == null) {
			throw new BussinessException("该类借款标的配置参数不对！");
		}
		String cfgIdentify = getConfig().getIdentify();
		int i1 = Integer.parseInt(identify, 2);
		int i2 = Integer.parseInt(cfgIdentify, 2);
		int ret = i1 & i2;
		if (ret > 0)
			return true;
		return false;
	}

	public boolean isNeedRealName() {
		String is = "100000";
		return toBool(is);
	}

	public boolean isNeedVIP() {
		String is = "010000";
		return toBool(is);
	}

	public boolean isNeedEmail() {
		String is = "001000";
		return toBool(is);
	}

	public boolean isNeedPhone() {
		String is = "000100";
		return toBool(is);
	}

	public boolean isNeedVideo() {
		String is = "000010";
		return toBool(is);
	}

	public boolean isNeedScene() {
		String is = "000001";
		return toBool(is);
	}

	@Override
	public boolean checkIdentify(User u) {
		if (isNeedRealName()) {
			if (u.getRealStatus() != 1) {
				throw new BussinessException("需要实名认证！");
			}
		}
		if (isNeedVIP()) {
			if (u.getUserCache().getVipStatus() != 1) {
				throw new BussinessException("需要VIP认证！");
			}
		}
		if (isNeedEmail()) {
			if (u.getEmailStatus() != 1) {
				throw new BussinessException("需要邮箱认证！");
			}
		}
		if (isNeedPhone()) {
			if (u.getPhoneStatus() != 1) {
				throw new BussinessException("需要手机认证！");
			}
		}
		if (isNeedVideo()) {
			if (u.getVideoStatus() != 1) {
				throw new BussinessException("需要视频认证！");
			}
		}
		if (isNeedScene()) {
			if (u.getSceneStatus() != 1) {
				throw new BussinessException("需要现场认证！");
			}
		}
		return true;
	}

	// v1.8.0.4 TGPROJECT-110 lx 2014-04-18 start
	@Override
	public boolean checkModelData() {
		Borrow model = getModel();
		if (model.getAccount() <= 0) {
			throw new BussinessException("借贷总金额必须为整数");
		}

		if (StringUtils.isEmpty(model.getName())) {
			throw new BussinessException("标题不能为空");
		}

		String minErrorMsg = "最低利率不能低于1%";
		String sixMonthErrorMsg = "六个月份以内(含六个月)的贷款年利率不能高于22.4%";
		String oneYearErrorMsg = "六个月至一年(含一年)的贷款年利率不能高于24%";
		String threeYearErrorMsg = "一年至三年(含三年)的贷款年利率不能高于24.6%";
		String fiveYearErrorMsg = "三年至五年(含五年)的贷款年利率不能高于25.6%";
		String moreYearErrorMsg = "五年以上的贷款年利率不能高于26.2%";

		if (model.getApr() < 1 && model.getType() != 107) {// 公益标不算利息。
			throw new BussinessException(minErrorMsg);
		}

		// 秒标或者天标按照借款期限一个月来算，1-6月最高利率是5.6%*4
		if (model.getType() == Constant.TYPE_SECOND || model.getIsday() == 1) {
			if (model.getApr() > 22.4) {
				throw new BussinessException(sixMonthErrorMsg);
			}
		} else {
			// 发标的利率是否限制，当规则的status为 1 时候，校验
			WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
			RuleDao ruleDao = (RuleDao) wac.getBean("ruleDao");
			Rule rule = ruleDao.getRuleByNid("add_borrow_apr");
			if (rule != null) {
				if (rule.getStatus() == 1) {
					int time_limit = model.getTimeLimit();
					double apr = model.getApr();
					if (time_limit <= 6 && apr > 22.4) {
						throw new BussinessException(sixMonthErrorMsg);
					}
					if (time_limit <= 12 && time_limit > 6 && apr > 24) {
						throw new BussinessException(oneYearErrorMsg);
					}
					if (time_limit <= 36 && time_limit > 12 && apr > 24.6) {
						throw new BussinessException(threeYearErrorMsg);
					}
					if (time_limit <= 60 && time_limit > 36 && apr > 25.6) {
						throw new BussinessException(fiveYearErrorMsg);
					}
					if (time_limit > 60 && apr > 26.2) {
						throw new BussinessException(moreYearErrorMsg);
					}
				}
			}
		}
		BorrowConfig config = Global.getBorrowConfig(model.getType());
		if (config != null) {
			double lowest_account = 0;
			if (config.getLowestAccount() != 0) {
				lowest_account = config.getLowestAccount();
			} else {
				lowest_account = 500;
			}
			double most_account = 0;
			if (config.getMostAccount() != 0) {
				most_account = config.getMostAccount();
			} else {
				most_account = 5000000;
			}
			double account = model.getAccount();
			if (account < lowest_account && model.getIsAssignment() != 1) {
				throw new BussinessException("借款金额不能低于" + lowest_account + "元");
			}
			if (account > most_account && model.getIsAssignment() != 1) {
				throw new BussinessException("借款金额不能高于" + most_account + "元", "/borrow/add.html");
			}
		}
		return true;
	}

	// v1.8.0.4 TGPROJECT-110 lx 2014-04-18 end
	@Override
	public List<BorrowRepayment> getRepayment() {
		InterestCalculator ic = interestCalculator();
		List<MonthInterest> monthList = ic.getMonthList();
		List<BorrowRepayment> repaysList = new ArrayList<BorrowRepayment>(ic.getPeriod());
		int i = 0;
		for (MonthInterest mi : monthList) {
			BorrowRepayment repay = new BorrowRepayment();
			repay.setBorrow(model);
			repay.setPeriod(i++);
			repay.setRepaymentTime(getRepayTime(repay.getPeriod()));

			double repayment_account = (mi.getAccountPerMon() + mi.getInterest());
			repay.setRepaymentAccount(repayment_account);
			double repaymeng_interest = mi.getInterest();
			repay.setInterest(repaymeng_interest);
			double repaymeng_accountPerMon = mi.getAccountPerMon();
			repay.setCapital(repaymeng_accountPerMon);
			repay.setAddtime(new Date());
			repaysList.add(repay);
		}
		return repaysList;
	}

	@Override
	public List<BorrowRepayment> getRepayment(List<BorrowCollection> list) {
		List<BorrowRepayment> repaysList = new ArrayList<BorrowRepayment>();
		if ("2".equals(model.getStyle())) {// 一次性还款
			repaysList.add(getNowRepayment(list, 0));
		} else {
			for (int i = 0; i < model.getTimeLimit(); i++) {
				repaysList.add(getNowRepayment(list, i));
			}
		}
		return repaysList;
	}

	private BorrowRepayment getNowRepayment(List<BorrowCollection> list, int nowPeriod) {
		BorrowRepayment repay = new BorrowRepayment();
		Date repayTime = getRepayTime(nowPeriod);
		repay.setBorrow(model);
		repay.setPeriod(nowPeriod);
		repay.setRepaymentTime(repayTime);
		double repayment_account = 0d;
		double repaymeng_interest = 0d;
		double captial = 0d;
		for (BorrowCollection bc : list) {
			if (bc.getPeriod() == repay.getPeriod()) {
				repayment_account += bc.getRepayAccount();
				repaymeng_interest += bc.getInterest();
				captial += bc.getCapital();
				bc.setRepayTime(repayTime);
			}
		}
		repay.setRepaymentAccount(repayment_account);
		repay.setInterest(repaymeng_interest);
		repay.setCapital(captial);
		repay.setAddtime(new Date());
		return repay;
	}

	@Override
	public List<BorrowRepayment> getFlowRepayment(BorrowTender bt, Borrow model) {// 生成流标的还款计划
		if (model.getStyle().equals("2") || model.getStyle().equals("3")) {
			logger.info("流转标只支持一次性还款和每月还息到期还本的还款方式");
		} else {
			throw new BussinessException("流转标只支持一次性还款和每月还息到期还本的还款方式,请您确定");
		}
		// 阶梯收益
		InterestCalculator ic;
		if (model.getIsIncomeLadder() == 1) {
			ic = interestCalculator(bt.getAccount(), bt);
		} else {
			ic = interestCalculator(bt.getAccount());
		}
		List<MonthInterest> monthList = ic.getMonthList();
		List<BorrowRepayment> repaysList = new ArrayList<BorrowRepayment>(ic.getPeriod());
		int i = 0;
		for (MonthInterest mi : monthList) {
			BorrowRepayment repay = new BorrowRepayment();
			repay.setBorrow(model);
			repay.setPeriod(i++);
			Date repayTime = null;
			if (model.getIsday() == 1) {
				repayTime = DateUtils.rollDay(bt.getAddtime(), model.getTimeLimitDay());
			} else {
				repayTime = DateUtils.rollMon(bt.getAddtime(), model.getTimeLimit());
			}
			repay.setRepaymentTime(repayTime);

			double repayment_account = (mi.getAccountPerMon() + mi.getInterest());
			repay.setRepaymentAccount(repayment_account);
			double repaymeng_interest = mi.getInterest();
			repay.setInterest(repaymeng_interest);
			double repaymeng_accountPerMon = mi.getAccountPerMon();
			repay.setCapital(repaymeng_accountPerMon);
			repay.setAddtime(new Date());
			repay.setBorrowTender(bt);
			repaysList.add(repay);
		}
		return repaysList;
	}

	@Override
	public Date getRepayTime(int period) {
		Date d = model.getVerifyTime();
		Date repayDate = DateUtils.getLastSecIntegralTime(d);
		// edit by gy 2016-10-21 16:39:42 增加体验标还款日为一天的条件
		if (model.getType() == Constant.TYPE_EXPERIENCE) {
			return DateUtils.rollDay(DateUtils.getLastSecIntegralTime(new Date()), 1);
		} else if (model.getType() == Constant.TYPE_SECOND) {
			// v1.8.0.4_u3 TGPROJECT-336 qinjun 2014-06-20 start
			return DateUtils.rollDay(d, 1);
			// v1.8.0.4_u3 TGPROJECT-336 qinjun 2014-06-20 end
		} else if (model.getIsday() == 1) {
			repayDate = DateUtils.rollDay(repayDate, model.getTimeLimitDay());
			return repayDate;
		} else {
			// 一次性还款
			if (StringUtils.isNull(model.getStyle()).equals("2")) {
				repayDate = DateUtils.rollMon(repayDate, model.getTimeLimit());
			} else {
				repayDate = DateUtils.rollMon(repayDate, period + 1);
			}
			return repayDate;
		}
	}

	@Override
	public boolean isLastPeriod(int order) {
		// ZTCAIFU-15 wsl 流转标还款方式处理 start
		/*
		 * if(model.getType()==110||model.getIsday()==1||StringUtils.isNull( model.getStyle()).equals("2")){ return true;
		 */
		if (model.getIsday() == 1 || StringUtils.isNull(model.getStyle()).equals("2")) {
			return true;
		} else {
			int time_limit = model.getTimeLimit();
			return (time_limit == (order + 1));
		}
		// ZTCAIFU-15 wsl 流转标还款方式处理 end
	}

	@Override
	public List<BorrowCollection> getCollectionList(BorrowTender t) {
		double validAccount = t.getAccount();
		// 按照等额还息算法得出每期的还款金额以及利息
		InterestCalculator ic = interestCalculator(validAccount);
		List<MonthInterest> monthList = ic.getMonthList();
		// 拼装Collection对象 批量插入还款表
		List<BorrowCollection> collectList = new ArrayList<BorrowCollection>();
		int i = 0;
		for (MonthInterest mi : monthList) {
			BorrowCollection c = fillCollection(mi, t, ic);
			c.setPeriod(i++);
			// ZTCAIFU-15 wsl 流转标还款方式处理 start
			/*
			 * if (model.getType()==Constant.TYPE_FLOW) { Date repayTime =null ; if(model.getIsday()==1){ repayTime=DateUtils.rollDay(t.getAddtime(),model.getTimeLimitDay( )); }else{ repayTime=DateUtils.rollMon(t.getAddtime(),model.getTimeLimit()); } c.setRepayTime(repayTime); }else{ c.setRepayTime(getRepayTime(c.getPeriod())); }
			 */
			c.setRepayTime(getRepayTime(c.getPeriod()));
			// ZTCAIFU-15 wsl 流转标还款方式处理 end
			collectList.add(c);
		}

		double repayment_account = ic.getTotalAccount();
		double repayment_interest = NumberUtils.format2(repayment_account) - NumberUtils.format2(ic.getAccount());
		t.setRepaymentAccount(repayment_account);
		t.setInterest(repayment_interest);
		// ZTCAIFU-15 wsl 流转标还款方式处理 start
		/*
		 * if (model.getType()== Constant.TYPE_FLOW){ t.setWaitAccount(ic.getAccount()); t.setWaitInterest(repayment_interest); }
		 */
		if (model.getType() == Constant.TYPE_FLOW) {
			t.setWaitAccount(ic.getTotalAccount());
			double repayment_yesinterest = NumberUtils.format2(t.getRepaymentYesinterest());
			t.setWaitInterest(repayment_interest - repayment_yesinterest);
		}
		// ZTCAIFU-15 wsl 流转标还款方式处理 end
		t.setAccount(ic.getAccount());
		return collectList;
	}

	/**
	 * 获取债权转让 的tender 和 collection
	 *
	 * @param t
	 *            当前投标
	 * @param tenderList
	 *            当前投标的所有记录
	 * @param assignMentTender
	 *            转让的投标
	 * @param assignCollection
	 *            转让的投标对应的待收
	 * @param hasCapital
	 * @param hasInterest
	 * @return
	 */
	@Override
	public List<BorrowCollection> getAssignMentCollectionList(BorrowTender t, List<BorrowTender> tenderList, BorrowTender assignMentTender, List<BorrowCollection> assignCollection) {
		// 获取债权转让的tender 和 collection
		double assignMentAccount = assignMentTender.getWaitAccount(); // 剩余的待收本金全部转让
		double assignMentInterest = assignMentTender.getWaitInterest();// 剩余的待收利息全部转让
		double validAccount = t.getAccount(); // 有效投标金额
		double account = model.getAccount(); // 转让标的本金
		double accountRate = validAccount / account; // 投资百分比 用来计算 投资应得的利息和每期的待收

		// 最后一期的投标利息应该用总的 减去 前边之和；防止利息前后的差别
		double interest = 0d;
		boolean isLastPeriod = validAccount + model.getAccountYes() >= model.getAccount();
		if (isLastPeriod) {// 最后一期
			double hasInterest = 0d;
			for (BorrowTender bt : tenderList) {
				hasInterest += bt.getInterest();
			}
			interest = assignMentInterest - hasInterest;
		} else {
			interest = NumberUtils.format2(assignMentInterest * accountRate); // 投资应得的利息
		}
		t.setInterest(interest);
		// 投资债权转让标的tender应该由：投标金额/发布债权转让标的金额*原始债权人的待收本金
		double repayAccount = accountRate * assignMentAccount;
		t.setRepaymentAccount(interest + repayAccount);
		t.setBorrow(model);
		return fillAssignMentCollection(assignCollection, accountRate, t, isLastPeriod, tenderList);
	}

	/**
	 * 债权转让时按照比例分 投资的 转让金额和利息
	 *
	 * @param assignCollection
	 * @param assignMentTender
	 * @param assignMentBorrow
	 * @param borrow
	 * @param accountRate
	 *            比例
	 * @return
	 */
	private List<BorrowCollection> fillAssignMentCollection(List<BorrowCollection> assignCollection, double accountRate, BorrowTender borrowTender, boolean isLastPeriods, List<BorrowTender> borrowTenderList) {
		List<BorrowCollection> borrowCollectionList = new ArrayList<BorrowCollection>();
		int i = 0;
		double allCollectionInterest = 0d;
		double allCollectionCaptial = 0d;
		for (BorrowCollection bc : assignCollection) {// 转让的collectoin,所有的投资人分
														// 本金和利息按照比例，用来生成新的待收本金和利息
			i++;
			double assignMentCapital = bc.getCapital();// 转让标当期待收本金
			double assignMentInterest = bc.getInterest();// 转让标当期待收利息

			double capital = 0d;
			double interest = 0d;
			if (isLastPeriods) {// 最后一期投资特别处理
				double hasCapital = 0d;
				double hasInterest = 0d;
				for (BorrowTender bt : borrowTenderList) {
					BorrowCollection currBc = bt.getBorrowCollections().get(i - 1); // 当前期所有待收和
					hasCapital += currBc.getCapital();
					hasInterest += currBc.getInterest();
				}
				capital = assignMentCapital - hasCapital;
				interest = assignMentInterest - hasInterest;
			} else {
				capital = NumberUtils.format2(assignMentCapital * accountRate);
				interest = NumberUtils.format2(assignMentInterest * accountRate);
			}

			if (i == assignCollection.size()) {// 最后一期的collection计算,投标待收金额应由：投标比例*债权转让人档期待收本金
				capital = borrowTender.getAccount() - allCollectionCaptial;
				// capital = accountRate*assignMentCapital -
				// allCollectionCaptial;
				interest = borrowTender.getInterest() - allCollectionInterest;
			} else {
				allCollectionCaptial += capital;
				allCollectionInterest += interest;
			}
			BorrowCollection newBc = new BorrowCollection();
			newBc.setPeriod(bc.getPeriod());
			newBc.setCapital(capital);
			newBc.setInterest(interest);
			newBc.setRepayAccount(capital + interest);
			newBc.setAddtime(new Date());
			newBc.setBorrowTender(borrowTender);
			newBc.setAddip(borrowTender.getAddip());
			newBc.setRepayTime(bc.getRepayTime());
			borrowCollectionList.add(newBc);
		}
		return borrowCollectionList;
	}

	@Override
	public List<BorrowCollection> getCollectionList(BorrowTender t, List<BorrowTender> list) {
		double validAccount = t.getAccount();
		double accountYes = model.getAccountYes();
		double hasTenderRepayment = 0d;
		double remainRepayment = 0d;

		// 阶梯收益
		if (model.getType() != Constant.TYPE_EXPERIENCE) { // 体验标不用这个。。
															// 否则最后一次投资利息就计算错误了。。。。
			if (model.getIsIncomeLadder() != 1) {
				if (validAccount + accountYes >= model.getAccount()) {// 最后一次投资
					for (BorrowTender borrowTender : list) {
						hasTenderRepayment += borrowTender.getRepaymentAccount();
					}
					remainRepayment = model.getRepaymentAccount() - hasTenderRepayment;
				}
			}
		}

		// 按照等额还息算法得出每期的还款金额以及利息

		// 阶梯收益sj
		InterestCalculator ic;
		if (model.getIsIncomeLadder() == 1) {
			ic = interestCalculator(validAccount, remainRepayment, t);
		} else {
			ic = interestCalculator(validAccount, remainRepayment);
		}

		List<MonthInterest> monthList = ic.getMonthList();
		// 拼装Collection对象 批量插入还款表
		List<BorrowCollection> collectList = new ArrayList<BorrowCollection>();
		int i = 0;
		for (MonthInterest mi : monthList) {
			BorrowCollection c = fillCollection(mi, t, ic);
			c.setPeriod(i++);
			c.setRepayTime(getRepayTime(c.getPeriod()));
			collectList.add(c);
		}
		double repayment_account = ic.getTotalAccount();
		double repayment_interest = NumberUtils.format2(repayment_account) - NumberUtils.format2(ic.getAccount());
		t.setInterest(repayment_interest);
		t.setRepaymentAccount(repayment_account);

		// add by gy 2016-10-19 14:25:18 体验标 borrowtender中，预还金额只有利息
		if (model.getType() == Constant.TYPE_EXPERIENCE) {
			t.setRepaymentAccount(repayment_interest);
			t.setWaitAccount(repayment_interest);
			t.setWaitInterest(repayment_interest);
		}

		if (model.getType() == Constant.TYPE_FLOW) {
			t.setWaitAccount(ic.getTotalAccount());
			double repayment_yesinterest = NumberUtils.format2(t.getRepaymentYesinterest());
			t.setWaitInterest(repayment_interest - repayment_yesinterest);
		}
		t.setAccount(ic.getAccount());
		return collectList;
	}

	private BorrowCollection fillCollection(MonthInterest mi, BorrowTender t, InterestCalculator ic) {
		BorrowCollection c = new BorrowCollection();
		if (model.getType() == 107) {// 公益标，没有利息
			c.setInterest(0);
		} else {// 非公益标有利息算进来
			c.setInterest(mi.getInterest());
		}
		c.setCapital(mi.getAccountPerMon());
		c.setRepayAccount((mi.getInterest() + mi.getAccountPerMon()));

		if (model.getType() == Constant.TYPE_EXPERIENCE) { // add by gy
															// 2016-10-19
															// 13:49:52 体验标没有本金
			c.setCapital(0);
			c.setRepayAccount(mi.getInterest()); // add by gy 2016-10-19
													// 13:50:45 预还金额 没有本金 只有利息
		}

		logger.info("------------------id:" + model.getId() + "   " + mi.getInterest() + "  " + mi.getAccountPerMon() + "  " + (mi.getInterest() + mi.getAccountPerMon()));
		c.setAddtime(new Date());
		c.setBorrowTender(t);
		return c;
	}

	// TGPROJECT-411 wsl 2014-09-04 start
	@Override
	public double getAwardValue(double account) {
		double awardValue = 0;
		if (model.getAward() == 1) {// 按投标金额比例
			awardValue = account * model.getPartAccount() / 100;
		} else if (model.getAward() == 2) {// 按固定金额分摊奖励
			awardValue = model.getFunds() / model.getAccount() * account;
		} else if (model.getAward() == 3) {// 后台按投标金额比例
			awardValue = account * model.getAdminAward() / 100;
		} else if (model.getAward() == 4) {// 后台按固定金额分摊奖励
			awardValue = model.getAdminFunds() / model.getAccount() * account;
		} else {
			awardValue = 0;
		}
		return NumberUtils.format2(awardValue);
	}

	// TGPROJECT-411 wsl 2014-09-04 end
	@Override
	public double getAwardValue() {
		return getAwardValue(model.getAccount());
	}

	@Override
	public double getManageFee() {
		double fee = 0.0;
		double account = model.getAccount();
		fee = getManageFee(account);
		return NumberUtils.format2(fee);
	}

	public double getBorrowSecondFreeze() {
		double interest = model.getRepaymentAccount() - model.getAccount();
		double fee = getManageFee();
		// double award = model.getFunds() + model
		return 0;
	}

	@Override
	public double getManageFee(double account) {
		double fee = 0.0;
		BorrowConfig cfg = getConfig();
		if (cfg != null) {
			if (model.getIsday() == 1) {
				fee = account * cfg.getDaymanagefee() * model.getTimeLimitDay() / 3000;
			} else {
				fee = account * cfg.getManagefee() * model.getTimeLimit() / 100;
			}
		}
		return NumberUtils.format2(fee);
	}

	@Override
	public int getCreditValue(double account) {
		double credit = 0;
		if (model.getIsday() == 1) {
			credit = account * model.getTimeLimitDay() / 3000;
		} else {
			credit = account * model.getTimeLimit() / 100;
		}
		return (int) credit;
	}

	/**
	 * 根据规则，计算管理费，规则如下： 0--满标复审通过的时候收取全部期数的管理费 1--还款的时候收取管理费，每还一次，收取一次管理费
	 */
	@Override
	public double getManageFeeWithRule(int timeLimit) {
		return NumberUtils.format2(timeLimit * getOneTimeLimitFee(model.getAccount()));
	}

	/**
	 * 根据规则，计算管理费，规则如下： 费率：0--按照初审通过时，填写的费率（每个月应收的费率），保存在borrow_fee表里边。 1--borrow_config 系统初始化进来的费率 计算每期的管理费用
	 */
	@Override
	public double getOneTimeLimitFee(double account) {

		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		RuleService ruleService = (RuleService) wac.getBean("ruleService");
		double fee = 0.0;
		BorrowConfig cfg = getConfig();
		Rule feeRule = ruleService.getRuleByNid("global_borrow_fee_rule");// 收取管理费规则
		int fee_rate_type = 0;
		if (feeRule != null && feeRule.getStatus() == 1) {
			fee_rate_type = feeRule.getValueIntByKey("fee_rate_type");// 收取管理费方式
																		// 0：按照初审填写收取，1：按照borrow_config配置中收取
		}
		double month_fee_rate = 0;
		double day_fee_rate = 0;
		if (fee_rate_type == 0) {// 按照填写的费率收取，填写的费率，只能是每个月的收取的费率,月标和天标都从这里边来取值
			if (model.getType() == 101) {// 秒标不支持，费率从后台填写进去的方式
				logger.info("秒标不支持，后台设置费率的方式,费率从borrow_config取");
				month_fee_rate = (cfg == null) ? 0 : cfg.getManagefee() / 100;
			} else {
				month_fee_rate = model.getBorrowFee().getManagefee();
				day_fee_rate = model.getBorrowFee().getManagefee();
			}
		} else {// 从borrow_config 里边取值。
			month_fee_rate = (cfg == null) ? 0 : cfg.getManagefee() / 100;
			day_fee_rate = (cfg == null) ? 0 : cfg.getDaymanagefee() / 3000;
		}
		if (model.getIsday() == 1) {
			fee = account * day_fee_rate * model.getTimeLimitDay();
		} else {
			fee = account * month_fee_rate;
		}
		return NumberUtils.format2(fee);
	}
}
