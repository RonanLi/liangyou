package com.liangyou.web.action.interfac;

import com.alibaba.fastjson.JSON;
import com.liangyou.context.AccountLogTypeName;
import com.liangyou.context.Constant;
import com.liangyou.domain.*;
import com.liangyou.json.Json;
import com.liangyou.model.OrderFilter;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchFilter;
import com.liangyou.model.SearchParam;
import com.liangyou.model.interfac.*;
import com.liangyou.service.*;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.BaseAction;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by Young on 2016/11/21.
 */
@Namespace("/interface/myAccount")
@ParentPackage("p2p-json")
public class AccountAction extends BaseAction {

    private static Logger logger = Logger.getLogger(AccountAction.class);

    @Autowired
    private AccountService accountService;
    @Autowired
    private BorrowService borrowService;
    @Autowired
    private UserService userService;
    @Autowired
    private MessageService messageService;


    /**
     * 投标列表
     *
     * @return
     */
    @Action(value = "borrowList")
    public String borrowList() {
        response.addHeader("Access-Control-Allow-Origin", "*");
        int status       = paramInt("status");
        String timeLimit = paramString("timeLimit");// 借款期限
        int borrowType   = paramInt("biaoType");    // 标的类型

        SearchParam param = SearchParam.getInstance().addPage(paramInt("page"), 10);

        // 项目状态
        if (status == 0) {
            status = 1;// 默认查询招标中
        }
        if (status == Constant.STATUS_complete) {// 成功案例状态 6，7， 8
            param.addParam("b.`status`", SearchFilter.Operator.GTE, 6).addParam("b.`status`", SearchFilter.Operator.LTE, 8);
        } else if (status == Constant.STATUS_REPAYING) {// 还款中 6,7
            param.addOrFilter("b.`status`", 6, 7);
        } else if (status == Constant.STATUS_ALLREPAIED) {// 还款成功
            param.addParam("b.`status`", 8);
        } else if (status == Constant.STATUS_REVIEW) {
            param.addParam("b.`status`", 3);
        } else if (status == 11) {// 查询正在招标、还款中、已还款
            param.addOrFilter("b.`status`", 1, 6, 7, 8);
        } else {// 招标中 status 1，且account!=accountYes
            param.addParam("b.account", SearchFilter.Operator.PROPERTY_NOTEQ, "b.account_yes").addParam("b.`status`", status);
        }

        // 项目期限
        if (!timeLimit.equals("all") && !StringUtils.isBlank(timeLimit) && !timeLimit.equals("0") && borrowType != 1 && borrowType != 101) {
            String[] timeLimitArgs = timeLimit.split("-");
            if (timeLimitArgs.length <= 1) {
                param.addParam("b.time_limit", SearchFilter.Operator.EQ, NumberUtils.getInt(timeLimitArgs[0]));
            } else if (timeLimitArgs[1].equals(Constant.SEARCH_UP)) {
                param.addParam("b.time_limit", SearchFilter.Operator.GTE, NumberUtils.getInt(timeLimitArgs[0]));
            } else if (timeLimitArgs[1].equals(Constant.SEARCH_DOWN)) {
                param.addParam("b.time_limit", SearchFilter.Operator.LTE, NumberUtils.getInt(timeLimitArgs[0]));
            } else {
                param.addParam("b.time_limit", SearchFilter.Operator.GTE, NumberUtils.getInt(timeLimitArgs[0])).addParam("b.time_limit", SearchFilter.Operator.LTE, NumberUtils.getInt(timeLimitArgs[1]));
            }
            param.addParam("b.isday", 0);
            param.addParam("b.type", SearchFilter.Operator.NOTEQ, 101);
        }

        PageDataList<Borrow> list = borrowService.findWapBorrowPageListBySql(param);

        List<BorrowVO> bList = new ArrayList<BorrowVO>();
        if (!list.getList().isEmpty()) {
            for (Borrow b : list.getList()) {
                BorrowVO bv = new BorrowVO();
                bv.setBorrowName(b.getName());
                bv.setBorrowLink("/wap/bid_details.html?id=" + b.getId());
                if (b.getType() == Constant.TYPE_EXPERIENCE) {
                    bv.setBorrowType("免费体验");
                } else {
                    bv.setBorrowType("");
                }
                bv.setBorrowApr(b.getApr());
                bv.setBorrowAmount(b.getAccount());
                bv.setBorrowIsDay(b.getIsday());
                bv.setBorrowAddTime(b.getAddtime());
                bv.setTimeLimit(b.getTimeLimit());
                bv.setTimeLimitDay(b.getTimeLimitDay());
                if (b.getIsday() == 1) {
                    bv.setBorrowTimeLimit(b.getTimeLimitDay() + "天");
                } else if (b.getType() == 101) {
                    bv.setBorrowTimeLimit("满标即还");
                } else {
                    bv.setBorrowTimeLimit(b.getTimeLimit() + "个月");
                }
                bv.setBorrowSchedule((b.getAccountYes() / b.getAccount()) * 100);
                bList.add(bv);
            }
        }
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("borrowList", bList);
        super.writeJson(new Json("", dataMap, ""));

        return null;
    }

