package com.liangyou.service;

import java.util.Date;
import java.util.List;

import com.liangyou.api.ips.IpsRepaymentNewTrade;
import com.liangyou.domain.AccountLog;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowAuto;
import com.liangyou.domain.BorrowCollection;
import com.liangyou.domain.BorrowConfig;
import com.liangyou.domain.BorrowDetail;
import com.liangyou.domain.BorrowDetailType;
import com.liangyou.domain.BorrowFee;
import com.liangyou.domain.BorrowIncomeLadder;
import com.liangyou.domain.BorrowIntent;
import com.liangyou.domain.BorrowProperty;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.ChinaPnrPayModel;
import com.liangyou.domain.IpsPay;
import com.liangyou.domain.MmmPay;
import com.liangyou.domain.User;
import com.liangyou.domain.YjfPay;
import com.liangyou.model.BorrowParam;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.borrow.BorrowModel;

/**
 * 借款标服务
 * 
 * @author 1432
 *
 */
public interface BorrowService {
	/**
	 * 获取标的列表方法
	 * 
	 * @return
	 */
	public List getList();

	/**
	 * 根据类型获取标的列表方法
	 * 
	 * @param type
	 *            101:秒标 102:信用标 103:抵押标 104:净值标 110:流转标 112:担保标 “秒标”是P2P网贷平台为招揽人气发放的高收益 、超短期限的借款标的，通常是网站虚构一笔借款，由投资者竞标并打款，网站在满标后很快就连本带息还款 。网络上由此聚集了一批专门投资秒标的投资者，号称“秒客”。
	 * @return
	 */
	public List getList(int type);

	/**
	 * 根据类型、状态获取标的列表方法
	 * 
	 * @param type
	 * @return
	 */
	public List getList(int type, int status);

	/**
	 * 根据BorrowModel对象获取标的列表方法
	 * 
	 * @param model
	 * @return
	 */
	public PageDataList getList(SearchParam param);

	/**
	 * add by gy 2016年12月14日15:19:35
	 * 获取wap端首页的标的列表
	 * @return
	 */
	public List<Borrow> getWapIndexBorrowList();

	/**
	 * add by gy 2016年12月14日15:19:35
	 * wap端获取标的列表
	 * @param param
	 * @param clazz
	 * @return
	 */
	public PageDataList findWapBorrowPageListBySql(SearchParam param);


	/**
	 * 获取导出excel list
	 * 
	 * @param param
	 * @return
	 */
	public List getExportBorrowList(SearchParam param);

	/**
	 * 借款标的信息，不含借款人的信息
	 * 
	 * @param id
	 * @return
	 */
	public Borrow getBorrow(long id);

	public PageDataList getTenderList(SearchParam param);

	/**
	 * 新增标的核心方法
	 * 
	 * @param borrow
	 * 
	 * 
	 * @param log
	 */
	// v1.8.0.3_u3 XINHEHANG-66 wuing 2014-06-19 start
	// 发标方法修改，将添加borrowproperty的service与borrow放在一个service中，便于事物控制
	// v1.8.0.3_u3 TGPROJECT-375 qinjun 2014-07-22 start
	public void addBorrow(BorrowModel borrow, AccountLog log, BorrowProperty property, List<BorrowDetail> details);

	// v1.8.0.3_u3 TGPROJECT-375 qinjun 2014-07-22 end
	// v1.8.0.3_u3 XINHEHANG-66 wujing 2014-06-19 end
	/**
	 * 新增债转标的核心方法
	 * 
	 * @param borrow
	 * @param log
	 */
	public void addAssignmentBorrow(BorrowModel borrow, AccountLog log, BorrowTender bt);

	/**
	 * 更新借款标
	 * 
	 * @param borrow
	 */
	public void updateBorrow(Borrow borrow);

	// v1.8.0.3_u3 XINHEHANG-66 wuing 2014-06-19 start
	// 新增更新借款信息接口
	public void updateBorrowAndProperty(Borrow borrow, BorrowProperty property);

