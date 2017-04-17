package com.liangyou.service;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.Message;
import com.liangyou.api.chinapnr.CashOut;
import com.liangyou.api.chinapnr.FssTrans;
import com.liangyou.api.pay.DeductSign;
import com.liangyou.api.pay.DepositQuery;
import com.liangyou.api.pay.SignmanyBank;
import com.liangyou.api.pay.UserAccountQuery;
import com.liangyou.api.pay.WithdrawQquery;
import com.liangyou.api.pay.YzzNewDeduct;
import com.liangyou.domain.Account;
import com.liangyou.domain.AccountBank;
import com.liangyou.domain.AccountCash;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.AccountRecharge;
import com.liangyou.domain.AccountWebDeduct;
import com.liangyou.domain.DrawBank;
import com.liangyou.domain.DrawBankMmm;
import com.liangyou.domain.GlodTransfer;
import com.liangyou.domain.InterestGenerate;
import com.liangyou.domain.User;
import com.liangyou.domain.WebGlodLog;
import com.liangyou.model.BorrowParam;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.APIModel.AccountCashModel;
import com.liangyou.model.APIModel.RechargeModel;
import com.liangyou.model.account.BaseAccountSumModel;
import com.liangyou.model.account.UserAccountSummary;
import com.liangyou.model.account.WebAccountSumModel;

/**
 * 資金服务
 * 
 * @author 1432
 *
 */
public interface AccountService {

	/**
	 * 新增充值记录，但是Account记录并未到账
	 * 
	 * @param r
	 * @param log
	 */
	public void addRecharge(AccountRecharge r);

	public UserAccountSummary getUserAccountSummary(long user_id);

	// v1.8.0.4_u2 TGPROJECT-308 lx 2014-05-22 start
	/**
	 * 查询该用户是否绑定了某一卡号的银行卡
	 * 
	 * @param cardNo
	 * @param user
	 * @return
	 */

	public AccountBank getAccountBankByCardNo(String cardNo, User user);

	/**
	 * 易极付充值全部回调都在这边
	 * 
	 * @param re
	 * @param log
	 * @param request
	 */
	public void doRechargeTask(RechargeModel rechargeModel, AccountLog log);

	// v1.8.0.4_u2 TGPROJECT-308 lx 2014-05-22 end
	// v1.8.0.4_u1 TGPROJECT-289 lx start
	/**
	 * 获取网站所有用户资金的统计
	 * 
	 * @return
	 */
	public WebAccountSumModel getWebAccountSumModel();

	// v1.8.0.4_u1 TGPROJECT-289 lx end
	/**
	 * 更新充值记录,并将充值金额到账
	 * 
	 * @param r
	 * @param log
	 * @throws Exception
	 */
	// public void newRecharge(RechargeModel re,AccountLog log ,String params)
	// throws Exception;
	/**
	 * 充值失败
	 * 
	 * @param re
	 * @param log
	 */
	public void failRecharge(RechargeModel re, AccountLog log, String params);

	/**
	 * 添加已经处理过的订单号
	 * 
	 * @param op
	 */
	public PageDataList<Account> getAccontList(SearchParam param);

	public List<Account> getSumAccontList(SearchParam param);

	// 资金记录
	public List<AccountLog> getSumAccontLogList(SearchParam param);

	public PageDataList<AccountLog> getAccontLogList(SearchParam param);

	// 现金总和1
	public List<AccountCash> getSumCashList(SearchParam param);

	// 投资人投资情况
	// public List<InvestSummary> getInvestSummaryList(int type);
	public PageDataList<AccountCash> getCashList(SearchParam param);

	// 总充值
	public List<AccountRecharge> getSumRechargeList(SearchParam param);

	// 充值列表
	public PageDataList<AccountRecharge> getRechargeList(SearchParam param);

	//
	public PageDataList<AccountWebDeduct> getWebDeductList(SearchParam param);

	public Account getAccountByUser(User u);

	// 用户资金记录
	public List<AccountBank> getBankListByUser(User u);

	// 添加银行
	public void addAccountBank(Object object);

	// 查询银行信息
	public PageDataList<AccountBank> getAccountBankList(SearchParam param);

