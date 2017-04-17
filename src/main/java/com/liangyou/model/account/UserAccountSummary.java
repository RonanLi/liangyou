package com.liangyou.model.account;

import com.liangyou.domain.User;

public class UserAccountSummary extends User {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9112886805833157599L;
	private long user_id;
	// 个人资金详情
	// 账户总额
	private double accountTotal;
	// 可用余额
	private double accountUseMoney;
	// 冻结金额
	private double accountNoUseMoney;
	//净资产
	private double accountOwnMoney;
	
	// 投标冻结总额
	private double borrowNoUseTotal;

	// 投资资金详情
	// 投标总额
	private double investTotal;
	
	private double investInterest;
	// 借出总额
	private double lendTotal;
	// 奖励收入总额
	private double awardTotal;

	// 收款资金详情
	// 逾期的本息
	private double lateTotal;
	//迟还款总额
	private double lateRepayed;
	// 逾期的利息
	private double lateInterest;
	// 逾期罚金收入
	private double overdueInterest;
	// 网站垫付总额
	private double advanceTotal;

	// 损失利息总额
	private double lossInterest;
	// 最近收款日期
	private String newestCollectDate;
	private double newestCollectMoney;
	private double newestCollectInterest;
	// 待回收总额
	private double collectTotal;
	// 待回收利息
	private double collectInterestTotal;

	// 贷款资金详情
	// 借款总额
	private double borrowTotal;
	
	private double borrowInterest;
	// 借款标次数
	private int borrowTimes;
	private double repayTotal;
	private double repayInterest;//待还的利息总和
	private double yesRepayTotal;
	// 已还总额
	private double repaidTotal;
	// 未还总额
	private double notRepayTotal;

	// 投资次数
	private int investTimes;
	// 还款标数
	private int repayTimes;
	// 待还笔数
	private int waitRepayTimes;
	// 最近还款日期
	private String newestRepayDate;
	// 最近应还款本金
	private double newestRepayMoney;

	// 充值成功总额
	private double rechargeTotal;
	// 提现成功总额
	private double cashTotal;
	// 在线充值总额
	private double onlineRechargeTotal;
	// 线下充值总额
	private double offlineRechargeTotal;
	// 收到充值总额
	private double manualRechargeTotal;
	// 手续费总额
	private double feeTotal;
	// 充值手续费
	private double rechargeFee;
	// 提现手续费
	private double cashFee;

	// 最高额度
	private double mostAmount;
	// 最低额度
	private double leastAmount;
	// 可用额度
	private double useAmount;

	private int waitRepayCount;
	
	private int hadRepayCount;
	
	private int hadDueRepayCount;
	
	private int waitDueRepayCount;
	
	//正在投标次数
	private int tenderingCount;
	//已经回收的合计
	private double hasCollectTotal;
	private double hasCollectInterestTotal;
	//贷回收的次数
	private int waitCollectCount;
	
	private int hasCollectCount;
	
	private double recentlyRepaymentTotal;

	private double nowCollectionInterest;
	
	private int waitFullBorrowTenderCount;
	
	private double waitFullBorrowTenderTotal;

	//wsl 满标前补偿金功能【心意贷】2014-09-01 start
	private double collectionCompensation;//待收补偿金
	
	private double collectionYesCompensation;//已收补偿金
	
	private double repayCompensation;//待还补偿金
	
	private double repayYesCompensation;//已还补偿金
	//wsl 满标前补偿金功能【心意贷】2014-09-01 end
	