    /**
     * WAPAPP 获取投标记录
     *
     * @return
     * @throws Exception
     */
    @Action(value = "wapAppDetailTenderForJson")
    public String wapAppDetailTenderForJson() throws Exception {
        logger.info("接口请求-投标记录查询开始!");
        int page = paramInt("page");
        long id = paramLong("borrowid");
        SearchParam param = SearchParam.getInstance().addParam("borrow", new Borrow(id)).addPage(page, 10).addOrder(OrderFilter.OrderType.ASC, "addtime");
        PageDataList data = borrowService.getTenderList(param);
        List<BorrowTender> list = data.getList();
        logger.info("接口请求-投标记录查询： 投标记录个数：" + list.size());
        List<DetailTenderVO> dtvoList = new ArrayList<DetailTenderVO>();
        for (BorrowTender bt : list) {
            DetailTenderVO dtvo = new DetailTenderVO();
            dtvo.setBorrowApr(bt.getBorrow().getApr());
            dtvo.setUserName(StringUtils.hideModdileChar(bt.getUser().getUsername(), 6, 1));
            dtvo.setTenderMoney(bt.getMoney());
            dtvo.setTenderDate(bt.getAddtime());
            dtvoList.add(dtvo);
        }
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("detailTender", dtvoList);
        Json json = new Json("", dataMap, "");
        logger.info("接口请求-投标记录查询结束：" + JSON.toJSONString(json));
        super.writeJson(json);
        return null;
    }

    /**
     * 系统消息
     *
     * @return
     * @throws Exception
     */
    @Action(value = "wapAppMsg")
    public String wapAppMsg() throws Exception {
        response.addHeader("Access-Control-Allow-Origin", "*");
        User sessionUser = this.getSessionUser();
        long userid = sessionUser.getUserId();
        SearchParam param = SearchParam.getInstance().addParam("receiveUser", new User(userid)).addParam("sented", SearchFilter.Operator.NOTEQ, 0).addOrder(OrderFilter.OrderType.ASC, "status").addOrder(OrderFilter.OrderType.DESC, "addtime").addPage(1, 10);
        PageDataList<Message> list = messageService.getMessageBySearchParam(param);
        List<MessageVO> msgList = new ArrayList<MessageVO>();
        if (!list.getList().isEmpty()) {
            for (Message msg : list.getList()) {
                MessageVO msgVo = new MessageVO();
                msgVo.setAddTime(msg.getAddtime());
                msgVo.setContent(msg.getContent());
                msgVo.setIsRead(String.valueOf(msg.getStatus()));
                msgList.add(msgVo);
            }
        }

        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("magList", msgList);
        super.writeJson(new Json("", dataMap, ""));

        // 每进入系统消息一次， 需要将未读消息设置为一度消息
        Integer[] ids = new Integer[1];
        for (Message msg : list.getList()) {
            if (msg.getStatus() == 0) {
                ids[0] = msg.getId();
                messageService.setReadMessage(ids);
            }
        }
        return null;
    }