	// 通过id查询银行信息
	public AccountBank getAccountBankById(int id);

	//
	public AccountWebDeduct getAccountWebDeductById(int id);

	// 添加或者更新银行信息
	public void addOrUpdateAccountBank(AccountBank ab);

	// 通过id删除
	public void delAccountBankById(int id);

	public AccountBank getAccontBankByAccount(String account);

	/**
	 * 易极付调用处理提现方法
	 * 
	 * @param cashModel
	 * @param param
	 * @return
	 */
	public boolean yjfNewCash(AccountCash accountCash, BorrowParam param);

	public void glodCashSuccess(CashOut cash, WebGlodLog log);

	/**
	 * 金账户充值添加记录
	 * 
	 * @param log
	 */
	public void webGlodLogBack(WebGlodLog log);

	public AccountCash getAccountCash(long id);

	public boolean verifyCash(AccountCash cash, AccountLog log);

	public List getDrawBankBySearchParam(SearchParam param);

	public DrawBank getDrawBankById(int id);

	public void delAccountCashById(int id);

	public void updateAccountCash(AccountCash ac);

	public void cancelCash(Account act, AccountCash cash, AccountLog log);

	/**
	 * 解除绑定银行卡
	 * 
	 * @param user
	 * @param accountBankId
	 */
	public void accountBankRemove(User user, String accountBankId);

	/**
	 * 第三方取现回调处理
	 * 
	 * @param awds
	 */
	public void cashCallBack(AccountCashModel cashModel, BorrowParam param);

	// v1.8.0.3 TGPROJECT-21 lx 2014-04-09 start

	// v1.8.0.3 TGPROJECT-21 lx 2014-04-09 end
	public AccountRecharge getAccountRechargeById(int id);

	public double getUserNetMoney(User user); // 获取用户的净资产

	public double getAllPropertyWaitRepayMoney(long user_id); // 获取用户所有净值标的待还总额

	// 对签约绑定银行卡的操作
	public List<AccountBank> getBankByUserId(long userid, int isbind); // 根据用户id和签约状态查询用户

	public AccountBank getBankByNoAndApiId(String no, String yid); // 根据银行卡号和易极付id查询用户银行卡

	public void updateIsbind(AccountBank bank); // 更新

	/**
	 * //查询当前用户可用的绑定的银行卡
	 * 
	 * @param userId
	 * @return
	 */
	public List<AccountBank> getBankLists(long userId);

	/**
	 * true, 流水号没有重复，可以充值
	 * 
	 * @param serialNo
	 * @return
	 */
	boolean checkRechargeOffLine(String serialNo);

	/**
	 * 网站后台充值
	 * 
	 * @param log
	 * @param user
	 * @param ar
	 * @throws Exception
	 */
	public void webRecharge(AccountLog log, AccountRecharge ar)
			throws Exception;

	/**
	 * 网站后台账户间转账
	 * 
	 * @param gt
	 */
	public void glodTransfer(GlodTransfer gt);

	public PageDataList getAllGlodTransfer(SearchParam param);

	/**
	 * 网站后台扣款
	 * 
	 * @param log
	 * @param awd
	 * @throws Exception
	 */
	public void webDeduct(AccountLog log, AccountWebDeduct awd);

	/**
	 * 审核扣款
	 * 
	 * @param log
	 * @param awd
	 */
	public void verifyWebDeduct(AccountLog log, AccountWebDeduct awd);

	public void dealAccountCash(AccountCash cash, double money,
			List<Object> taskList);

	/**
	 * 保存订单的方法
	 * 
	 * @param cash
	 */
	public void seveCash(AccountCash cash);

	public void saveWebGlodLog(WebGlodLog log);

	public PageDataList<WebGlodLog> getWebGlodLogList(SearchParam param);

	/**
	 * 根据订单号查询是否存在记录
	 * 
	 * @param ordid
	 * @return
	 */
	public int sumWebLogByOrdid(String ordid);

	/**
	 * 根据查询条件，查询出所有的记录
	 * 
	 * @param param
	 * @return
	 */
	public List<WebGlodLog> getAllGlodLogList(SearchParam param);