	private String cannel; // 渠道来源
	private long peopleNum; // 投资人数
	
	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}
	public double getAccountTotal() {
		return accountTotal;
	}

	public void setAccountTotal(double accountTotal) {
		this.accountTotal = accountTotal;
	}

	public double getAccountUseMoney() {
		return accountUseMoney;
	}

	public void setAccountUseMoney(double accountUseMoney) {
		this.accountUseMoney = accountUseMoney;
	}

	public double getAccountNoUseMoney() {
		return accountNoUseMoney;
	}

	public void setAccountNoUseMoney(double accountNoUseMoney) {
		this.accountNoUseMoney = accountNoUseMoney;
	}

	public double getBorrowNoUseTotal() {
		return borrowNoUseTotal;
	}

	public void setBorrowNoUseTotal(double borrowNoUseTotal) {
		this.borrowNoUseTotal = borrowNoUseTotal;
	}

	public double getInvestTotal() {
		return investTotal;
	}

	public void setInvestTotal(double investTotal) {
		this.investTotal = investTotal;
	}

	public double getLendTotal() {
		return lendTotal;
	}

	public void setLendTotal(double lendTotal) {
		this.lendTotal = lendTotal;
	}

	public double getAwardTotal() {
		return awardTotal;
	}

	public void setAwardTotal(double awardTotal) {
		this.awardTotal = awardTotal;
	}

	public double getCollectTotal() {
		return collectTotal;
	}

	public void setCollectTotal(double collectTotal) {
		this.collectTotal = collectTotal;
	}

	public double getCollectInterestTotal() {
		return collectInterestTotal;
	}

	public void setCollectInterestTotal(double collectInterestTotal) {
		this.collectInterestTotal = collectInterestTotal;
	}

	public double getLateTotal() {
		return lateTotal;
	}

	public void setLateTotal(double lateTotal) {
		this.lateTotal = lateTotal;
	}

	public double getLateInterest() {
		return lateInterest;
	}

	public void setLateInterest(double lateInterest) {
		this.lateInterest = lateInterest;
	}
	
	public double getOverdueInterest() {
		return overdueInterest;
	}

	public void setOverdueInterest(double overdueInterest) {
		this.overdueInterest = overdueInterest;
	}

	public double getAdvanceTotal() {
		return advanceTotal;
	}

	public void setAdvanceTotal(double advanceTotal) {
		this.advanceTotal = advanceTotal;
	}

	public double getLossInterest() {
		return lossInterest;
	}

	public void setLossInterest(double lossInterest) {
		this.lossInterest = lossInterest;
	}

	public String getNewestCollectDate() {
		return newestCollectDate;
	}

	public void setNewestCollectDate(String newestCollectDate) {
		this.newestCollectDate = newestCollectDate;
	}

	public double getBorrowTotal() {
		return borrowTotal;
	}

	public void setBorrowTotal(double borrowTotal) {
		this.borrowTotal = borrowTotal;
	}

	public int getBorrowTimes() {
		return borrowTimes;
	}

	public void setBorrowTimes(int borrowTimes) {
		this.borrowTimes = borrowTimes;
	}

	public double getRepaidTotal() {
		return repaidTotal;
	}

	public void setRepaidTotal(double repaidTotal) {
		this.repaidTotal = repaidTotal;
	}

	public double getNotRepayTotal() {
		return notRepayTotal;
	}

	public void setNotRepayTotal(double notRepayTotal) {
		this.notRepayTotal = notRepayTotal;
	}

	public int getInvestTimes() {
		return investTimes;
	}

	public void setInvestTimes(int investTimes) {
		this.investTimes = investTimes;
	}

	public int getRepayTimes() {
		return repayTimes;
	}

	public void setRepayTimes(int repayTimes) {
		this.repayTimes = repayTimes;
	}
	
	public String getNewestRepayDate() {
		return newestRepayDate;
	}

	public void setNewestRepayDate(String newestRepayDate) {
		this.newestRepayDate = newestRepayDate;
	}
	public double getNewestCollectMoney() {
		return newestCollectMoney;
	}

	public void setNewestCollectMoney(double newestCollectMoney) {
		this.newestCollectMoney = newestCollectMoney;
	}

	public double getNewestRepayMoney() {
		return newestRepayMoney;
	}

	public void setNewestRepayMoney(double newestRepayMoney) {
		this.newestRepayMoney = newestRepayMoney;
	}

	public double getRechargeTotal() {
		return rechargeTotal;
	}

	public void setRechargeTotal(double rechargeTotal) {
		this.rechargeTotal = rechargeTotal;
	}

	public double getCashTotal() {
		return cashTotal;
	}

	public void setCashTotal(double cashTotal) {
		this.cashTotal = cashTotal;
	}

	public double getOnlineRechargeTotal() {
		return onlineRechargeTotal;
	}

	public void setOnlineRechargeTotal(double onlineRechargeTotal) {
		this.onlineRechargeTotal = onlineRechargeTotal;
	}

	public double getOfflineRechargeTotal() {
		return offlineRechargeTotal;
	}

	public void setOfflineRechargeTotal(double offlineRechargeTotal) {
		this.offlineRechargeTotal = offlineRechargeTotal;
	}

	public double getManualRechargeTotal() {
		return manualRechargeTotal;
	}

	public void setManualRechargeTotal(double manualRechargeTotal) {
		this.manualRechargeTotal = manualRechargeTotal;
	}

	public double getFeeTotal() {
		return feeTotal;
	}

	public void setFeeTotal(double feeTotal) {
		this.feeTotal = feeTotal;
	}

	public double getRechargeFee() {
		return rechargeFee;
	}

	public void setRechargeFee(double rechargeFee) {
		this.rechargeFee = rechargeFee;
	}

	public double getCashFee() {
		return cashFee;
	}

	public void setCashFee(double cashFee) {
		this.cashFee = cashFee;
	}

	public double getMostAmount() {
		return mostAmount;
	}

	public void setMostAmount(double mostAmount) {
		this.mostAmount = mostAmount;
	}

	public double getLeastAmount() {
		return leastAmount;
	}

	public void setLeastAmount(double leastAmount) {
		this.leastAmount = leastAmount;
	}

	public double getUseAmount() {
		return useAmount;
	}

	public void setUseAmount(double useAmount) {
		this.useAmount = useAmount;
	}

	public double getInvestInterest() {
		return investInterest;
	}

	public void setInvestInterest(double investInterest) {
		this.investInterest = investInterest;
	}

	public double getBorrowInterest() {
		return borrowInterest;
	}

	public void setBorrowInterest(double borrowInterest) {
		this.borrowInterest = borrowInterest;
	}

	public double getAccountOwnMoney() {
		return accountOwnMoney;
	}

	public void setAccountOwnMoney(double accountOwnMoney) {
		this.accountOwnMoney = accountOwnMoney;
	}

	public double getRepayTotal() {
		return repayTotal;
	}

	public void setRepayTotal(double repayTotal) {
		this.repayTotal = repayTotal;
	}

	public double getRepayInterest() {
		return repayInterest;
	}

	public void setRepayInterest(double repayInterest) {
		this.repayInterest = repayInterest;
	}

	public int getWaitRepayTimes() {
		return waitRepayTimes;
	}

	public void setWaitRepayTimes(int waitRepayTimes) {
		this.waitRepayTimes = waitRepayTimes;
	}

	public int getWaitRepayCount() {
		return waitRepayCount;
	}

	public void setWaitRepayCount(int waitRepayCount) {
		this.waitRepayCount = waitRepayCount;
	}

	public int getHadRepayCount() {
		return hadRepayCount;
	}

	public void setHadRepayCount(int hadRepayCount) {
		this.hadRepayCount = hadRepayCount;
	}

	public int getHadDueRepayCount() {
		return hadDueRepayCount;
	}

	public void setHadDueRepayCount(int hadDueRepayCount) {
		this.hadDueRepayCount = hadDueRepayCount;
	}

	public int getWaitDueRepayCount() {
		return waitDueRepayCount;
	}

	public void setWaitDueRepayCount(int waitDueRepayCount) {
		this.waitDueRepayCount = waitDueRepayCount;
	}

	public int getTenderingCount() {
		return tenderingCount;
	}

	public void setTenderingCount(int tenderingCount) {
		this.tenderingCount = tenderingCount;
	}

	public double getHasCollectTotal() {
		return hasCollectTotal;
	}

	public void setHasCollectTotal(double hasCollectTotal) {
		this.hasCollectTotal = hasCollectTotal;
	}

	public double getHasCollectInterestTotal() {
		return hasCollectInterestTotal;
	}

	public void setHasCollectInterestTotal(double hasCollectInterestTotal) {
		this.hasCollectInterestTotal = hasCollectInterestTotal;
	}

	public int getWaitCollectCount() {
		return waitCollectCount;
	}

	public void setWaitCollectCount(int waitCollectCount) {
		this.waitCollectCount = waitCollectCount;
	}

	public int getHasCollectCount() {
		return hasCollectCount;
	}

	public void setHasCollectCount(int hasCollectCount) {
		this.hasCollectCount = hasCollectCount;
	}

	public double getNowCollectionInterest() {
		return nowCollectionInterest;
	}

	public void setNowCollectionInterest(double nowCollectionInterest) {
		this.nowCollectionInterest = nowCollectionInterest;
	}

	public double getRecentlyRepaymentTotal() {
		return recentlyRepaymentTotal;
	}

	public void setRecentlyRepaymentTotal(double recentlyRepaymentTotal) {
		this.recentlyRepaymentTotal = recentlyRepaymentTotal;
	}

	public double getNewestCollectInterest() {
		return newestCollectInterest;
	}

	public void setNewestCollectInterest(double newestCollectInterest) {
		this.newestCollectInterest = newestCollectInterest;
	}

	public double getLateRepayed() {
		return lateRepayed;
	}

	public void setLateRepayed(double lateRepayed) {
		this.lateRepayed = lateRepayed;
	}

	public double getYesRepayTotal() {
		return yesRepayTotal;
	}

	public void setYesRepayTotal(double yesRepayTotal) {
		this.yesRepayTotal = yesRepayTotal;
	}

	public int getWaitFullBorrowTenderCount() {
		return waitFullBorrowTenderCount;
	}

	public void setWaitFullBorrowTenderCount(int waitFullBorrowTenderCount) {
		this.waitFullBorrowTenderCount = waitFullBorrowTenderCount;
	}

	public double getWaitFullBorrowTenderTotal() {
		return waitFullBorrowTenderTotal;
	}

	public void setWaitFullBorrowTenderTotal(double waitFullBorrowTenderTotal) {
		this.waitFullBorrowTenderTotal = waitFullBorrowTenderTotal;
	}
	
	//wsl 满标前补偿金功能【心意贷】2014-09-01 start
	public double getCollectionCompensation() {
		return collectionCompensation;
	}

	public void setCollectionCompensation(double collectionCompensation) {
		this.collectionCompensation = collectionCompensation;
	}

	public double getCollectionYesCompensation() {
		return collectionYesCompensation;
	}

	public void setCollectionYesCompensation(double collectionYesCompensation) {
		this.collectionYesCompensation = collectionYesCompensation;
	}

	public double getRepayCompensation() {
		return repayCompensation;
	}

	public void setRepayCompensation(double repayCompensation) {
		this.repayCompensation = repayCompensation;
	}

	public double getRepayYesCompensation() {
		return repayYesCompensation;
	}

	public void setRepayYesCompensation(double repayYesCompensation) {
		this.repayYesCompensation = repayYesCompensation;
	}
	//wsl 满标前补偿金功能【心意贷】2014-09-01 end

	public String getCannel() {
		return cannel;
	}

	public void setCannel(String cannel) {
		this.cannel = cannel;
	}

	public long getPeopleNum() {
		return peopleNum;
	}

	public void setPeopleNum(long peopleNum) {
		this.peopleNum = peopleNum;
	}

	
}