	// v1.8.0.3_u3 XINHEHANG-66 wujing 2014-06-19 end
	// v1.8.0.4_u1 TGPROJECT-379 wsl 2014-08-01 start 信合行逾期罚息修改功能
	public void updateBorrowRepayment(BorrowRepayment repaymnet);

	// v1.8.0.4_u1 TGPROJECT-379 wsl 2014-08-01 end
	/**
	 * 更新标的子类型
	 * 
	 * @param borrow
	 */
	public void updateBorrowProperty(BorrowProperty borrowProperty);

	/**
	 * 获取未满标的借款标
	 * 
	 * @param user_id
	 * @return
	 */
	public List unfinshBorrowList(long user_id);

	/**
	 * 投标的核心方法
	 * 
	 * @param tender
	 * @param borrow
	 * @param act
	 * @param log
	 * @return
	 * @throws Exception
	 */
	public BorrowTender addTender(BorrowParam param, User user) throws Exception;

	/**
	 * 双乾投标方法 borrowTender 投标表
	 * 
	 * @param param
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public BorrowTender addMmmTender(BorrowParam param, User user) throws Exception;

	/**
	 * add by gy 2016-10-14 10:04:57 体验标投标方法
	 *
	 * @param param
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public BorrowTender doTenderExperienceBorrow(BorrowParam param, User user) throws Exception;

	/**
	 * 初审借款标
	 * 
	 * @throws Exception
	 */
	// v1.8.0.3_u3 TGPROJECT-335 qinjun 2014-06-16 start
	public void verifyBorrow(BorrowModel borrow, AccountLog log, BorrowFee borrowFee, List<BorrowDetail> details) throws Exception;

	// v1.8.0.3_u3 TGPROJECT-335 qinjun 2014-06-16 end

	public PageDataList getCollectList(SearchParam param);

	/**
	 * 满标复审
	 * 
	 * @throws Exception
	 */
	public void verifyFullBorrow(BorrowModel borrow, BorrowParam param) throws Exception;

	/**
	 * 根据borrowId 查询所有的 投资人。
	 * 
	 * @param borrowId
	 * @return
	 */
	public List<BorrowTender> getAllTenderByBorrowId(Long borrowId);

	/**
	 * 用户撤回标
	 * 
	 * @param borrow
	 * @param log
	 */
	public void deleteBorrow(Borrow borrow, AccountLog log, BorrowParam param);

	/**
	 * 双乾接口统一处理 ---》是一个独立的过程，绝对不能影响本地系统核心的业务。
	 * 
	 * @param taskList
	 * @return
	 */
	public boolean doMmmTask(List<Object> taskList);

	/**
	 * 查询发标拦截配置
	 * 
	 * @param id
	 * @return
	 */
	public BorrowConfig getBorrowConfig(int id);

	/**
	 * yjf任务调度查询
	 * 
	 * @param
	 * @return
	 */
	public PageDataList<YjfPay> getYjfPayList(SearchParam sp);

	/**
	 * 双乾任务调度查询
	 * 
	 * @param sp
	 * @return
	 */
	public PageDataList<MmmPay> getMmmPayList(SearchParam sp);

	/**
	 * yjf任务查询
	 */
	public YjfPay getYjfPayById(int id);

	/**
	 * yjf处理
	 * 
	 * @param
	 * @return
	 */
	public YjfPay autoYjfPay(YjfPay yjfPay);

	public MmmPay getMmmPayById(int id);

	/**
	 * 查询还款记录
	 * 
	 * @return
	 */
	public PageDataList getBorrowRepaymentList(SearchParam param);

	public List getBorrowRepaymentExportList(SearchParam param);

	/**
	 * 用户取消待审核的标
	 * 
	 * @param borrowModel
	 */
	public void userCancelBorrow(BorrowModel borrowModel);

