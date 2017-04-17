package com.liangyou.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.domain.Prize;
import com.liangyou.domain.PrizeDetail;
import com.liangyou.domain.PrizeUserRelationship;
import com.liangyou.domain.User;
import com.liangyou.json.Json;
import com.liangyou.model.LotteryRecordsVO;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.prize.MyPrize;
import com.liangyou.service.LotteryService;
import com.liangyou.service.PrizeDetailService;
import com.liangyou.service.PrizeUserRelationshipService;
import com.liangyou.service.UserService;
import com.liangyou.tool.Page;
import com.liangyou.util.NumberUtils;

@Namespace("/lottery")
@ParentPackage("p2p-default")
public class LotteryAction extends BaseAction {

	private static final Logger logger = Logger.getLogger(LotteryAction.class);

	@Autowired
	private LotteryService lotteryService;
	@Autowired
	private PrizeUserRelationshipService prizeUserRelationshipService;
	@Autowired
	private UserService userService;
	@Autowired
	private PrizeDetailService prizeDetailService;

	/**
	 * 加载抽奖页面
	 * 
	 * @return
	 */
	@Action(value = "/lottery", results = { @Result(name = "success", type = "ftl", location = "/lottery/index.html") })
	public String lottery() {

		List<LotteryRecordsVO> lrvoList = new ArrayList<LotteryRecordsVO>();
		for (int i = 0; i < 30; i++) {
			LotteryRecordsVO lrvo = new LotteryRecordsVO();
			lrvo.setPhone(getTel());
			lrvo.setDetail(winInformation[getNum(0, winInformation.length - 1)]);
			lrvoList.add(lrvo);
		}

		request.setAttribute("lrvoList", lrvoList);

		return SUCCESS;
	}

	private static String[] telFirst = "134,135,136,137,138,139,150,151,152,157,158,159,130,131,132,155,156,133,153".split(",");
	private static String[] winInformation = " 抢到了唱吧直播间VIP券！手气太棒了！, 抢到了美的电饭煲！真幸运啊！, 抢到了360行车记录仪！人品棒棒哒！, 抢到了黑糖姜茶！, 抢到了50元京东卡！手气太棒了！, 抢到了美的电饭煲！真幸运啊！, 抢到了两张薰衣草庄园门票！太幸福了！, 抢到了不粘锅系列煎盘！可以大显身手了！, 抢到了360行车记录仪！人品棒棒哒！, 抢到了唱吧直播间VIP券！手气太棒了！, 抢到了黑糖姜茶！, 抢到了美的电饭煲！真幸运啊！, 抢到了50元京东卡！手气太棒了！, 抢到了美的电饭煲！真幸运啊！, 抢到了两张电影票！手气太棒了！, 抢到了50元京东卡！手气太棒了！, 抢到了唱吧直播间VIP券！手气太棒了！, 抢到了360行车记录仪！人品棒棒哒！, 抢到了黑糖姜茶！, 抢到了360行车记录仪！人品棒棒哒！, 抢到了唱吧直播间VIP券！手气太棒了！, 抢到了美的电饭煲！真幸运啊！".split(",");

	// 获取两个数区间的随机数
	public static int getNum(int start, int end) {
		return (int) (Math.random() * (end - start + 1) + start);
	}

	// 获取随机手机号
	private static String getTel() {
		int index = getNum(0, telFirst.length - 1);
		String first = telFirst[index];
		// String second = String.valueOf(getNum(1, 888) + 10000).substring(1);
		String third = String.valueOf(getNum(1, 9100) + 10000).substring(1);
		return first + "****" + third;
	}

	/**
	 * 进行抽奖
	 * 
	 * @return
	 */
	@Action(value = "/doLottery")
	public String doLottery() {
		List<Prize> prizes = new ArrayList<Prize>();

		SearchParam param = new SearchParam();
		param.addParam("status", 1);
		prizes = this.lotteryService.findByParam(param);

		// 如果奖品份数为0，概率设置为0
		if (!prizes.isEmpty()) {
			for (Prize prize : prizes) {
				if (prize.getQuantity() == 0) {
					prize.setProbability(0);
					this.lotteryService.updatePrize(prize);
				}
			}
		}

		List<Double> orignalRates = new ArrayList<Double>(prizes.size());
		for (Prize prize : prizes) {
			double probability = prize.getProbability();
			if (probability < 0) {
				probability = 0;
			}
			orignalRates.add(probability);
		}

		int index = this.lotteryService.doLottery(orignalRates);
		Prize tuple = prizes.get(index);
		logger.info("抽奖的结果为：" + tuple.toString());

		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("prizeId", tuple.getPrizeId());
		Json json = new Json("获取抽奖结果成功！", dataMap, "");
		super.writeJson(json);

		return null;
	}