    /**
     * 正在招标的借款  /  正在还款的借款
     *
     * @return
     */
    @Action(value = "borrowingOrTendering")
    public String borrowingOrTendering() {
        response.addHeader("Access-Control-Allow-Origin", "*");
        User user = getSessionUser();
        // 正在招标项目
        SearchParam param = SearchParam.getInstance()
                .addParam("user", user)
                .addParam("status", 1)
                .addPage(paramInt("page"))
                .addOrder(OrderFilter.OrderType.DESC, "id");
        PageDataList<Borrow> list = borrowService.getList(param);
        List<BorrowingVO> bvList = new ArrayList<BorrowingVO>();
        if (!list.getList().isEmpty()) {
            for (Borrow b : list.getList()) {
                BorrowingVO bv = new BorrowingVO();
                bv.setBorrowName(b.getName());
                bv.setBorrowLink("/wap/bid_details.html?id=" + b.getId());
                bv.setBorrowAccount(b.getAccount());
                bv.setBorrowApr(b.getApr());
                if (b.getIsday() == 1) {
                    bv.setTimeLimit(b.getTimeLimitDay() + "天");
                } else {
                    bv.setTimeLimit(b.getTimeLimit() + "个月");
                }
                if (b.getStatus() == 0) {
                    bv.setStatus("发布审批中");
                } else if (b.getStatus() == 1) {
                    if (b.getAccountYes() == b.getAccount()) {
                        if (b.getType() == 110) {
                            bv.setStatus("满标审核中");
                        } else {
                            bv.setStatus("满标待审");
                        }
                    } else {
                        bv.setStatus("正在募集");
                    }
                } else if (b.getStatus() == 2) {
                    bv.setStatus("审核失败");
                } else if (b.getStatus() == 3) {
                    bv.setStatus("已满标");
                } else if (b.getStatus() == 4) {
                    bv.setStatus("满标审核失败");
                } else if (b.getStatus() == 5) {
                    bv.setStatus("撤回");
                }
                bvList.add(bv);
            }
        }

        // 正在还款的项目
        SearchParam param2 = SearchParam.getInstance()
                .addOrFilter("status", 6, 7)
                .addParam("user", user)
                .addParam("type", SearchFilter.Operator.NOTEQ, Constant.TYPE_FLOW)
                // .addParam("type", Operator.NOTEQ, Constant.TYPE_SECOND)
                .addPage(paramInt("page"))
                .addOrder(OrderFilter.OrderType.DESC, "id");
        PageDataList<Borrow> list2 = borrowService.getList(param2);
        List<BorrowingVO> repaybvList = new ArrayList<BorrowingVO>();

        if (!list2.getList().isEmpty()) {
            for (Borrow b : list2.getList()) {
                BorrowingVO bv = new BorrowingVO();
                bv.setBorrowName(b.getName());
                bv.setBorrowLink("/wap/bid_details.html?id=" + b.getId());
                bv.setBorrowAccount(b.getAccount());
                bv.setBorrowApr(b.getApr());
                if (b.getIsday() == 1) {
                    bv.setTimeLimit(b.getTimeLimitDay() + "天");
                } else {
                    bv.setTimeLimit(b.getTimeLimit() + "个月");
                }
                if (b.getType() == Constant.TYPE_EXPERIENCE) {
                    bv.setRepayTime("--");
                } else {
                    bv.setRepayTime(!b.getBorrowRepayments().isEmpty() && b.getBorrowRepayments().size() > 0 ? DateUtils.dateStr4(b.getBorrowRepayments().get(0).getRepaymentTime()) : "--");
                }
                if (b.getStatus() == 0) {
                    bv.setStatus("发布审批中");
                } else if (b.getStatus() == 1) {
                    if (b.getAccountYes() == b.getAccount()) {
                        if (b.getType() == 110) {
                            bv.setStatus("满标审核中");
                        } else {
                            bv.setStatus("认购已满标");
                        }
                    } else {
                        bv.setStatus("正在募集");
                    }
                } else if (b.getStatus() == 2) {
                    bv.setStatus("审核失败");
                } else if (b.getStatus() == 3) {
                    bv.setStatus("已满标");
                } else if (b.getStatus() == 4) {
                    bv.setStatus("满标审核失败");
                } else if (b.getStatus() == 5) {
                    bv.setStatus("撤回");
                } else if (b.getStatus() == 6 || b.getStatus() == 7) {
                    bv.setStatus("还款中");
                } else if (b.getStatus() == 8) {
                    bv.setStatus("已经还款");
                }
                repaybvList.add(bv);
            }
        }

        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("borrowing", bvList);
        dataMap.put("repaying", repaybvList);

        super.writeJson(new Json("", dataMap, ""));
        return null;
    }

