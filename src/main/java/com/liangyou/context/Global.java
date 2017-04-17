package com.liangyou.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.liangyou.domain.BorrowConfig;
import com.liangyou.domain.CreditRank;
import com.liangyou.domain.StarRank;
import com.liangyou.model.SystemInfo;
import com.liangyou.service.impl.UserServiceImpl;
import com.liangyou.tool.Utils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;

/**
 * @author fuxingxing
 * @version <b>Copyright (c)</b> 2012-融都rongdu-版权所有<br/>
 * @date 2012-8-29 下午4:28:22
 */
public class Global {

	private static Logger logger = Logger.getLogger(UserServiceImpl.class);

	public static SystemInfo SYSTEMINFO;
	public static Map BORROWCONFIG;
	public static List ALLKEFU;
	public static List<CreditRank> ALLCREDITRANK;
	public static List<StarRank> ALLSTARRANK;

	public static Set TENDER_SET = Collections.synchronizedSet(new HashSet());
	public static Map RESULT_MAP = Collections.synchronizedMap(new HashMap<String, String>());
	public static final String TABLE_PREFIX = "";

	// v1.8.0.4 TGPROJECT-25 qj 2014-04-03 start
	public static String[] SYSTEMNAME = new String[] { "webname", "meta_keywords", "meta_description", "beian", "copyright", "fuwutel", "address", "weburl", "vip_fee", "most_cash", "theme_dir", "api_name", "api_code", "version" };
	// v1.8.0.4 TGPROJECT-25 qj 2014-04-03 start

	public static ThreadLocal ipThreadLocal = new ThreadLocal();
	public static ThreadLocal perviewBorrowThreadLocal = new ThreadLocal();
	public static ThreadLocal paramLocal = new ThreadLocal();
	// v1.8.0.4 TGPROJECT-111 lx 2014-04-21 start
	public static String VERSION = "v1.8.0.4_u1";

	public static String getVersion() {
		return Global.getString("version");
	}

	// v1.8.0.4 TGPROJECT-111 lx 2014-04-21 end
	public static BorrowConfig getBorrowConfig(int borrowType) {
		BorrowConfig config = null;
		if (BORROWCONFIG == null) {
			return null;
		}
		Object obj = BORROWCONFIG.get(borrowType);
		if (obj == null)
			return null;
		config = (BorrowConfig) obj;
		return config;
	}

	public static String getValue(String key) {
		Object o = null;
		if (SYSTEMINFO != null) {
			o = SYSTEMINFO.getValue(key);
		}
		if (o == null) {
			return "";
		}
		return o.toString();
	}

	public static String getString(String key) {
		String s = StringUtils.isNull(getValue(key));
		return s;
	}

	public static int getInt(String key) {
		int i = NumberUtils.getInt(getValue(key));
		return i;
	}

	public static double getDouble(String key) {
		double i = NumberUtils.getDouble(getValue(key));
		return i;
	}
	public static boolean getBoolean(String key) {
		String value = getValue(key);
		if("true".equals(value)){
			return true;
		}else {
			return false;
		}
	}
	public static String getBorrowTypeName(int type) {
		switch (type) {
		case 100:
			return "month";// default is month
		case 101:
			return "miaobiao";// second
		case 102:
			return "month"; // credit
		case 103:
			return "fast";// mortgage
		case 104:
			return "jin";// property
		case 105:
			return "vouch";
		case 106:
			return "art";
		case 107:
			return "charity";
		case 108:
			return "labor";
		case 109:
			return "netbussiness";
		case 110:
			return "flow";
		case 111:
			return "student";// 学业信用标
		case 112:
			return "shopbusiness";// 经营信用标
		case 114:
			return "assignment";// 债权转让标
		case 115:
			return "experience";// 体验标 add by gy 2016-10-14 10:16:22
		default:
			return "month";
		}
	}

	public static int getBorrowType(String type) {
		if (type.equals("month") || type.equals("")) {
			return 102;
		} else if (type.equals("miaobiao")) {
			return 101;
		} else if (type.equals("xin")) {
			return 102;
		} else if (type.equals("fast")) {
			return 103;
		} else if (type.equals("jin")) {
			return 104;
		} else if (type.equals("vouch")) {// 担保标
			return 105;
		} else if (type.equals("charity")) {// 慈善标，公益标
			return 107;
		} else if (type.equals("labor")) {// 工薪信用标
			return 108;
		} else if (type.equals("netbussiness")) {// 网商信用标
			return 109;
		} else if (type.equals("flow")) {
			return 110;
		} else if (type.equals("student")) {// 学业信用标
			return 111;
		} else if (type.equals("shopbusiness")) {// 经营信用标
			return 112;
		} else if (type.equals("experience")) { // add by gy 2016-10-13 18:14:48 体验标
			return 115;
		} else {
			return 102;
		}
	}

	public static double getCash(double x, double r, double money, double maxCash) {
		String site_id = StringUtils.isNull(Global.getValue("webid"));
		double fee = 0.0;
		if (site_id.equals("zrzb") || site_id.equals("zdvci") || site_id.equals("xdcf") || site_id.equals("lhcf")) {
			fee = Utils.getCashFeeForZrzbZero(x, r, money);
		} else if (site_id.equals("huidai")) {
			fee = Utils.getCashFeeForHuidai(x, money, r);
		} else if (site_id.equals("lhdai")) {
			fee = Utils.getCashFeeForlhd(x, money, r, maxCash);
		} else if (site_id.equals("ssjb")) {
			fee = Utils.getCashFeeForSSJB(x, money, r, maxCash);
		} else {
			fee = Utils.getCashFeeForZRZB(x, money, r, maxCash);
		}
		return fee;
	}

	public static double getCashForWzdai(double use_money, double ownmoney, double x, double r, double money, double maxCash) {
		String site_id = StringUtils.isNull(Global.getValue("webid"));
		double fee = 0.0;
		if (site_id.equals("jsy")) {
			fee = Utils.getCashFeeForJJY(x, r, money, maxCash);
		} else {
			if (use_money >= 200000 && ownmoney >= 200000 && x >= 200000) {
				fee = Utils.GetLargeCashFee(x, r, money, maxCash);
			} else {
				fee = Utils.GetCashFee(x, r, money, maxCash);
			}
		}
		return fee;
	}

	public static String getWebid() {
		return StringUtils.isNull(Global.getValue("webid"));
	}

	public static String getIP() {
		Object retObj = Global.ipThreadLocal.get();
		logger.debug("Set Ip:" + retObj);
		return retObj == null ? "" : retObj.toString();
	}

	public static Object getPerviewBorrow() {
		Object retObj = Global.perviewBorrowThreadLocal.get();
		logger.debug("Set perviewBorrow:" + retObj);
		return retObj == null ? null : retObj;
	}
}