	public List<Borrow> getBorrowListOrderByStatus();

	/**
	 * 查询所有的用户 满足的话就投标
	 * 
	 * @param model
	 * @throws Exception
	 */
	public void doAutoTender(BorrowModel model) throws Exception;

	/**
	 * 网站垫付
	 * 
	 * @param borrowid
	 * @throws Exception
	 */
	public void webSitePayForLateBorrow(long borrowid, BorrowParam param) throws Exception;

	public BorrowTender getTenderById(long id);

	public BorrowRepayment getBorrowRepaymentById(long repayMentId);

	public BorrowRepayment getBorrowRepaymentByBorrowIdAndPeriod(long borrowId, int repayMentId);

	/**
	 * 获取能债权转让的所有投标记录
	 * 
	 * @param param
	 * @return
	 */
	public PageDataList<BorrowTender> getAssignmentBorrowTenders(SearchParam param);

	/**
	 * 核对是否能发 债权转让标
	 * 
	 * @return
	 */
	public String checkAssignMentBorrow(BorrowTender bt);

	public Borrow getAssignMentBorrowByTenderId(long tenderId);

	public BorrowTender getAssignMentTenderByBorrowId(long borrowId);

	public void isCanVerifyFullSuccess(long borrowId);

	/**
	 * 根据用户id查询该用户待收总额
	 * 
	 * @param userId
	 * @return
	 */
	public double sumTenderWaitAccount(long userId);

	/**
	 * 结标方法， 任何标
	 * 
	 * @param borrowId
	 */
	public double doBorrowFull(long borrowId);

	/**
	 * 查询指定用户逾期还款的个数
	 * 
	 * @param user
	 * @return
	 */
	public int getLateRepaymentByUser(User user);

	/**
	 * 查询网站成功借款总额
	 * 
	 * @return
	 */
	public double getSuccessBorrowSumAccount();

	/**
	 * 添加标的子类型
	 * 
	 * @param borrowProperty
	 */
	public void addBorrowProperty(BorrowProperty borrowProperty);

	/**
	 * 统一处理所有的接口任务
	 * 
	 * @param taskList
	 * @return
	 */
	public boolean doApiTask(List<Object> taskList);

	public boolean doApiTask(List<Object> taskList, String rechargeType);

	public int getlimtTime(int id);

	/**
	 * 获取当天发表总数
	 * 
	 * @return
	 */
	public int countBorrowByDay();

	/**
	 * 获取当天投标总数
	 * 
	 * @return
	 */
	public int countTenderByDay();

	/**
	 * 计算用户所有借款的待还本金总额
	 * 
	 * @param userId
	 * @return
	 */
	public double sumBorrowAccountByUserId(long userId);

	/**
	 * 根据id查询chinapnr对象
	 * 
	 * @param id
	 * @return
	 */
	public ChinaPnrPayModel getChinapnrPayById(int id);

	public PageDataList<ChinaPnrPayModel> getChinapnrList(SearchParam param);

	// v1.8.0.3 TGPROJECT-12 lx 2014-04-02 start
	/**
	 * 
	 * @param borrowId
	 * @param period
	 * @return
	 */
	public List<BorrowCollection> getCollectionByBorrowIdAndPeriod(long borrowId, int period);

	// v1.8.0.3 TGPROJECT-12 lx 2014-04-02 end
	// v1.8.0.4_u1 TGPROJECT-127 lx start
	public PageDataList<Borrow> getFriendBorrowList(SearchParam param);

	// v1.8.0.4_u1 TGPROJECT-127 lx end
	// v1.8.0.4_u1 TGPROJECT-240 zf start
	public PageDataList<BorrowAuto> findAutoTenderList(SearchParam param);

	// v1.8.0.4_u1 TGPROJECT-240 zf end
	// v1.8.0.4_u2 TGPROJECT-299 lx start
	public List<BorrowCollection> getCollectionList(long borrowId);