    /**
     * 招标中的项目 / 未收款明细 / 已收款明细
     *
     * @return
     */
    @Action(value = "collecticonDetails")
    public String collecticonDetails() {
        response.addHeader("Access-Control-Allow-Origin", "*");
        User user = getSessionUser();
        // 投标中的项目查询
        SearchParam param = SearchParam.getInstance()
                .addParam("status", 0)
                .addParam("borrow.type", SearchFilter.Operator.NOTEQ, Constant.TYPE_FLOW) //流转标即投即扣
                .addParam("waitAccount", 0)
                .addParam("user", user)
                .addPage(1, 20)
                .addOrder(OrderFilter.OrderType.DESC, "id");
        PageDataList<BorrowTender> list = borrowService.getTenderList(param);

        List<TenderingVO> tvList = new ArrayList<TenderingVO>();
        if (!list.getList().isEmpty()) {
            for (BorrowTender bt : list.getList()) {
                TenderingVO tv = new TenderingVO();
                tv.setBorrowName(bt.getBorrow().getName());
                tv.setBorrowLink("/wap/bid_details.html?id=" + bt.getBorrow().getId());
                tv.setBorrower(bt.getBorrow().getUser().getPhone());
                tv.setTenderMoney(bt.getMoney());
                tv.setTenderDate(bt.getAddtime());
                if (bt.getBorrow().getType() == Constant.TYPE_EXPERIENCE) {
                    if (bt.getStatus() == 0) {
                        tv.setTenderStatus("投标成功，体验金被冻结");
                    } else if (bt.getStatus() == 1) {
                        tv.setTenderStatus("已经还款");
                    }
                } else {
                    if (bt.getStatus() == 0) {
                        tv.setTenderStatus("投标成功，资金被冻结");
                    } else if (bt.getStatus() == 1) {
                        tv.setTenderStatus("已经还款");
                    }
                }
                tvList.add(tv);
            }
        }

        // 未收款明细查询
        SearchParam param2 = SearchParam.getInstance()
                .addParam("status", 0)
                .addParam("borrowTender.user", user)
                .addParam("borrowTender.waitAccount", SearchFilter.Operator.GT, 0)
                .addParam("borrowTender.status", 0)
                .addPage(1, 20)
                .addOrder(OrderFilter.OrderType.ASC, "repayTime");
        PageDataList<BorrowCollection> list2 = borrowService.getCollectList(param2);
        List<CollectVO> nocvList = new ArrayList<CollectVO>();
        if (!list2.getList().isEmpty()) {
            for (BorrowCollection bc : list2.getList()) {
                CollectVO cv = new CollectVO();
                cv.setBorrowName(bc.getBorrowTender().getBorrow().getName());
                cv.setBorrowLink("/wap/bid_details.html?id=" + bc.getBorrowTender().getBorrow().getId());
                cv.setBorrower(bc.getBorrowTender().getBorrow().getUser().getPhone());
                cv.setCapital(bc.getCapital());
                cv.setInterest(bc.getMyInterest());
                cv.setCollectTime(bc.getRepayTime());
                nocvList.add(cv);
            }
        }

        // 已收款明细
        SearchParam param3 = SearchParam.getInstance()
                .addJoin("borrowCollection", BorrowCollection.class)
                .addParam("status", 1)
                .addParam("borrowTender.user", user)
                .addPage(1, 20)
                .addOrder(OrderFilter.OrderType.DESC, "id");
        PageDataList<BorrowCollection> list3 = borrowService.getCollectList(param3);
        List<CollectVO> hascvList = new ArrayList<CollectVO>();
        if (!list3.getList().isEmpty()) {
            for (BorrowCollection bc : list3.getList()) {
                CollectVO cv = new CollectVO();
                cv.setBorrowName(bc.getBorrowTender().getBorrow().getName());
                cv.setBorrowLink("/wap/bid_details.html?id=" + bc.getBorrowTender().getBorrow().getId());
                cv.setBorrower(bc.getBorrowTender().getBorrow().getUser().getPhone());
                cv.setCapital(bc.getCapital());
                if (bc.getMyInterest() != 0) {
                    cv.setInterest(bc.getMyInterest());
                } else {
                    cv.setInterest(bc.getMyTotal() - bc.getCapital());
                }
                cv.setCollectTime(bc.getRepayYestime());
                cv.setCollectStatus(bc.getBorrowRepayType().getName());
                hascvList.add(cv);
            }
        }

        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("tendering", tvList);
        dataMap.put("noCollect", nocvList);
        dataMap.put("hasCollect", hascvList);
        Json json = new Json("", dataMap, "");

        super.writeJson(json);
        return null;
    }