	// v1.8.0.3 TGPROJECT-11 lx 2014-04-03 start
	/**
	 * 查询用户投资情况
	 * 
	 * @param tenderParam
	 * @param userParam
	 * @return
	 */
	public PageDataList<BaseAccountSumModel> getUserInvestDetail(
			SearchParam tenderParam, SearchParam userParam);

	// v1.8.0.3 TGPROJECT-11 lx 2014-04-03 end
	/**
	 * 双乾支查询银行卡
	 * 
	 * @param param
	 * @return
	 */
	public List getDrawBankMmmBySearchParam(SearchParam param);

	public DrawBankMmm getDrawBankMmmById(int id);

	public void madeDeductSign(DeductSign dedu);

	// v1.8.0.4_u2 TGPROJECT-314 qj 2014-05-30 start
	public void madeInterestGenerateCall(FssTrans fssTrans);

	// v1.8.0.4_u2 TGPROJECT-314 qj 2014-05-30 end
	// v1.8.0.4_u2 TGPROJECT-314 qj 2014-05-30 start
	public void addInterestGenerate(InterestGenerate ig);

	// v1.8.0.4_u2 TGPROJECT-314 qj 2014-05-30 end
	// v1.8.0.4_u4 TGPROJECT-357 qinjun 2014-07-07 start
	public double getHuifuServFee(AccountCash accountCash);

	// v1.8.0.4_u4 TGPROJECT-357 qinjun 2014-07-07 end

	// TGPROJECT-362 无卡签约 start

	/**
	 * 代扣签约回调处理
	 * 
	 * @param signBank
	 */
	public void doSignmanyBank(SignmanyBank signBank);

	// TGPROJECT-362 无卡签约 end
	// TGPROJECT-410 wsl 2014-09-03 start
	/**
	 * 查询当日提现金额（包括提现成功的和正在申请的）
	 * 
	 * @param userId
	 * @return
	 */
	public double getAccountCashDailySum(long userId);

	// TGPROJECT-410 wsl 2014-09-03 end

	public void doIpsAddBank(User user, AccountBank bank);

	/**
	 * 易极付代扣充值异步回调处理
	 * 
	 * @param yzznewdeduct
	 * @throws Exception
	 */
	public void deductRechargeOffLine(YzzNewDeduct yzznewdeduct)
			throws Exception;

	/**
	 * 根据流水号查询用户提现信息
	 */
	public AccountCash getAccountCashByLoanNo(String orderNo);

	/** 后台充值 根据充值类型充值
	 * @param log
	 * @param r
	 * @param rechargeType
	 * @throws Exception 
	 */
	public void webRecharge(AccountLog log, AccountRecharge r,
			String rechargeType) throws Exception;

	/**add by lijing 2016-11-25 
	 * 后台批量充值
	 * @param rechargeIP
	 * @param remark 
	 * @return
	 */
	public String webBatchRecharge(String rechargeIP, String remark);

	/**add by lijing 2016-11-28 10:01:50
	 * 后台收回转账
	 * @param log
	 * @param r
	 * @throws Exception
	 */
	public void webBackRecharge(AccountLog log, AccountRecharge r,User rechargeUser) throws Exception;

	/**add by lijing 2016-12-7 15:33:33
	 * 
	 * 后台批量充值回调处理
	 * @param accountRecharge
	 */
	public void doBatchRechargeTask(AccountRecharge accountRecharge);

	/**add by lijing 2016-12-13 12:00:14
	 * 批量充值待审核列表
	 * @param param
	 * @return
	 */
	public PageDataList<AccountRecharge> getAllVerifyBR(SearchParam param);

	/**add by lijing 2016-12-13 12:00:18
	 * 批量充值后台审核列表
	 * @param batchno
	 * @return
	 */
	public PageDataList<AccountRecharge> getAccountRechargeByBathNo(
			String batchno);

	/**add by lijing 2016-12-13 12:08:07
	 * 批量充值后台审核
	 * @param list
	 * @param checkString 
	 */
	public void verifyBatchRecharge(List<AccountRecharge> list, String checkString);

	public ArrayList<AccountRecharge> getAccountRechargeByBathNoAndState(String batchno);


   

}