	// v1.8.0.4_u2 TGPROJECT-299 lx end
	// v1.8.0.4_u2 TGPROJECT-324 lx 2014-05-29 start
	public void addBorrowIntent(BorrowIntent borrowIntent);

	public PageDataList<BorrowIntent> findListBorrowIntent(SearchParam param);

	public BorrowIntent getBorrowIntent(long id);

	public void updateBorrowIntent(BorrowIntent borrowIntent);

	// v1.8.0.4_u2 TGPROJECT-324 lx 2014-05-29 end
	// v1.8.0.3_u3 TGPROJECT-335 qinjun 2014-06-16 start
	public List<BorrowDetailType> getBorrowDetailTypeListByPid(long pid);

	// v1.8.0.3_u3 TGPROJECT-335 qinjun 2014-06-16 end

	// v1.8.0.3_u3 TGPROJECT-335 wuing 2014-06-17 start

	/**
	 * 根据borrowId查询borrowdeatil信息
	 * 
	 * @param borrowId
	 * @return
	 */
	public List<BorrowDetail> getBorrowDetailListByBorrowId(long borrowId);

	// v1.8.0.3_u3 TGPROJECT-335 wujing 2014-06-17 end

	// v1.8.0.4_u3 TGPROJECT-337 qinjun 2014-06-23 start
	public void quzrtzAwardForFridenBorrow();

	// v1.8.0.4_u3 TGPROJECT-337 qinjun 2014-06-23 end

	// 1.8.0.4_u3 TGPROJECT qinjun 2014-06-25 start
	/**
	 * 根据borrowid跟新借款开始时间
	 * 
	 * @param borrowId
	 * @param startDate
	 */
	public void updateBorrowStartDate(long borrowId, Date startDate);

	// 1.8.0.4_u3 TGPROJECT qinjun 2014-06-25 end
	/**
	 * 根据状态查询标
	 * 
	 * @return
	 */
	public List getBorrowByStatus(int borrowStatus);

	/**
	 * 融资总额
	 * 
	 * @return
	 */
	public double getBorrowSum();

	/**
	 * 融资总额产生利息
	 * 
	 * @return
	 */
	public double getBorrowSumInterest();

	// v1.8.0.5_u4 TGPROJECT-386 qinjun 2014-08-11 start
	/**
	 * 借款人还款提醒
	 * 
	 * @return
	 */
	public void noticeBorrowerRepay();

	// v1.8.0.5_u4 TGPROJECT-386 qinjun 2014-08-11 end

	public void batchSave(List<BorrowIncomeLadder> list);

	/**
	 * 环迅发标方法
	 * 
	 * @param param
	 */
	public void doIpsAddBorrow(BorrowParam param);

	/**
	 * 环迅接口统一处理
	 * 
	 * @param taskList
	 * @return
	 */
	public boolean doIpsTask(List<Object> taskList);

	/**
	 * 环迅接口触发接口触发
	 * 
	 * @param ipsPay
	 * @return
	 */
	public String autoIpsPay(IpsPay ipsPay);

	/**
	 * 更改环迅操作结果状态
	 * 
	 * @param status
	 * @param ordId
	 */
	public void updatePayStatus(IpsPay ipspay);

	/**
	 * 环迅还款，封装环迅对象
	 * 
	 * @param repayId
	 * @return
	 */
	public IpsRepaymentNewTrade doIpsRepayment(long repayId);

	public double countTenderRepayment(long id);

	/**
	 * 截标业务处理，
	 * 
	 * @param borrow
	 */
	public void doEndBorrow(Borrow borrow);

	/**
	 * 根据用户id查询已发布借款标
	 * 
	 * @param userId
	 * @return
	 */
	public List<Borrow> getBorrowListByUserId(long userId, int type);

	// 本人已投标金额
	public double hasTenderTotalPerBorrowByUserid(long bid, long userId);

}