    /**
     * 充值记录
     *
     * @return
     */
    @Action(value = "rechargeRecords")
    public String rechargeRecords() {
        response.addHeader("Access-Control-Allow-Origin", "*");
        User user = getSessionUser();
        SearchParam param = SearchParam.getInstance().addParam("user", user).addPage(1, 20).addOrder(OrderFilter.OrderType.DESC, "addtime");
        PageDataList<AccountRecharge> data = accountService.getRechargeList(param);
        List<RechargeRecordsVO> rrList = new ArrayList<RechargeRecordsVO>();
        if (!data.getList().isEmpty()) {
            for (AccountRecharge ar : data.getList()) {
                RechargeRecordsVO rr = new RechargeRecordsVO();
                rr.setOrderNo(ar.getTradeNo());
                rr.setRechargeType(ar.getType()); // 1 网银充值  2 线下充值   8 汇款充值  其他：无卡代扣充值
                rr.setRechargeAmount(ar.getMoney());
                rr.setRechargeTime(ar.getAddtime());
                if (ar.getStatus() == 0) {
                    rr.setRechargeStatus("充值失败");
                } else if (ar.getStatus() == 1) {
                    rr.setRechargeStatus("充值成功");
                } else if (ar.getStatus() == 2) {
                    rr.setRechargeStatus("充值失败");
                } else if (ar.getStatus() == 8) {
                    rr.setRechargeStatus("第三方审核中");
                }
                rrList.add(rr);
            }
        }
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("rechargeRecords", rrList);
        super.writeJson(new Json("", dataMap, ""));

        return null;
    }