	/**
	 * 点击领取按钮 保存临时手机号与奖品的关系
	 * 
	 * @return
	 */
	@Action(value = "/receivePrize")
	public String receivePrize() {
		Json json = new Json();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		String phone = paramString("phone");
		String prizeId = paramString("prizeId");
		SearchParam param = new SearchParam();
		param.addParam("prizeId", prizeId);
		List<Prize> prizes = this.lotteryService.findByParam(param);
		Prize prize = new Prize();
		if (!prizes.isEmpty()) {
			prize = prizes.get(0);
		}

		PrizeUserRelationship pur = new PrizeUserRelationship();
		pur.setPhone(phone);
		pur.setPrize(prize);

		this.prizeUserRelationshipService.save(pur);

		User user = getSessionUser();
		// 如果是当前已经登录的用户
		if (user != null && user.getPhone().equals(phone)) {
			dataMap.put("redirectURL", "/user/award.html?interfaceFlag=0&userId=" + user.getUserId() + "&purId=" + pur.getId());
			json = new Json(2, true, "当前已经登录的用户，领取奖品成功！", dataMap, "");
		} else {
			SearchParam pms = new SearchParam();
			pms.addParam("phone", phone);
			pms.addParam("islock", 0);
			List<User> uList = this.userService.getUserList(pms);
			if (!uList.isEmpty()) {
				dataMap.put("redirectURL", "/user/login.html?phone=" + phone + "&flag=lottey");
			} else {
				dataMap.put("redirectURL", "/user/register.html?phone=" + phone);
			}
			json = new Json("登录或注册页面跳转！", dataMap, "");
		}
		super.writeJson(json);
		return null;
	}

	/**
	 * 加载我的奖品页面
	 * 
	 * @return
	 */
	@Action(value = "/myPirze", results = { @Result(name = "success", type = "ftl", location = "/lottery/myPirze.html") })
	public String myPirze() {
		User user = getSessionUser();
//		List<MyPrize> prizes = lotteryService.getPrizesByUserId(user.getUserId());

		List<MyPrize> myPrizes = new ArrayList<MyPrize>();
		SearchParam param = new SearchParam();
		param.addParam("user", user);
		param.addParam("receiveState", 1);
		List<PrizeUserRelationship> purList = this.prizeUserRelationshipService.findByParam(param);
		if (!purList.isEmpty())
			for (PrizeUserRelationship pur : purList) {
				MyPrize mp = new MyPrize();
				mp.setPrizeName(pur.getPrize().getPrizeName());
				mp.setPrizeStatus(String.valueOf(pur.getStatus()));
				mp.setPrizeImgUrl(pur.getPrize().getImgURL());
				mp.setPrizeTime(pur.getRealReceiveTime());

				SearchParam params = new SearchParam();
				params.addParam("prizeUser", pur);
				params.addParam("prize", pur.getPrize());
				//params.addParam("status", 1);业务修改不需要区分状态
				List<PrizeDetail> pdList = this.prizeDetailService.findByParam(params);
				logger.info("查询是否有兑换码：" + pdList.size());
				if (!pdList.isEmpty()) {
					mp.setExchangeCode(pdList.get(0).getDetail());
				} else {
					mp.setExchangeCode("");
				}
				myPrizes.add(mp);

			}
		int currentPage = paramInt("page") == 0 ? 1 : paramInt("page");
		int pernum = paramInt("pageNum") == 0 ? Page.ROWS
				: paramInt("pageNum");
		int total = myPrizes.size();
		Page page = new Page(total, currentPage, pernum);
		PageDataList<MyPrize> pageDataList = new PageDataList<MyPrize>(page, myPrizes);
		String string = request.getRequestURL().toString();
		setPageAttribute(pageDataList, param);
		return SUCCESS;
	}
	
	/**
	 * 加载我的奖励页面
	 * 
	 * @return
	 */
	@Action(value = "/myrewardMoney", results = { @Result(name = "success", type = "ftl", location = "/lottery/myrewardMoney.html") })
	public String myrewardMoney() {
		return SUCCESS;
	}

	/**
	 * 获取我的奖品的详细信息
	 * 
	 * @return
	 */
	@Action(value = "/getMyPirzeDetail")
	public String getMyPirzeDetail() {
		Json json = new Json();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		User user = getSessionUser();
//		List<MyPrize> prizes = lotteryService.getPrizesByUserId(user.getUserId());

		List<MyPrize> myPrizes = new ArrayList<MyPrize>();
		SearchParam param = new SearchParam();
		param.addParam("user", user);
		param.addParam("receiveState", 1);
		List<PrizeUserRelationship> purList = this.prizeUserRelationshipService.findByParam(param);
		if (!purList.isEmpty())
			for (PrizeUserRelationship pur : purList) {
				MyPrize mp = new MyPrize();
				mp.setPrizeName(pur.getPrize().getPrizeName());
				mp.setPrizeStatus(String.valueOf(pur.getStatus()));
				mp.setPrizeImgUrl(pur.getPrize().getImgURL());
				mp.setPrizeTime(pur.getRealReceiveTime());

				SearchParam params = new SearchParam();
				params.addParam("prizeUser", pur);
				params.addParam("prize", pur.getPrize());
				//params.addParam("status", 1);业务修改不需要区分状态
				List<PrizeDetail> pdList = this.prizeDetailService.findByParam(params);
				logger.info("查询是否有兑换码：" + pdList.size());
				if (!pdList.isEmpty()) {
					mp.setExchangeCode(pdList.get(0).getDetail());
				} else {
					mp.setExchangeCode("");
				}
				myPrizes.add(mp);

			}
		int currentPage = paramInt("page") == 0 ? 1 : paramInt("page");
		int pernum = paramInt("pageNum") == 0 ? Page.ROWS
				: paramInt("pageNum");
		int total = myPrizes.size();
		Page page = new Page(total, currentPage, pernum);
		PageDataList<MyPrize> pageDataList = new PageDataList<MyPrize>(page, myPrizes);
		String string = request.getRequestURL().toString();
		setPageAttribute(pageDataList, param);
		dataMap.put("prizes", myPrizes);
		json = new Json("", dataMap, "");
		super.writeJson(json);
		return null;
	}
}