    /**
     * 提现记录
     *
     * @return
     * @throws Exception
     */
    @Action(value = "cashRecords"/*, interceptorRefs = { @InterceptorRef(value = "mydefault") }*/)
    public String cashRecords() throws Exception {
        response.addHeader("Access-Control-Allow-Origin", "*");
        User user = getSessionUser();
        SearchParam param = SearchParam.getInstance().addParam("user", user).addParam("status", SearchFilter.Operator.NOTEQ, 4).addOrder(OrderFilter.OrderType.DESC, "addtime").addPage(paramInt("page"));
        PageDataList<AccountCash> data = accountService.getCashList(param);
        List<CashRecordsVO> crList = new ArrayList<CashRecordsVO>();
        if (!data.getList().isEmpty()) {
            for (AccountCash ac : data.getList()) {
                CashRecordsVO cr = new CashRecordsVO();
                cr.setCashCardNo(ac.getAccountBank().getAccount());
                cr.setCashAmount(ac.getTotal());
                cr.setCashTime(ac.getAddtime());
                if (ac.getStatus() == 0) {
                    cr.setCashStatus("审核中");
                } else if (ac.getStatus() == 1) {
                    cr.setCashStatus("提现成功");
                } else if (ac.getStatus() == 2) {
                    cr.setCashStatus("用户取消");
                } else if (ac.getStatus() == 3) {
                    cr.setCashStatus("提现失败");
                }
                crList.add(cr);
            }
        }
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("cashRecords", crList);
        super.writeJson(new Json("", dataMap, ""));
        return null;
    }

    /**
     * 资金记录
     *
     * @return
     * @throws Exception
     */
    @Action(value = "accountLog"/*, interceptorRefs = {@InterceptorRef(value = "mydefault")}*/)
    public String log() throws Exception {
        response.addHeader("Access-Control-Allow-Origin", "*");
        User user = getSessionUser();
        SearchParam param = SearchParam.getInstance().addParam("user.userId", user.getUserId()).addOrder(OrderFilter.OrderType.DESC, "id").addPage(paramInt("page"));

        PageDataList<AccountLog> data = accountService.getAccontLogList(param);
        List<AccountLogVO> alvList = new ArrayList<AccountLogVO>();
        if (!data.getList().isEmpty()) {
            for (AccountLog al : data.getList()) {
                AccountLogVO alv = new AccountLogVO();
                alv.setType(AccountLogTypeName.getInstance().typeNameMap.get(al.getType()));
                alv.setAccount(al.getMoney());
                alv.setTotalAccount(al.getTotal());
                alv.setUseAccount(al.getUseMoney());
                alv.setNoUseAccount(al.getNoUseMoney());
                alv.setCollectAccount(al.getCollection());
                alv.setRepayAccount(al.getRepay());

                if (al.getType().equals("recharge") || al.getType().contains("cash_")) {
                    alv.setUserName("--");
                } else if (al.getToUser().getUserId() == 1) {
                    alv.setUserName("系统");
                } else {
                    alv.setUserName(al.getToUser().getPhone());
                }

                alv.setAddTime(al.getAddtime());
                alv.setRemark(al.getRemark());
                alvList.add(alv);
            }
        }
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("accountLogs", alvList);
        super.writeJson(new Json("", dataMap, ""));
        return null;
    }

    /**
     * 我的银行卡
     *
     * @return
     * @throws Exception
     */
    @Action(value = "bankcancel"/*, interceptorRefs = {@InterceptorRef(value = "mydefault")}*/)
    public String bankcancel() throws Exception {
        response.addHeader("Access-Control-Allow-Origin", "*");
        User sessionUser = getSessionUser();
        long user_id = sessionUser.getUserId();
        User user = userService.getUserById(user_id);

        List<AccountBank> bankList = accountService.getAccountBankList(SearchParam.getInstance().addParam("user", new User(user.getUserId())).addParam("status", 1)).getList();
        List<AccountBankVO> abList = new ArrayList<AccountBankVO>();
        if (!bankList.isEmpty()) {
            for (AccountBank ab : bankList) {
                AccountBankVO abvo = new AccountBankVO();
                abvo.setBankname(ab.getBankMmm().getBankName());
                abvo.setCardNumber(ab.getAccount());
                abvo.setAddressCreate(ab.getMmmprovince().getName() + ", " + ab.getMmmcity().getName());
                abvo.setTimeCreate(ab.getAddtime());
                abvo.setPhonenumber(user.getPhone());
                abvo.setUserid(String.valueOf(user.getUserId()));
                abvo.setBankid(String.valueOf(ab.getId()));
                abList.add(abvo);
            }
        }
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("accountBanks", abList);
        super.writeJson(new Json("", dataMap, ""));
        return null;
    }
}