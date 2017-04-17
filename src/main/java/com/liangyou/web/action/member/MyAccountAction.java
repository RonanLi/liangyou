package com.liangyou.web.action.member;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.liangyou.dao.BorrowCollectionDao;
import com.liangyou.domain.*;
import com.liangyou.service.*;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.liangyou.api.moneymoremore.MmmCash;
import com.liangyou.api.moneymoremore.MmmHelper;
import com.liangyou.api.moneymoremore.MmmLoanAuthorize;
import com.liangyou.api.moneymoremore.MmmRecharge;
import com.liangyou.context.Constant;
import com.liangyou.context.Global;
import com.liangyou.exception.BussinessException;
import com.liangyou.json.Json;
import com.liangyou.model.OrderFilter;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchFilter;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.model.SearchParam;
import com.liangyou.model.TempIdentifyUser;
import com.liangyou.model.account.UserAccountSummary;
import com.liangyou.model.prize.MyPrize;
import com.liangyou.model.prize.MyPrizePlus;
import com.liangyou.tool.Page;
import com.liangyou.tool.coder.MD5;
import com.liangyou.tool.jxl.ExcelHelper;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.OrderNoUtils;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.BaseAction;
import com.opensymphony.xwork2.ModelDriven;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Namespace("/member/myAccount")
@ParentPackage("p2p-default")
@Results({
	@Result(name = "success", type = "ftl", location = "/index.html"),
	@Result(name = "myAccount", type = "ftl", location = "/myAccount/index.html")

})
@InterceptorRefs({ @InterceptorRef("mydefault") })
public class MyAccountAction extends BaseAction implements ModelDriven<AccountRecharge>{

	private static Logger logger = Logger.getLogger(MyAccountAction.class);

	AccountRecharge accountRecharge = new AccountRecharge();

    // imgurl action params
    private String userid;
    private String size;
    // interest action params
    private int time_limit_day;

    private File upload;
    private String uploadFileName;
    private String sep = File.separator;

    // 裁剪后的图像大小
    private double cropX;
    private double cropY;
    private double cropW;
    private double cropH;

    private String plugintype;

	@Autowired
	private BorrowService borrowService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private RepaymentService repaymentService;
	@Autowired
	private PrizeUserRelationshipService prizeUserRelationshipService;
	@Autowired
	private PrizeDetailService prizeDetailService;
	@Autowired
	private RuleService ruleService;
	@Autowired
	private MessageService messageService;
	@Autowired
	private ExperienceMoneyService experienceMoneyService;
	@Autowired
	private ApiService apiService;
    @Autowired
    private UserService userService;
    private Message message = new Message();
    @Autowired
    private UserinfoService userinfoService;
    @Autowired
    private BorrowCollectionDao borrowCollectionDao;
  
    	
	@Override
	public AccountRecharge getModel() {
		return accountRecharge;
	}

    /*我账户默认页*/
	@SuppressWarnings("unchecked")
	@Action(value="index",results={
			@Result(name="success", type="ftl",location="/myAccount/index.html"),
			@Result(name="login", type="redirect",location="/user/login.html")
			})
	public String index() throws Exception {
		User user = getSessionUser();
		// 招标中的项目
		SearchParam param=SearchParam.getInstance()
				.addParam("status", 0)
				.addParam("borrowTender.user", user)
				.addParam("borrowTender.waitAccount", SearchFilter.Operator.GT, 0 )
				.addParam("borrowTender.status", 0)
				.addPage(1, 3)
				.addOrder(OrderFilter.OrderType.ASC, "repayTime");
		PageDataList<BorrowCollection> list=borrowService.getCollectList(param);
		request.setAttribute("collectList", list.getList());

		// 资金记录
		SearchParam param2 = SearchParam.getInstance()
				.addParam("user.userId", user.getUserId())
				.addOrder(OrderFilter.OrderType.DESC, "id")
				.addPage(1, 3);
		PageDataList<AccountLog> data = accountService.getAccontLogList(param2);
		request.setAttribute("accountLogList", data.getList());

		// 账户资金详情
		Account account = user.getAccount();
		UserAccountSummary summary = accountService.getUserAccountSummary(user.getUserId());
		request.setAttribute("user", user);
		request.setAttribute("account", account);
		request.setAttribute("summary", summary);

		return SUCCESS;
	}

/* 更换头像 START*/
	@Action(value = "avatar", results = { @Result(name = "success", type = "ftl", location = "/myAccount/personalCenter/avatar.html") })
	public String avatar() throws Exception {
        return SUCCESS;
	}


    @SuppressWarnings("rawtypes")
	@Action(value = "upload")
    public String upload() throws Exception {
        Map map = ServletActionContext.getContext().getSession();
        HttpServletResponse response = ServletActionContext.getResponse();
        HttpServletRequest request = ServletActionContext.getRequest();
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String retMsg = "";
        User user = (User) map.get(Constant.SESSION_USER);
        logger.info("文件：" + this.upload);
        logger.info("文件名：" + uploadFileName);
        String destfilename2 = ServletActionContext.getServletContext().getRealPath("/data") + sep + "temp" + sep + user.getUserId() + ".jpg";
        logger.info(destfilename2);
        File imageFile2 = null;
        try {

            imageFile2 = new File(destfilename2);
            long a = upload.length();
            if (a > 1048576) {
                retMsg = getRetMsg(1, "你上传的图片大于1M，请更换成小于1M的图片！", "");
                out.print(retMsg);
                out.flush();
                out.close();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        FileUtils.copyFile(upload, imageFile2);
        try {

            BufferedImage bi = ImageIO.read(imageFile2);
            if (bi == null) {
                logger.info("你输入的文件为无效图片，即图片文件不合法");
                retMsg = getRetMsg(1, "你输入的文件为无效图片，即图片文件不合法！", "");
                out.print(retMsg);
                out.flush();
                out.close();
                return null;
            } else {
                logger.info("你输入的文件为有效图片");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 保存上传的临时图片名称
        String src = request.getContextPath() + "/data/temp/" + user.getUserId() + ".jpg";
        BufferedImage srcImage = null;
        BufferedImage destImage = null;
        File file = new File(destfilename2);
        int newWi = 0;
        int newHi = 0;
        try {
            srcImage = ImageIO.read(file);
            int w = srcImage.getWidth();
            int h = srcImage.getHeight();
            int minW = (w > h) ? w : h;
            double newWd = (300.0 / minW) * w;
            double newHd = (300.0 / minW) * h;
            newWi = (int) newWd;
            newHi = (int) newHd;
            destImage = new BufferedImage(newWi, newHi, BufferedImage.TYPE_3BYTE_BGR);
            destImage.getGraphics().drawImage(srcImage.getScaledInstance(newWi, newHi, Image.SCALE_SMOOTH), 0, 0, null);
            ImageIO.write(destImage, "jpg", new File(destfilename2));
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("Print img:" + src);
        // out.print(src);
        out.print("{");
        out.print("error: '',\n");
        out.print("msg: '" + src + "',\n");
        out.print("width: " + newWi + ",\n");
        out.print("height: " + newHi + "\n");
        out.print("}");
        out.flush();
        out.close();
        return null;
    }
    // 保存头像
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Action("saveAvatar")
    public String saveAvatar() throws Exception {
        try {
            HttpServletResponse response = ServletActionContext.getResponse();
            response.setContentType("application/json;charset=UTF-8");
            int x = 0, y = 0, w = 0, h = 0;
            if (cropX >= 0) {
                x = (int) cropX;
            }
            if (cropY >= 0) {
                y = (int) cropY;
            }
            if (cropW >= 0) {
                w = (int) cropW;
            }
            if (cropH >= 0) {
                h = (int) cropH;
            }

            logger.debug("X=" + x + ",Y=" + y + ",W=" + w + ",H=" + h);
            boolean re = operateImg(x, y, w, h);
            Map map = new HashMap();
            if (re) {
                map.put("flag", 1);
                map.put("msg", "success");
                map.put("useravatar", "");
                String json = JSON.toJSONString(map, true);
                printJson(json);
            } else {
                map.put("flag", 0);
                map.put("msg", "success");
                map.put("useravatar", "");
                String json = JSON.toJSONString(map, true);
                printJson(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
	private boolean operateImg(int x, int y, int w, int h) throws IOException {
        Map map = ServletActionContext.getContext().getSession();
        User user = (User) map.get(Constant.SESSION_USER);
        String dataPath = ServletActionContext.getServletContext().getRealPath("/data");
        String avatarDir = dataPath + sep + "avatar";
        String dest = avatarDir + sep + user.getUserId() + "_avatar_middle.jpg";
        String smalldest = avatarDir + sep + user.getUserId() + "_avatar_small.jpg";
        String src = ServletActionContext.getServletContext().getRealPath("/data") + sep + "temp" + sep + user.getUserId() + ".jpg";
        BufferedImage srcImage = null;
        try {
            srcImage = ImageIO.read(new File(src));
            BufferedImage destImage = srcImage.getSubimage(x, y, w, h);
            BufferedImage lastImage = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
            lastImage.getGraphics().drawImage(destImage, 0, 0, null);
            File avatarDirFile = new File(avatarDir);
            if (!avatarDirFile.exists()) {
                avatarDirFile.mkdir();
            }
            File newFile = new File(dest);
            File smallNewFile = new File(smalldest);
            logger.info("Avatar dest:" + dest);
            ImageIO.write(lastImage, "jpg", newFile);
            ImageIO.write(lastImage, "jpg", smallNewFile);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    // 生成需要裁剪的照片
    @SuppressWarnings("rawtypes")
	@Action("cropimg")
    public String cropimg() throws Exception {
        try {
            Map map = ServletActionContext.getContext().getSession();
            User user = (User) map.get(Constant.SESSION_USER);
            String destfilename = ServletActionContext.getServletContext().getRealPath("/data") + sep + "temp" + sep + user.getUserId() + ".jpg";
            logger.info(destfilename);
            this.cteateImg(destfilename);
        } catch (Exception e) {
            logger.info(e.getMessage());
            logger.error(e);
        }
        return null;
    }
    @Action("imgurl")
    public String imgurl() throws Exception {
        if (NumberUtils.getInt(userid) < 1) {
            userid = NumberUtils.getInt(userid) + "";
        }
        userid = (userid == null || userid.equals("")) ? "" : userid;
        size = (size == null || size.equals("")) ? "" : size;
        String[] sizes = { "big", "middle", "small" };
        List<String> sizelist = Arrays.asList(sizes);
        size = sizelist.contains(size) ? size : "big";

        String url = "data" + sep + "avatar" + sep + userid + "_avatar_" + size + ".jpg";
        String contextPath = ServletActionContext.getServletContext().getRealPath("/");
        url = contextPath + url;
        File avatarFile = new File(url);
        if (!avatarFile.exists()) {
            url = contextPath + sep + "data" + sep + "images" + sep + "avatar" + sep + "noavatar_" + size + ".gif";
        }
        logger.debug("IMG_URL:" + url);
        cteateImg(url);
        return null;
    }

    @Action("editorUploadImg")
    public String editorUploadImg() throws Exception {
        String newFileName = generateUploadFilename();
        String retMsg = "";
        if (upload == null) {
            retMsg = getRetMsg(1, "上传图片成功！", "");
        }
        if (StringUtils.isBlank(retMsg)) {
            String imgUrl = upload(upload, uploadFileName, "/data/upload", newFileName + ".jpg");
            retMsg = getRetMsg(0, "上传图片成功！", imgUrl);
        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(retMsg);
        out.flush();
        out.close();
        return null;
    }

    /**
     * 以图片流形式输出
     *
     * @param url
     * @throws IOException
     */

    private void cteateImg(String url) throws IOException {
        HttpServletResponse res = ServletActionContext.getResponse();
        res.setHeader("Pragma", "No-cache");
        res.setHeader("Cache-Control", "no-cache");
        res.setDateHeader("Expires", 0);
        OutputStream output = res.getOutputStream();// 得到输出流
        if (url.toLowerCase().endsWith(".jpg")) {
            // 表明生成的响应是图片
            res.setContentType("image/jpeg");
        } else if (url.toLowerCase().endsWith(".gif")) {
            res.setContentType("image/gif");
        }
        InputStream imageIn = new FileInputStream(new File(url));
        BufferedInputStream bis = new BufferedInputStream(imageIn);// 输入缓冲流
        BufferedOutputStream bos = new BufferedOutputStream(output);// 输出缓冲流
        byte data[] = new byte[4096];// 缓冲字节数
        int size = 0;
        size = bis.read(data);
        while (size != -1) {
            bos.write(data, 0, size);
            size = bis.read(data);
        }
        bis.close();
        bos.flush();// 清空输出缓冲流
        bos.close();
        output.flush();
        output.close();
    }

    private String getRetMsg(int flag, String msg, String imgUrl) {
        JSONObject obj = new JSONObject();
        obj.put("error", flag);
        obj.put("message", msg);
        obj.put("url", request.getContextPath() + imgUrl);
        return obj.toString();
    }

    public File getUpload() { return upload; }

    public void setUpload(File upload) { this.upload = upload; }

    public String getUploadFileName() { return uploadFileName; }

    public void setUploadFileName(String uploadFileName) { this.uploadFileName = uploadFileName; }

    public double getCropX() { return cropX; }

    public void setCropX(double cropX) { this.cropX = cropX; }

    public double getCropY() { return cropY; }

    public void setCropY(double cropY) { this.cropY = cropY; }

    public double getCropW() { return cropW; }

    public void setCropW(double cropW) { this.cropW = cropW; }

    public double getCropH() { return cropH; }

    public void setCropH(double cropH) { this.cropH = cropH; }

    public String getPlugintype() { return plugintype; }

    public void setPlugintype(String plugintype) { this.plugintype = plugintype; }

    public int getTime_limit_day() { return time_limit_day; }

    public void setTime_limit_day(int time_limit_day) { this.time_limit_day = time_limit_day; }

    public String getUserid() { return userid; }
/*更换头像 END*/

    /*手机认证*/
    @Action(value="phoneActive",results={
            @Result(name="success", type="ftl",location="/myAccount/personalCenter/phoneActive.html"),
            @Result(name="login", type="redirect",location="/user/login.html")
    })
    public String phoneActive() throws Exception {
        String phone = StringUtils.isNull(request.getParameter("phone"));
        String phoneUrl = "/member/myAccount/phoneActive.html";
        User user = getSessionUser();
        user = userService.getUserById(user.getUserId());
        String errormsg = "";
        if (isBlank()) {
            session.put(Constant.SESSION_USER, user);
            return SUCCESS;
        }
        int phonelStatus = user.getPhoneStatus();
        if (phonelStatus == 1 || phonelStatus == 2) {
            message("手机认证已经审核成功或正在审核中！", "");
            return MSG;
        }
        if (phone == null || phone.equals("")) {
            errormsg = "手机号码不能为空!";
            message(errormsg, phoneUrl);
            return MSG;
        } else {
            if (!StringUtils.isMobile(StringUtils.isNull(phone))) {
                errormsg = "手机号码格式不对!";
                message(errormsg, phoneUrl);
                return MSG;
            }
            user.setPhone(phone);
            user.setPhoneStatus(2);
            userService.updateUser(user);
            User tempUser = userService.getUserById(user.getUserId());
            if (tempUser != null) {
                session.put(Constant.SESSION_USER, tempUser);
                request.setAttribute("user", tempUser);
            }
        }
        errormsg = "手机认证申请成功，等待管理员审核!";
        message(errormsg, phoneUrl);
        return MSG;
    }

    /*实名认证已认证*/
    @Action(value="realName",results={
            @Result(name="success", type="ftl",location="/myAccount/personalCenter/realName.html"),
            @Result(name="login", type="redirect",location="/user/login.html"),

    })
    public String realName() throws Exception {
        User user = getSessionUser();
        user = userService.getUserById(user.getUserId());
        if (user.getApiStatus() == 1 || !StringUtils.isBlank(user.getApiId())) {
            user.getBirthday(user.getCardId());
        }
        boolean checkPhone = true;
        if (user.getPhoneStatus() == 1) {
            checkPhone = false;
        }
        Rule rule = ruleService.getRuleByNid("is_phone_check");
        if (rule != null && rule.getStatus() == 1) {
            if (rule.getValueIntByKey("status") == 0) {
                checkPhone = false;
            }
        }

        // 启用体验金
        logger.info("是否启用体验金功能：" + isEnableExperienceMoney());
        if (isEnableExperienceMoney()) {
            ExperienceMoney em = this.experienceMoneyService
                    .getExperenceMoney(SearchParam.getInstance().addParam(
                            "user", user));
            if (em != null)
                request.setAttribute("em", em);
        }

        if (checkPhone) {
            message("请先绑定手机! ", "/member/myAccount/phone.html", "点击绑定手机");
            return MSG;
        } else if (user.getRealStatus() == 0) {// 提交实名信息
            request.setAttribute("user", user);
            return SUCCESS;
        } else {
            request.setAttribute("user", user);
            return "success";
        }
    }

    /*TODO 实名认证未认证--环境才能测试*/
    @Action(value="realNameActive",results={
            @Result(name="success", type="ftl",location="/myAccount/personalCenter/realNameActive.html"),
            @Result(name="login", type="redirect",location="/user/login.html")
    })
    public String realNameActive() throws Exception {

    	User user = getSessionUser();
		user = userService.getUserById(user.getUserId());
		if (user.getApiStatus() == 1 || !StringUtils.isBlank(user.getApiId())) {
			user.getBirthday(user.getCardId());
		}
		boolean checkPhone = true;
		if (user.getPhoneStatus() == 1) {
			checkPhone = false;
		}
		Rule rule = ruleService.getRuleByNid("is_phone_check");
		if (rule != null && rule.getStatus() == 1) {
			if (rule.getValueIntByKey("status") == 0) {
				checkPhone = false;
			}
		}
		
		// 启用体验金
		logger.info("是否启用体验金功能：" + isEnableExperienceMoney());
		if (isEnableExperienceMoney()) {
			ExperienceMoney em = this.experienceMoneyService
					.getExperenceMoney(SearchParam.getInstance().addParam(
							"user", user));
			if (em != null)
				request.setAttribute("em", em);
		}

		if (checkPhone) {
			message("请先绑定手机!", "/member/myAccount/phone.html", "点击绑定手机");
			return MSG;
		} else if (user.getRealStatus() == 0) {// 提交实名信息
			request.setAttribute("user", user);
			return SUCCESS;
		} else {
			request.setAttribute("user", user);
			return "success";
		}
    }

    /*重置密码*/
    @Action(value="resetPassword",results={
            @Result(name="success", type="ftl",location="/myAccount/personalCenter/resetPassword.html"),
            @Result(name="login", type="redirect",location="/user/login.html")
    })
    public String resetPassword() throws Exception {
        User user = getSessionUser();
        String errormsg="";
        String oldpassword = request.getParameter("oldpassword");
        String newpassword = request.getParameter("newpassword");
        String newpassword1 = request.getParameter("newpassword1");
        String chgpwdType=request.getParameter("actionType");
        request.setAttribute("query_type", "userpwd");
        if(!StringUtils.isNull(chgpwdType).equals("")){
            if (newpassword != null) {
                errormsg= checkUserpwd(oldpassword, newpassword ,newpassword1);
                if (!errormsg.equals("")) {
                    request.setAttribute("errormsg", errormsg);
                    return SUCCESS;
                }
                user.setPassword(new MD5().getMD5ofStr(newpassword));
                String msg = "修改密码成功!";
                request.setAttribute("msg", msg);
                userService.updateUser(user);
                super.systemLogAdd(user, 6, "用户修改登录密码成功!");
            }else{
                errormsg="修改密码失败!";
                request.setAttribute("errormsg", errormsg);
                return SUCCESS;
            }
        }
        return SUCCESS;
    }
    private String checkUserpwd(String oldpassword, String newpassword,String newpassword1) {
        User user = getSessionUser();
        if (userService.login(user.getUsername(), oldpassword) == null) {
            return "密码不正确，请输入您的旧密码!";
        } else if (newpassword.length() < 8 || newpassword.length() > 16) {
            return "新密码长度在8到16之间!";
        } else if(StringUtils.isBlank(newpassword) || !newpassword.equals(newpassword1) ){
            return "您两次输入的新密码不一样，请重新填写!";
        }else if (!(StringUtils.pwdContainStr(newpassword)&&StringUtils.pwdContainNum(newpassword))) {
            return "登陆密码不能为纯数字或者纯字母模式，请添加复杂的密码!";
        }
        return "";
    }

    /*我的银行卡*/
    @SuppressWarnings("rawtypes")
	@Action(value="myBank",results={
            @Result(name="success", type="ftl",location="/myAccount/personalCenter/myBank.html"),
            @Result(name="login", type="redirect",location="/user/login.html")
    })
    public String myBank() throws Exception {
    	User sessionUser = getSessionUser();
		long user_id = sessionUser.getUserId();
		User user = userService.getUserById(user_id);
		if (user == null) {
			throw new BussinessException("非法操作，请登录...", "/user/login.html");
		}
		String typeStr = StringUtils.isNull(paramString("type"));
		if (StringUtils.isBlank(typeStr)) {
			// 查询提现银行卡
			SearchParam drawBankParam = new SearchParam();
			if (!"ALL".equals(Constant.DRAW_CARDTYPE)) {
				drawBankParam.addParam("cardType", Constant.DRAW_CARDTYPE);
			}
			if (!"ALL".equals(Constant.DRAW_CHANNEL_NO)) {
				drawBankParam.addParam("channelNo", Constant.DRAW_CHANNEL_NO);
			}
			SearchParam param = new SearchParam();
			param.addParam("user", new User(user.getUserId())).addParam("status", 1);
			List bankList = accountService.getAccountBankList(param).getList();
			request.setAttribute("bankSize", bankList.size());
			request.setAttribute("bankList", bankList);
			return SUCCESS;
		} else {// 解绑银行卡
			String phonecode = paramString("validCodeee");
			String bankid = paramString("bankid");
			if (isOpenApi()) {
				if (StringUtils.isBlank(bankid)) {
					throw new BussinessException("无效的参数，请重新操作");
				}
			}

			Rule rule = ruleService.getRuleByNid("bank_check_phone");
			if (rule != null) {
				if (rule.getStatus() == 1) { // 校验添加银行手机校验是否启用
					int rulerStatus = rule.getValueIntByKey("status");// 获取初审是否需要进行分布操作，1为需要，0为不需要
					if (rulerStatus == 1) {// 1需要手机校验						
						String codeUniqueId = String.valueOf(session.get(getSessionUser().getPhone()));
						if (StringUtils.isBlank(codeUniqueId)) {
							throw new BussinessException("处理失败，请重新操作", "/member/myAccount/myBank.html");
						}
						if (!phonecode.equals(codeUniqueId)) {
							throw new BussinessException("解绑银行卡失败，输入验证码有误！请重新获取验证码进行验证");
						}
						session.remove(getSessionUser().getPhone());
					}
				}
			}
			accountService.accountBankRemove(user, bankid);
			message("解绑银行卡成功!!", "/member/myAccount/myBank.html");
			User tempUser = userService.getUserById(user.getUserId());
			TempIdentifyUser tempIdentifyUser = userService.inintTempIdentifyUser(tempUser);
			session.put(Constant.TEMP_IDENTIFY_USER, tempIdentifyUser);
			super.systemLogAdd(user, 19, "用户解绑银行卡成功");
			return MSG;
        }
    }

    /*绑定银行卡*/
    @SuppressWarnings("rawtypes")
	@Action(value="bindBank",results={
            @Result(name="success", type="ftl",location="/myAccount/personalCenter/bindBank.html"),
            @Result(name="login", type="redirect",location="/user/login.html")
    })
    public String bindBank() throws Exception {
        User sessionUser = getSessionUser();
        long user_id = sessionUser.getUserId();
        User user = userService.getUserById(user_id);
        checkBank(user);// 绑卡校验
        String typeStr = StringUtils.isNull(paramString("type"));
        SearchParam drawBankParam = apiService.getdrawBankParam();// 第三方银行定义
        List drawList = null;
        switch (Global.getInt("api_code")) {
            case 3:// 双乾
                drawList = accountService.getDrawBankMmmBySearchParam(drawBankParam);
                request.setAttribute("drawBankList", drawList);
                break;
            default:
                break;
        }
        if (typeStr.equals("add")) { // 添加提现银行卡
            String msg = checkApi(user);// 第三方校验
            if (!msg.equals("success")) {// msg不为success则直接提示用户
                return msg;
            }
        } else {// 查询银行卡列表
            SearchParam param = new SearchParam();
            param.addParam("user", new User(user.getUserId()));
            List bankList = accountService.getAccountBankList(param).getList();
            request.setAttribute("bankList", bankList);
        }
        request.setAttribute(Constant.SESSION_USER, user);
        return SUCCESS;
    }

    private void checkBank(User user) {
        String api_name = Global.getValue("api_name");
        if (user == null) {
            throw new BussinessException("请您先登录，然后操作", "/user/login.html");
        }
        if (isOpenApi()) {
            if (StringUtils.isBlank(user.getApiId())) {// 如果不存在apiId
                throw new BussinessException("为了您的资金安全，请您先按照开户流程，填写实名信息开通" + api_name + "账号 ");
            }
        }
    }

    private String checkApi(User user) {
        int apiType = Global.getInt("api_code");// 第三方参数
        String msg = "";
        switch (apiType) {
            case 3:// 双乾接口:跳转钱多多绑卡页面
                msg = checkMmmBank(user);
                break;
            default:
                break;
        }
        return msg;
    }

    private String checkMmmBank(User user) {
        int drawBankId = paramInt("drawBank");
        String branchStr = paramString("branch");
        String accountStr = paramString("account");
        String accountSec = paramString("accountSec");
        if (StringUtils.isBlank(accountStr) || StringUtils.isBlank(accountSec)) {
            throw new BussinessException("银行卡账号输入不能为空！", "/member/myAccount/myBank");
        }
        if (!accountStr.equals(accountSec)) {
            throw new BussinessException("两次输入银行卡账号不一样！", "/member/myAccount/myBank");
        }
        // 同个银行卡多次绑定的校验
        AccountBank ab = accountService.getAccountBankByCardNo(accountStr, user);
        if (ab != null) {
            throw new BussinessException("该卡号银行卡已绑定，请勿重复绑定！", "/member/myAccount/myBank");
        }
        long province = paramLong("province");
        long city = paramLong("city");
		//修改为 省市二级联动
        if (province == 0 || city == 0 ) {
            throw new BussinessException("请选择完整的银行卡归属地！", "/member/myAccount/myBank");
        }
        DrawBankMmm drawBankmm = accountService.getDrawBankMmmById(drawBankId);
        if (drawBankmm == null) {
            throw new BussinessException("对不起,获取银行信息出错,请联系管理员！", "/member/myAccount/myBank");
        }
        AccountBank actbank = new AccountBank();
        actbank.setAccount(accountStr);
        actbank.setBankMmm(drawBankmm);
        actbank.setBranch(branchStr);
        actbank.setMmmprovince(new AreaMmm(province));
        actbank.setMmmcity(new AreaMmm(city));
        actbank.setAddtime(new Date());
        actbank.setAddip(getRequestIp());
        actbank.setUser(new User(user.getUserId()));
        accountService.addAccountBank(actbank);
        message("添加银行卡成功!", "/member/myAccount/myBank.html");
        super.systemLogAdd(user, 18, "用户绑定银行卡成功");
        return MSG;
    }

	/* 绑定银行卡省市联动 */
	@Action("provinceBank")
	public String provinceBank() throws Exception {
		long pid = paramLong("pid");
		List<AreaMmm> areas = userinfoService.getMmmBankListByPid(pid);
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		String jo = JSONArray.toJSONString(areas);
		out.print(jo);
		out.close();
		return null;
	}

	@Action("cityBank")
	public String cityBank() throws Exception {
		HttpServletRequest req = ServletActionContext.getRequest();
		String pid = (String) req.getParameter("pid");
		List<Area> areas = userinfoService.getAreaListByPid(NumberUtils.getInt(pid));
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		String jo = JSONArray.toJSONString(areas);
		out.print(jo);
		out.close();
		return null;
	}

/*Prize by liruonan 20161121 START*/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	/*TODO 我的奖品 */  
	@Action(value = "myAccountPrize", results = { 
			@Result(name = "kindPrize", type = "ftl", location = "/myAccount/prize/kindPrize.html"),
			@Result(name = "prizeUsed", type = "ftl", location = "/myAccount/prize/prizeUsed.html"),
			@Result(name = "prizeExpired", type = "ftl", location = "/myAccount/prize/prizeExpired.html")
			})
	public String myAccountPrize() throws Exception {
		User user = getSessionUser();
		List<MyPrize> myPrizes = new ArrayList<MyPrize>();
		SearchParam param = new SearchParam();
		param.addParam("user", user);
		String status=request.getParameter("status");
		String prizeurl=request.getParameter("prizeurl");
		if(StringUtils.isBlank(status)&&StringUtils.isBlank(prizeurl)){
			status="0";
			prizeurl="kindPrize";
		}

		if(status.equals("0")){
			param.addParam("status", 0);
		}else if(status.equals("1")){
			param.addParam("status", 1);
		}else if(status.equals("2")){
			param.addParam("status", 2);
		}
		
	
		ExperienceMoney em = this.experienceMoneyService.getExperenceMoney(SearchParam.getInstance().addParam("user", user).addParam("useStatus", paramInt("status")));
		if (em != null) {
			request.setAttribute("em", em);
		}
		

		List<PrizeUserRelationship> purList = prizeUserRelationshipService.findByParam(param);
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
				List<PrizeDetail> pdList = this.prizeDetailService.findByParam(params);
				logger.info("查询是否有兑换码：" + pdList.size());
				if (!pdList.isEmpty()) {
					mp.setExchangeCode(pdList.get(0).getDetail());
				} else {
					mp.setExchangeCode("");
				}
				myPrizes.add(mp);
			}
		
		// 封装 start
		List<MyPrizePlus> lst=new ArrayList<MyPrizePlus>();
		
		if(em!=null){
			MyPrizePlus myPrizePlus=new MyPrizePlus();
			myPrizePlus.setPrizeName("体验金");
			myPrizePlus.setImgUrl("/themes/soonmes_yjt/member/experienceMoney/imgs/rewardMoney.png");
			myPrizePlus.setExperienceMoney(em.getExperienceMoney());
			myPrizePlus.setExchangeCode(null);
			myPrizePlus.setTime(em.getAddTime());
			lst.add(myPrizePlus);
		}
		
		for (MyPrize prize : myPrizes) {
			MyPrizePlus myPrizePlus=new MyPrizePlus();
			myPrizePlus.setPrizeName(prize.getPrizeName());
			myPrizePlus.setImgUrl(prize.getPrizeImgUrl());
			myPrizePlus.setTime(prize.getPrizeTime());
			myPrizePlus.setExchangeCode(prize.getExchangeCode());
			lst.add(myPrizePlus);
		}
		// 封装 end
		
		//按奖品的领取时间排序
		Collections.sort(lst, new CalenderComparator());
		System.out.println("按奖品的领取时间排序:");  
	    myprint(lst);
	    
		int currentPage = paramInt("page") == 0 ? 1 : paramInt("page");
		int total = lst.size();
		int pernum = paramInt("pageNum") == 0 ? 4 : paramInt("pageNum");
		Page page = new Page(total, currentPage, pernum);
		PageDataList<MyPrizePlus> pageDataList=null;
		if(total>4){
			List newList=null;
			int first=(currentPage-1)*pernum;
			if( first+5 <= total ){
				newList=lst.subList(first, first+4);
			}else{
				newList=lst.subList(first, total);
			}
			pageDataList = new PageDataList<MyPrizePlus>(page, newList);
		}else{
			pageDataList = new PageDataList<MyPrizePlus>(page, lst);
		}
		
		setPageAttribute(pageDataList, param);
		return prizeurl;
	}
	
	 // 自定义方法：分行打印输出list中的元素  
    @SuppressWarnings("rawtypes")
	public static void myprint(List<MyPrizePlus> list) {  
        Iterator it = list.iterator(); // 得到迭代器，用于遍历list中的所有元素  
        while (it.hasNext()) {// 如果迭代器中有元素，则返回true  
            System.out.println("\t" + it.next());// 显示该元素  
        }  
    }  
	
    // 自定义比较器：按奖品的领取时间来排序
	@SuppressWarnings("rawtypes")
	static class CalenderComparator implements Comparator {  
        public int compare(Object object1, Object object2) { // 实现接口中的方法  
        	MyPrizePlus m1 = (MyPrizePlus) object1;          // 强制转换  
        	MyPrizePlus m2 = (MyPrizePlus) object2;  
            return m2.getTime().compareTo(m1.getTime());  
        }  
    }  

/* Prize by liruonan 20161121 END */


/* cashWithdrawal by liruonan 20161117 START */
	/* 资金提现默认页 */
	@SuppressWarnings("rawtypes")
	@Action(value = "cashWithdrawal", results = {
			@Result(name = "success", type = "ftl", location = "/myAccount/cashWithdrawal/cashWithdrawal.html"),
			@Result(name = "login", type = "redirect", location = "/user/login.html") })
	public String cashWithdrawal() throws Exception {
		User user = getSessionUser();
		if (user == null) {
			return LOGIN;
		}
		String startTime = paramString("dotime1");
		String endTime = paramString("dotime2");
		String type = paramString("account_type");
		String username = paramString("username");// 用户名
		String tousername = paramString("tousername");// 交易对方名称
		SearchParam param = SearchParam.getInstance().addParam("user.userId", user.getUserId()).addOrder(OrderType.DESC, "id").addPage(paramInt("page"));

        if(!StringUtils.isBlank(startTime)){
            param.addParam("addtime", SearchFilter.Operator.GTE , DateUtils.getDate2(startTime));
        }
        if(!StringUtils.isBlank(endTime)){
            param.addParam("addtime", SearchFilter.Operator.LTE , DateUtils.getDate2(endTime));
        }
        if(!StringUtils.isBlank(type) && !"0".equals(type)){
            param.addParam("type", type);
        }
        if(!StringUtils.isBlank(username)){
            param.addParam("user.username", SearchFilter.Operator.LIKE, username);
        }
        if(!StringUtils.isBlank(tousername)){
            param.addParam("toUser.username", SearchFilter.Operator.LIKE, tousername);
        }
        param.addOrder(OrderFilter.OrderType.DESC, "id");

        PageDataList data = accountService.getAccontLogList(param);
        Map<String, Object> extraparam = new HashMap<String, Object>();
        extraparam.put("dotime1", startTime);
        extraparam.put("dotime2", endTime);
        extraparam.put("account_type", type);
        extraparam.put("username", username);
        extraparam.put("tousername", tousername);

		UserAccountSummary summary = accountService.getUserAccountSummary(user.getUserId()); // 账户资金详情
		extraparam.put("summary", summary); // 账户总额
		setPageAttribute(data, param, extraparam); // 保存时间查询

		String excelType = paramString("excel");
		if (excelType.isEmpty()) {
			return SUCCESS;
		} else {

            String contextPath = ServletActionContext.getServletContext().getRealPath("/");
            String downloadFile = "资金记录_" + DateUtils.dateStr3(new Date()) + ".xls";
            String infile = contextPath + "/data/export/" + downloadFile;
            String[] names=new String[]{"user/.username","user/.realname","toUser/.username","typeName", "money","total","useMoney","noUseMoney","collection","repay" ,"remark","addtime","addip"};
            String[] titles=new String[]{"用户名","真实姓名","交易对方","交易类型","操作金额","账户总额","可用金额","冻结金额","待收金额","待还"+ "金额","备注","时间","IP"};

			SearchParam param2 = SearchParam.getInstance();
			Date startDate = DateUtils.getDate2(startTime);
			Date endTimeDate = DateUtils.getDate2(endTime);
			if (!StringUtils.isBlank(startTime) && startDate != null) {
				param2.addParam("addtime", SearchFilter.Operator.GTE, startDate);
			}
			if (!StringUtils.isBlank(endTime) && endTimeDate != null) {
				param2.addParam("addtime", SearchFilter.Operator.LTE,endTimeDate);
			}
			if (!StringUtils.isBlank(type) && !"0".equals(type)) {
				param2.addParam("type", type);
			}
			if (!StringUtils.isBlank(username)) {
				param2.addParam("user.username", SearchFilter.Operator.LIKE,username);
			}
			if (!StringUtils.isBlank(tousername)) {
				param2.addParam("toUser.username", SearchFilter.Operator.LIKE,tousername);
			}
			param2.addOrder(OrderFilter.OrderType.ASC, "id");
			List list = accountService.getSumAccontLogList(param2);
			ExcelHelper.writeExcel(infile, list, AccountLog.class,Arrays.asList(names), Arrays.asList(titles));
			export(infile, downloadFile);

			return null;
		}
	}

	/* TODO 充值--要在测试上测 */
	@SuppressWarnings("rawtypes")
	@Action(value = "recharge", results = { @Result(name = "success", type = "ftl", location = "/myAccount/cashWithdrawal/recharge.html") })
	public String recharge() throws Exception {
		User user = getSessionUser();
		SearchParam param = SearchParam.getInstance().addParam("user", user).addPage(paramInt("page")).addOrder(OrderType.DESC, "addtime");
		PageDataList data = accountService.getRechargeList(param);
		setPageAttribute(data, param);
		request.setAttribute("user", user);
		return SUCCESS;
	}

	@Action(value = "newrecharge", results = { 
			@Result(name = "success", type = "ftl", location = "/member/myAccount/recharge.html"), 
			@Result(name = "mmm", type = "ftl", location = "/member/mmm/mmmRecharge.html") 
			
	})
	public String newrecharge() throws Exception {
		User user = this.getSessionUser();
		user = userService.getUserById(user.getUserId());//当前用户本人
		request.setAttribute("user", user);
		if (!(user.getRealStatus() == 1 || user.getRealStatus() == 3)) {
			message("您的实名审核未通过！", "/member/apiRealname.html", "点击进入实名认证");
			logger.info("充值异常日志记录：" + user.getPhone() + ", 您的实名审核未通过， 导致充值失败！");
			super.systemLogAdd(user, 20, "您的实名审核未通过， 导致充值失败！");
			return MSG;
		}
		String result = checkRecharge(user); // 充值校验，由于汇付和易极付所需的东西不同，所以校验的东西也不同
		if (!result.equals("")) {
			return result;
		}
		String type = accountRecharge.getType();//充值类型 1为 网上充值 8 汇款 9快捷支付
		List<AccountBank> accountDeduBankList = accountService.getBankByUserId(getSessionUser().getUserId(), 1);// 获取用户签约的银交行卡
		request.setAttribute("bankList", accountDeduBankList); // 保存已经签约的银行卡
		double remitMoney = Global.getDouble("remit_money_lowest_limit");// 汇款充值最低限额
		request.setAttribute("remitMoney", remitMoney); // 保存汇款充值最低限额
		if (StringUtils.isBlank(type)) {
			return SUCCESS;
		} else {
			String msg = checkRechargeType();
			return msg;
		}
	}

	private String checkRecharge(User user) {
		if (StringUtils.isBlank(user.getApiId())) {
			message("充值需要开通" + Global.getString("api_name") + "账户！", "/member/index.html", "返回主页，开通" + Global.getString("api_name") + "账户");
			logger.info("充值异常日志记录：" + user.getPhone() + ", 充值需要开通" + Global.getString("api_name") + "账户, 导致充值失败!");
			super.systemLogAdd(user, 20, "充值需要开通" + Global.getString("api_name") + "账户, 导致充值失败!");
			return MSG;
		}
		return "";
	}

	private String checkRechargeType() throws Exception {
		String type = accountRecharge.getType();// 1 ,8, 9 
		String payment = accountRecharge.getPayment();// 易极付这里先不用
		double money = accountRecharge.getMoney();//充值金额
		logger.info("type:" + type + "   payment:" + payment + " money:" + money);
		if (money == 0) {
			message("充值金额不能为零", "/member/account/newrecharge.html");
			logger.info("充值异常日志记录：" + getSessionUser().getPhone() + ", 充值金额不能为零, 导致充值失败!");
			super.systemLogAdd(getSessionUser(), 20, "充值金额不能为零, 导致充值失败!");
			return MSG;
		}
		// 验证码 这里加上
		// 这里判断验证码是否由Ajax请求传过来的。ajax请求的校验验证码，调用的方法不同
		boolean b = true;
		if (!StringUtils.isBlank(paramString("isPhoneRequest")) && paramInt("isPhoneRequest") == 1) {
			if (!ajaxCheckValidImg(StringUtils.isNull(paramString("valicode")))) {
				b = false;
			}
		} else {
			if (!checkValidImg(StringUtils.isNull(paramString("valicode")))) {
				b = false;
			}
		}
		if (!b) {
			message("验证码不正确！", "/member/account/newrecharge.html");
			logger.info("充值异常日志记录：" + getSessionUser().getPhone() + ", 验证码不正确, 导致充值失败!");
			super.systemLogAdd(getSessionUser(), 20, "验证码不正确, 导致充值失败!");
			return MSG;
		}

		// 汇款充值限额
		double remitMoney = Global.getDouble("remit_money_lowest_limit");
		if ("8".equals(type) && money < remitMoney) {
			message("线下汇款充值金额必须大于等于" + remitMoney + "元！", "/member/account/newrecharge.html");
			logger.info("充值异常日志记录：" + getSessionUser().getPhone() + ", 线下汇款充值金额必须大于等于" + remitMoney + "元, 导致充值失败!");
			super.systemLogAdd(getSessionUser(), 20, "线下汇款充值金额必须大于等于" + remitMoney + "元, 导致充值失败!");
			return MSG;
		}

		if ("1".equals(type) || "8".equals(type) || "9".equals(type)) {// 易极付网银支付和线下汇款充值及快捷支付
			User sessionUser = this.getSessionUser();
			AccountRecharge r = new AccountRecharge();
			r.setUser(sessionUser);
			r.setType(type);
			r.setAddtime(new Date());
			r.setAddip(this.getRequestIp());
			r.setPayment(payment);
			r.setMoney(money);
			r.setRemark(Global.getValue("api_name") + "线上充值");
			r.setStatus(0);
			String returnType = madeRecharge(r, sessionUser); // 判断接口类型，封装充值对象。
			accountService.addRecharge(r);
			super.systemLogAdd(sessionUser, 20, "用户线上充值,金额:" + r.getMoney() + ",订单号:" + r.getTradeNo());//记录系统日志
			return returnType;
		} else if ("2".equals(type)) {// 线下充值
			User sessionUser = this.getSessionUser();
			// 汇款信息
			String serial_no = paramString("serial_no");//流水号
			String account_name = paramString("account_name");//汇款户名
			String bank_in = paramString("bank_in");//收款银行
			if (StringUtils.isBlank(serial_no) || StringUtils.isBlank(account_name) || StringUtils.isBlank(bank_in)) {
				throw new BussinessException("流水号/汇款户名/收款银行等必须填写");
			}
			String return_txt = "流水号：" + serial_no + ", 汇款户名：" + account_name + ",收款银行：" + bank_in + ", 联系手机号：" + sessionUser.getPhone();
			// 判断流水号唯一性
			if (!accountService.checkRechargeOffLine(serial_no)) {
				throw new BussinessException("流水号已经存在，请重新输入", "/member/account/newrecharge.html");
			}

			AccountRecharge r = new AccountRecharge();
			r.setUser(sessionUser);
			r.setType(type);
			r.setAddtime(new Date());
			r.setAddip(this.getRequestIp());
			r.setPayment(payment);
			r.setMoney(money);
			r.setRemark("线下充值");
			r.setStatus(0);
			r.setTradeNo(OrderNoUtils.getInstance().getSerialNumber()); // 订单号
			r.setReturnTxt(return_txt);
			accountService.addRecharge(r);
			super.systemLogAdd(sessionUser, 20, "用户线下充值,金额:" + r.getMoney() + ",订单号:" + r.getTradeNo());
			message("尊敬的用户您线下充值" + Global.getValue("webname") + "已经受理，请您联系客服" + Global.getValue("fuwutel") + "做进一步的操作", "/member/account/recharge.html");
			return MSG;
		} else if ("3".equals(type)) { // 无卡代扣充值方式 TGPROJECT-137 start
			User sessionUser = this.getSessionUser();
			int id = paramInt("bankId");
			AccountBank bank = accountService.getAccountBankById(id);
			if (bank == null) {
				message("无签约银行卡，不能使用代扣充值！", "/member/account/newrecharge.html");
				return MSG;
			}
			AccountRecharge r = new AccountRecharge();
			r.setUser(sessionUser);
			r.setType(type);
			r.setAddtime(new Date());
			r.setAddip(this.getRequestIp());
			r.setPayment(payment);
			r.setMoney(money);
			r.setRemark("易极付无卡代扣充值");
			r.setStatus(0);
			r.setAccountBank(bank);
			String orderNo = OrderNoUtils.getInstance().getSerialNumber();
			r.setTradeNo(orderNo); // 订单号
			super.systemLogAdd(sessionUser, 20, "用户无卡代扣充值,金额:" + r.getMoney() + ",订单号:" + r.getTradeNo());
			return MSG;
		} else {
			message("不支持这种支付方式，请选择其他方式", "/member/account/newrecharge.html");
			return MSG;
		}

	}
	
	/**
	 * 对用户充值进行判断，封装不同的充值对象
	 * 
	 * @param
	 * @param user
	 * @return
	 */
	private String madeRecharge(AccountRecharge r, User user) {
		int apiType = Global.getInt("api_code");
		switch (apiType) {
		case 3: // 双乾
			MmmRecharge mmmRe = MmmHelper.mmmRecharge(user, r.getMoney() + "", r.getType());
			r.setTradeNo(mmmRe.getOrderNo()); // 订单号
            //前台充值打开双乾页面需要的参数：rechargeMoneymoremore：要充值的账号的钱多多标识~platformMoneymoremore：开通乾多多帐号为平台帐号时生成，以p开头~orderNo：平台流水号~amount:金额~rechargeType:充值方式~feeType:手续费类型~returnURL:页面回调地址~notifyURL:后台回调地址~signInfo:签名参数
			logger.info("用户充值：" + user.getPhone() + "前台充值打开双乾页面需要的参数：rechargeMoneymoremore：" + mmmRe.getRechargeMoneymoremore() + ", platformMoneymoremore: " + mmmRe.getPlatformMoneymoremore() + ", orderNo: " + mmmRe.getOrderNo() + ", amount: " + mmmRe.getAmount() + ", rechargeType: +" + mmmRe.getRechargeType() + ", feeType: " + mmmRe.getFeeType() + ", returnURL: " + mmmRe.getReturnURL() + ", notifyURL: " + mmmRe.getNotifyURL() + ", signInfo: " + mmmRe.getSignInfo());
			super.systemLogAdd(user, 20, "前台充值打开双乾页面需要的参数：rechargeMoneymoremore：" + mmmRe.getRechargeMoneymoremore() + ", platformMoneymoremore: " + mmmRe.getPlatformMoneymoremore() + ", orderNo: " + mmmRe.getOrderNo() + ", amount: " + mmmRe.getAmount() + ", rechargeType: +" + mmmRe.getRechargeType() + ", feeType: " + mmmRe.getFeeType() + ", returnURL: " + mmmRe.getReturnURL() + ", notifyURL: " + mmmRe.getNotifyURL() + ", signInfo: " + mmmRe.getSignInfo());
			request.setAttribute("mmm", mmmRe);
			return "mmm";
		default:
			throw new BussinessException("系统未找到此支付方，请联系客服人员！", "/member/account/newrecharge.html");
		}

	}

	/* 充值--结束 */

	/* TODO 提现--要在测试上测 */
	@Action(value = "withdrawals", results = { @Result(name = "success", type = "ftl", location = "/myAccount/cashWithdrawal/withdrawals.html") })
	public String withdrawals() throws Exception {
		User user = getSessionUser();
		user = userService.getUserById(user.getUserId());
		if (user == null) {
			throw new BussinessException("请您先登录!");
		}
		if (!(user.getRealStatus() == 1 || user.getRealStatus() == 3)) {
			message("您的实名审核未通过！", "/member/myAccount/realName.html", "点击进入实名认证!");
			return MSG;
		}
		List<AccountBank> banklist = accountService.getBankLists(user.getUserId()); // 获取用户可用的银行卡
		if (banklist.size() < 1) {
			message("请先添加银行卡！", "/member/myAccount/bindBank.html", "点击进入绑定银行卡!");
			return MSG;
		}

		Account account = accountService.getAccountByUser(user);
		int lateCount = borrowService.getLateRepaymentByUser(user); // 获取用户是否有逾期未还的记录
		if (lateCount != 0) {
			request.setAttribute("lateCount", lateCount = 0);
		}
		request.setAttribute("account", account);
		request.setAttribute("banklist", banklist);
		request.setAttribute("user", user);
		return SUCCESS;
	}

	@Action(value = "doNewcash", results = {
			@Result(name = "success", type = "ftl", location = "/member/myAccount/withdrawals.html"),
			@Result(name = "mmmCash", type = "ftl", location = "/member/mmm/mmmCash.html") }, interceptorRefs = { @InterceptorRef(value = "mydefault") })
	public String doNewcash() throws Exception {
		String backUrl = "/member/myAccount/withdrawals.html";
		User user = getSessionUser();
		// 校验取现并填充AccountCash
		AccountCash cash = checkCashMessage(user, backUrl);
		// 提现操作
		String result = madeCash(cash, user, backUrl);
		// 更新accountCash
		accountService.updateAccountCash(cash);
		if (!"".equals(result)) {
			return result;
		}
		return MSG;
	}

	public String madeCash(AccountCash accountCash, User user, String backUrl) {
		int apiType = Global.getInt("api_code");
		switch (apiType) {
		case 3:
			String webVipFeePercent = Global.getValue("webVip_feePercent");// 平台垫付vip用户取现手续费比例
			String siteFee = "0";// 网站垫付比例
			siteFee = Double.parseDouble(webVipFeePercent) * 100 + "";
			Rule rule = ruleService.getRuleByNid("free_cash_rule");// 免费提现额度
			if (rule != null && rule.getStatus() == 1) {
				double freeCashMoney = user.getAccount().getFreeCashMoney();// 免费提现金额
				double total = accountCash.getTotal();// 提现总额
				if (freeCashMoney >= total) {
					siteFee = "100";
				}
			}
			MmmCash mmCash = MmmHelper.mmmCash(accountCash, siteFee);
			accountCash.setOrderNo(mmCash.getOrderNo());
			request.setAttribute("mmm", mmCash);
			return "mmmCash";
		default:
			return "";
		}
	}

	private AccountCash checkCashMessage(User user, String backUrl) {
		// 这里判断验证码是否由Ajax请求传过来的。ajax请求的校验验证码，调用的方法不同
		boolean b = true;
		if (!StringUtils.isBlank(paramString("isPhoneRequest"))&& paramInt("isPhoneRequest") == 1) {
			if (!ajaxCheckValidImg(StringUtils.isNull(paramString("valicode")))) {
				b = false;
			}
		} else {
			if (!checkValidImg(StringUtils.isNull(paramString("valicode")))) {
				b = false;
			}
		}
		if (!b) {
			throw new BussinessException("验证码不正确！", backUrl);
		}
		// end

		if (user.getUserCache().getCashForbid() != 0) {
			throw new BussinessException("该用户禁止提现，请联系客服人员！", backUrl);
		}
		double money = paramDouble("money");
		double minCash = NumberUtils.getDouble(Global.getValue("lowest_cash"));// 平台要求的最低提现金额
		if (money < minCash) {
			throw new BussinessException("提现金额必须大于" + minCash, backUrl);
		}
		double maxCash = NumberUtils.getDouble(Global.getValue("most_cash"));// 最大提现金额
		if (money > maxCash) {
			throw new BussinessException("提现金额不能大于" + maxCash + ", 请您联系客服!");
		}
		Account at = accountService.getAccountByUser(user);
		if (at == null || at.getUseMoney() < money) { // 校验账户余额是否满足取现金额
			throw new BussinessException("可用金额不足!", backUrl);
		}
		// 获取用户净值标的总待还金额
		double waitPropertyAllMoney = accountService.getAllPropertyWaitRepayMoney(user.getUserId());
		if (at == null|| money > at.getUseMoney() + at.getCollection()- waitPropertyAllMoney) {
			// 校验用户可提现金额：可用金额 + 待收金额 - 净值标待还金额
			throw new BussinessException("输入的提现金额大于可提现总额，请核对后再提现！", backUrl);
		}
		AccountCash cash = new AccountCash();
		// 校验，并封装提现信息。
		checkNewCash(at, backUrl, cash);
		// 申请记录保存到数据库
		User u = userService.getUserById(user.getUserId());
		if (u == null) {
			cash.setUser(new User(user.getUserId()));
		} else {
			cash.setUser(u);// 防止添加空 user
		}
		cash.setAddtime(new Date());
		cash.setAddip(getRequestIp());
		cash.setTotal(money); // 提现金额
		cash.setFee(0);
		int id = paramInt("accountBank");
		if (id != 0) {
			AccountBank accountBank = accountService.getAccountBankById(id);
			cash.setAccountBank(accountBank);
		}
		cash.setStatus(4);// 初始状态设为4
		accountService.seveCash(cash);
		return cash;
	}

	private void checkNewCash(Account at, String backUrl, AccountCash cash) {
		int apiType = Global.getInt("api_code");
		switch (apiType) {
		default:
			break;
		}
	}

	
	/* 提现--结束 */
/* cashWithdrawal by liruonan 20161117 END */
	
	
	/* 第三方授权（投标、还款、扣除管理费授权） add by lxm 2016年12月20日16:02:44 */
	@Action(value = "loanAuthorize", results = { @Result(name = "success", type = "ftl", location = "/member/mmm/loanAuthorize.html") })
	public String loanAuthorize() {
		User user = getSessionUser();
		user = userService.getUserById(user.getUserId());
		if (user.getPhoneStatus() != 1) {
			message("请先绑定手机！", "/member/identify/phone.html", "点击绑定手机");
			return MSG;
		} else if (user.getRealStatus() == 0) {// 提交实名信息
			message("请先实名认证！", "/member/apiRealname.html", "点击实名认证");
			return MSG;
		}
		// 增加跳转授权的标 ID标识
		MmmLoanAuthorize mmm = MmmHelper.mmmLoanAuthorize(user, paramString("borrowId"));
		request.setAttribute("mmm", mmm);
		return SUCCESS;
	}

/* personalCenter by liruonan 20161122 START */
	/* 资料概览 */
	@SuppressWarnings("rawtypes")
	@Action(value = "personalCenter", results = { @Result(name = "success", type = "ftl", location = "/myAccount/personalCenter/personalCenter.html") })
	public String personalCenter() throws Exception {
		User sessionUser = getSessionUser();
		long user_id = sessionUser.getUserId();
		User user = userService.getUserById(user_id);
		SearchParam param = new SearchParam();
		param.addParam("user", new User(user.getUserId())).addParam("status", 1);
		List bankList = accountService.getAccountBankList(param).getList();
		request.setAttribute("bankSize", bankList.size());
		return SUCCESS;
	}

	/* 基本资料 */
	@Action(value = "basicData", results = {
			@Result(name = "success", type = "ftl", location = "/myAccount/personalCenter/basicData.html"),
			@Result(name = "login", type = "redirect", location = "/user/login.html") })
	public String basicData() throws Exception {

		return SUCCESS;
	}

	/* 房产资料 */
	@Action(value = "houseData", results = {
			@Result(name = "success", type = "ftl", location = "/myAccount/personalCenter/houseData.html"),
			@Result(name = "login", type = "redirect", location = "/user/login.html") })
	public String houseData() throws Exception {

		return SUCCESS;
	}

	/* 单位资料 */
	@Action(value = "unitData", results = {
			@Result(name = "success", type = "ftl", location = "/myAccount/personalCenter/unitData.html"),
			@Result(name = "login", type = "redirect", location = "/user/login.html") })
	public String unitData() throws Exception {

		return SUCCESS;
	}

	/* 私营业主 */
	@Action(value = "privateOwner", results = {
			@Result(name = "success", type = "ftl", location = "/myAccount/personalCenter/privateOwner.html"),
			@Result(name = "login", type = "redirect", location = "/user/login.html") })
	public String privateOwner() throws Exception {

		return SUCCESS;
	}

	/* 财务状况 */
	@Action(value = "finance", results = {
			@Result(name = "success", type = "ftl", location = "/myAccount/personalCenter/finance.html"),
			@Result(name = "login", type = "redirect", location = "/user/login.html") })
	public String finance() throws Exception {

		return SUCCESS;
	}

	/* 联系方式 */
	@Action(value = "contact", results = {
			@Result(name = "success", type = "ftl", location = "/myAccount/personalCenter/contact.html"),
			@Result(name = "login", type = "redirect", location = "/user/login.html") })
	public String contact() throws Exception {

		return SUCCESS;
	}

	/* 配偶资料 */
	@Action(value = "spouse", results = {
			@Result(name = "success", type = "ftl", location = "/myAccount/personalCenter/spouse.html"),
			@Result(name = "login", type = "redirect", location = "/user/login.html") })
	public String spouse() throws Exception {

		return SUCCESS;
	}

	/* 教育背景 */
	@Action(value = "education", results = {
			@Result(name = "success", type = "ftl", location = "/myAccount/personalCenter/education.html"),
			@Result(name = "login", type = "redirect", location = "/user/login.html") })
	public String education() throws Exception {

		return SUCCESS;
	}

/* personalCenter by liruonan 20161122 END */

/* projectRecord by liruonan 20161122 START */
	/* 投标项 */
	@SuppressWarnings("unchecked")
	@Action(value = "tender", results = { @Result(name = "success", type = "ftl", location = "/myAccount/projectRecord/tender.html") })
	public String tender() throws Exception {
		User user = this.getSessionUser();
		SearchParam param = SearchParam.getInstance()
				.addParam("status", 0)
				.addParam("borrow.type", SearchFilter.Operator.NOTEQ,Constant.TYPE_FLOW) //流转标即投即扣
				.addParam("waitAccount", 0)
				.addParam("user", user)
				.addPage(paramInt("page"))
				.addOrder(OrderFilter.OrderType.DESC, "id");
		PageDataList<BorrowTender> list = borrowService.getTenderList(param);
		request.setAttribute("page", list.getPage());
		request.setAttribute("list", list.getList());
		request.setAttribute("param", new HashMap<String, String>());
		request.setAttribute("invest_type", "tendering");
		return SUCCESS;
	}

	/* 借出项 */
	@SuppressWarnings("unchecked")
	@Action(value = "lending", results = { @Result(name = "success", type = "ftl", location = "/myAccount/projectRecord/lending.html") })
	public String lending() {
		User user = getSessionUser();
		String startTime = paramString("dotime1");
		String endTime = paramString("dotime2");
		SearchParam param=SearchParam.getInstance()
				.addParam("user", user)
				.addOrFilter("status",0,1)
				.addPage(paramInt("page"))
				.addParam("borrow.status", SearchFilter.Operator.GTE, 6)
				.addOrder(OrderFilter.OrderType.DESC, "id");
		if (!StringUtils.isBlank(startTime)) {
			startTime += " 00:00:00";
			param.addParam("addtime", SearchFilter.Operator.GTE,DateUtils.getDate2(startTime));
		}
		if (!StringUtils.isBlank(endTime)) {
			endTime += " 23:59:59";
			param.addParam("addtime", SearchFilter.Operator.LTE,DateUtils.getDate2(endTime));
		}
		PageDataList<BorrowTender> list = borrowService.getTenderList(param);
		request.setAttribute("page", list.getPage());
		request.setAttribute("list", list.getList());
		request.setAttribute("param", new HashMap<String, String>());
		request.setAttribute("invest_type", "hasTender");
		return SUCCESS;
	}

	/* 未收款 */
	@SuppressWarnings({ "unchecked" })
	@Action(value = "unpaid", results = { @Result(name = "success", type = "ftl", location = "/myAccount/projectRecord/unpaid.html") })
	public String unpaid() {
		User user = getSessionUser();
		String startTime = paramString("dotime1");
		String endTime = paramString("dotime2");
		SearchParam param=SearchParam.getInstance()
				.addParam("status", 0)
				.addParam("borrowTender.user", user)
				.addParam("borrowTender.waitAccount", SearchFilter.Operator.GT,0)
				.addParam("borrowTender.status", 0)
				.addPage(paramInt("page"))
				.addOrder(OrderFilter.OrderType.ASC, "repayTime");
		if (!StringUtils.isBlank(startTime)) {
			startTime += " 00:00:00";
			param.addParam("repayTime", SearchFilter.Operator.GTE,DateUtils.getDate2(startTime));
		}
		if (!StringUtils.isBlank(endTime)) {
			endTime += " 23:59:59";
			param.addParam("repayTime", SearchFilter.Operator.LTE,DateUtils.getDate2(endTime));
		}
		PageDataList<BorrowCollection> list = borrowService.getCollectList(param);
		request.setAttribute("page", list.getPage());
		request.setAttribute("list", list.getList());
		request.setAttribute("param", new HashMap<String, String>());
		request.setAttribute("invest_type", "notCollect");
		return SUCCESS;
	}

	/* 已收款 */
	@SuppressWarnings("unchecked")
	@Action(value = "received", results = { @Result(name = "success", type = "ftl", location = "/myAccount/projectRecord/received.html") })
	public String received() {
		User user = getSessionUser();
		String startTime = paramString("dotime1");
		String endTime = paramString("dotime2");
		SearchParam param=SearchParam.getInstance()
				.addJoin("borrowCollection", BorrowCollection.class)
				.addParam("status", 1)
				.addParam("borrowTender.user", user)
				.addPage(paramInt("page"))
				.addOrder(OrderFilter.OrderType.DESC, "id");
		if (!StringUtils.isBlank(startTime)) {
			startTime += " 00:00:00";
			param.addParam("repayTime", SearchFilter.Operator.GTE,DateUtils.getDate2(startTime));
		}
		if (!StringUtils.isBlank(endTime)) {
			endTime += " 23:59:59";
			param.addParam("repayTime", SearchFilter.Operator.LTE,DateUtils.getDate2(endTime));
		}
		PageDataList<BorrowCollection> list = borrowService.getCollectList(param);
		request.setAttribute("page", list.getPage());
		request.setAttribute("list", list.getList());
		request.setAttribute("param", new HashMap<String, String>());
		request.setAttribute("invest_type", "hasCollect");
		return SUCCESS;
	}
/* projectRecord by liruonan 20161122 END */


/* loanAccount by liruonan 20161121 START */
	/* 尚未发布的借款 */
	@SuppressWarnings("rawtypes")
	@Action(value = "noBorrowing", results = { @Result(name = "success", type = "ftl", location = "/myAccount/loanAccount/noBorrowing.html") })
	public String noBorrowing() throws Exception {
		User user = getSessionUser();
		SearchParam param=SearchParam.getInstance()
				.addOrFilter("status", 0)
				.addParam("user", user)
				.addPage(paramInt("page"))
				.addOrder(OrderFilter.OrderType.DESC, "id");
		PageDataList list = borrowService.getList(param);
		request.setAttribute("page", list.getPage());
		request.setAttribute("list", list.getList());
		request.setAttribute("param", new HashMap<String, String>());
		request.setAttribute("borrow_type", "unpublish");
		return SUCCESS;
	}

	/* 已经还款的借款 */
	@SuppressWarnings("rawtypes")
	@Action(value = "alreadyRepaid", results = { @Result(name = "success", type = "ftl", location = "/myAccount/loanAccount/alreadyRepaid.html") })
	public String alreadyRepaid() throws Exception {
		User user = getSessionUser();
		String startTime = paramString("dotime1");
		String endTime = paramString("dotime2");
		SearchParam param=SearchParam.getInstance()
				.addParam("status", 8)
				.addParam("user", user)
				.addParam("isAssignment", 0)
				.addParam("type",Operator.NOTEQ,Constant.TYPE_FLOW)
				.addPage(paramInt("page"))
				.addOrder(OrderType.DESC, "id");
		if(!StringUtils.isBlank(startTime)){
			startTime+= " 00:00:00";
			param.addParam("verifyTime", Operator.GTE , DateUtils.getDate2(startTime));
		}
		if(!StringUtils.isBlank(endTime)){
			endTime+= " 23:59:59";
			param.addParam("verifyTime", Operator.LTE , DateUtils.getDate2(endTime));
		}
		PageDataList list = borrowService.getList(param);
		request.setAttribute("page", list.getPage());
	    request.setAttribute("list", list.getList());
		request.setAttribute("param", new HashMap<String, String>());
		request.setAttribute("borrow_type", "repaid");
		return SUCCESS;
	}

	/* 撤回的贷款 */
	@SuppressWarnings("rawtypes")
	@Action(value = "recallBorrowing", results = {
			@Result(name = "success", type = "ftl", location = "/myAccount/loanAccount/recallBorrowing.html"),
			@Result(name = "login", type = "redirect", location = "/user/login.html") })
	public String recallBorrowing() throws Exception {
		User user = getSessionUser();
		SearchParam param=SearchParam.getInstance()
				.addOrFilter("status",-1,2,4,49,5,59)
				.addParam("user", user)
				.addPage(paramInt("page"))
				.addOrder(OrderFilter.OrderType.DESC, "id");
		PageDataList list = borrowService.getList(param);
		request.setAttribute("page", list.getPage());
		request.setAttribute("list", list.getList());
		request.setAttribute("param", new HashMap<String, String>());
		request.setAttribute("borrow_type", "showCancelBorrows");
		return SUCCESS;
	}

	/* 正在还款的借款 */
	@SuppressWarnings("rawtypes")
	@Action(value = "repaymentBorrowing", results = { @Result(name = "success", type = "ftl", location = "/myAccount/loanAccount/repaymentBorrowing.html") })
	public String repaymentBorrowing() throws Exception {
		User user = getSessionUser();
		String startTime = paramString("dotime1");
		String endTime = paramString("dotime2");
		SearchParam param=SearchParam.getInstance()
				.addOrFilter("status", 6, 7)
				.addParam("user", user)
				.addParam("type", SearchFilter.Operator.NOTEQ,Constant.TYPE_FLOW)
				.addPage(paramInt("page"))
				.addOrder(OrderFilter.OrderType.DESC, "id");
		if (!StringUtils.isBlank(startTime)) {
			startTime += " 00:00:00";
			param.addParam("verifyTime", SearchFilter.Operator.GTE,DateUtils.getDate2(startTime));
		}
		if (!StringUtils.isBlank(endTime)) {
			endTime += " 23:59:59";
			param.addParam("verifyTime", SearchFilter.Operator.LTE,DateUtils.getDate2(endTime));
		}
		PageDataList list = borrowService.getList(param);
		request.setAttribute("list", list.getList());
		request.setAttribute("page", list.getPage());
		request.setAttribute("param", new HashMap<String, String>());
		request.setAttribute("borrow_type", "repaying");
		return SUCCESS;
	}
    /*正在还款的借款还款页面*/
    @SuppressWarnings("unchecked")
	@Action(value = "repaymentBorrowD", results = { @Result(name = "success", type = "ftl", location = "/myAccount/loanAccount/repaymentBorrowD.html") })
    public String repaymentBorrowD() throws Exception {
    	long borrowId = paramLong("id");
		String borrow_type = paramString("borrow_type");
		User user=getSessionUser();
		int page = paramInt("page")==0?1:paramInt("page");
		
		List<BorrowRepayment> list = null;
		if(borrowId == 0){
			SearchParam param = SearchParam.getInstance().addPage(page);		
			param.addParam("borrow.user", new User(user.getUserId()));
			list = repaymentService.getRepayMentsBySearchParam(param).getList();
		}else{
			list = repaymentService.getRepayMents(borrowId);
			Borrow borrow = borrowService.getBorrow(borrowId);
			if(borrow.getStatus() == 8){//8完成
				request.setAttribute("repay_status", "end");
			}
		}
		
		
		request.setAttribute("repay", list);
		request.setAttribute("borrow_type", borrow_type);
		request.setAttribute("repay_token", saveToken("repay_token"));
		return "success";
    }

    /* 还款明细汇总 */
	@SuppressWarnings("unchecked")
	@Action(value = "repaymentDetail", results = { @Result(name = "success", type = "ftl", location = "/myAccount/loanAccount/repaymentDetail.html") })
	public String repaymentDetail() throws Exception {
		User user = getSessionUser();
		String borrow_type = paramString("borrow_type");
		int page = paramInt("page") == 0 ? 1 : paramInt("page");
		SearchParam param = SearchParam.getInstance().addPage(page);
		param.addParam("borrow.user", new User(user.getUserId()));
		param.addOrder(OrderFilter.OrderType.ASC, "status");
		param.addOrder(OrderFilter.OrderType.ASC, "repaymentTime");
		PageDataList<BorrowRepayment> list = repaymentService.getRepayMentsBySearchParam(param);
		setPageAttribute(list, param);
		request.setAttribute("borrow_type", borrow_type);
		request.setAttribute("repay_token", saveToken("repay_token"));
		return SUCCESS;
	}

	/* 正在招标的借款 */
	@SuppressWarnings("rawtypes")
	@Action(value = "tenderBorrowing", results = { @Result(name = "success", type = "ftl", location = "/myAccount/loanAccount/tenderBorrowing.html") })
	public String tenderBorrowing() throws Exception {
		User user = getSessionUser();
		SearchParam param=SearchParam.getInstance()
				.addParam("user", user)
				.addParam("status", 1)
				.addPage(paramInt("page"))
				.addOrder(OrderFilter.OrderType.DESC, "id");
		PageDataList list = borrowService.getList(param);
		// 这里查询参数 要重新处理
		request.setAttribute("page", list.getPage());
		request.setAttribute("list", list.getList());
		request.setAttribute("param", new HashMap<String, String>());
		request.setAttribute("borrow_type", "borrowing");
		return SUCCESS;
	}
/* projectRecord by liruonan 20161122 END */


/* message by liruonan 20161122 START */
	/* 收件箱 */
	@SuppressWarnings("rawtypes")
	@Action(value = "inbox", results = { @Result(name = "success", type = "ftl", location = "/myAccount/messages/inbox.html") })
	public String inbox() throws Exception {
		User sessionUser = this.getSessionUser();
		long userid = sessionUser.getUserId();
		SearchParam param = SearchParam.getInstance().addPage(paramInt("page"))
				.addParam("receiveUser", new User(userid))
				.addParam("sented", Operator.NOTEQ, 0)
				.addOrder(OrderType.DESC, "addtime");
		PageDataList plist = messageService.getMessageBySearchParam(param);
		request.setAttribute("msgList", plist.getList());
		request.setAttribute("page", plist.getPage());
		request.setAttribute("param", param.toMap());
		return SUCCESS;
	}

	/* 已发送信息 */
	@SuppressWarnings("rawtypes")
	@Action(value = "hasSend", results = { @Result(name = "success", type = "ftl", location = "/myAccount/messages/hasSend.html") })
	public String hasSend() throws Exception {
		User sessionUser = this.getSessionUser();
		int page = paramInt("page");
		int perNum = (paramInt("perNum") == 0) ? Page.ROWS : paramInt("perNum");
		SearchParam param = SearchParam.getInstance();
		param.addPage(page, perNum);
		param.addParam("sentUser", sessionUser);
		param.addParam("sented", 1);
		param.addOrder(OrderType.DESC, "addtime");
		PageDataList plist = messageService.getMessageBySearchParam(param);
		setPageAttribute(plist, param);
		return SUCCESS;
	}

	/* 发件箱 */
	@Action(value = "sendMsg", results = { @Result(name = "success", type = "ftl", location = "/myAccount/messages/sendMsg.html") })
	public String sendMsg() throws Exception {
		User sessionUser = this.getSessionUser();
		String type = StringUtils.isNull(request.getParameter("type"));// 提交时是reply，转发时是add
		String sendType = StringUtils.isNull(request.getParameter("sendType"));// 提交时是空，转发时是forward
		int id = NumberUtils.getInt(request.getParameter("id"));// 消息的ID
		Message msg = messageService.getMessageById(id);// 根据消息ID查到此消息
		User sentUser = userService.getUserById(sessionUser.getUserId());// 发送人
		if (type.equals("add")) {
			String errormsg = checkMessage();
			if (!errormsg.equals("")) {
				request.setAttribute("errormsg", errormsg);
				return SUCCESS;
			}
			User receive_user = new User();
			if (type.equals("add")) {
				receive_user = userService.getUserByName(request.getParameter("receiveUser.username"));
			} else {
				receive_user = message.getReceiveUser();// 接收人
			}

			if (receive_user == null) {
				errormsg = "收件人不存在!";
				request.setAttribute("errormsg", errormsg);
				return SUCCESS;
			}
			if (sentUser.getUserId() == receive_user.getUserId()) {
				message("收件人不能为自己！", "/member/myAccount/hasSend.html");
				return MSG;
			}
			Message nowMsg = new Message();
			nowMsg.setContent(request.getParameter("content"));
			nowMsg.setName(request.getParameter("name"));
			nowMsg.setSentUser(sentUser);
			nowMsg.setReceiveUser(receive_user);
			nowMsg.setStatus(0);
			nowMsg.setSented(1);
			nowMsg.setType(Constant.SYSTEM);
			nowMsg.setType(Constant.SYSTEM);
			nowMsg.setAddip(getRequestIp());
			nowMsg.setAddtime(new Date());
			messageService.addMessage(nowMsg);
			message("发送消息成功!", "/member/myAccount/hasSend.html");
			return MSG;
		} else if (type.equals("reply")) {
			Message message = new Message();// 封装Message
			message.setSentUser(msg.getReceiveUser());
			message.setReceiveUser(msg.getSentUser());
			message.setStatus(0);
			message.setSented(1);
			message.setType(Constant.SYSTEM);
			message.setAddip(getRequestIp());
			message.setAddtime(new Date());
			message.setName("Re:" + msg.getName());
			message.setContent(msg.getContent()
					+ "</br>------------------ 原始信息 ------------------</br>"
					+ StringUtils.isNull(paramString("repContent")));
			messageService.addMessage(message);
			message("回复消息成功!", "/member/myAccount/hasSend.html");
			return MSG;
		} else {
			request.setAttribute("msg_type", "send");
		}
		request.setAttribute("sendType", sendType);
		request.setAttribute("msg", msg);
		return SUCCESS;

	}

	private String checkMessage() throws Exception {
		String errormsg = "";
		String validcode = StringUtils.isNull(request.getParameter("valicode"));// 验证码
		String name = request.getParameter("name");
		String content = request.getParameter("content");
		User receiveUser = userService.getUserByName(request.getParameter("receiveUser.username")); // 收件人
		if (receiveUser == null) {
			errormsg = "对不起收件人不存在!";
			return errormsg;
		} else if (StringUtils.isNull(name).equals("")) {
			errormsg = "标题不能为空!";
			return errormsg;
		} else if (StringUtils.isNull(content).equals("")) {
			errormsg = "内容不能为空!";
			return errormsg;
		} else if (!checkValidImg(validcode)) {
			errormsg = "验证码错误!";
			return errormsg;
		}
		return errormsg;
	}

	/* 回复邮件 */
	@Action(value = "viewMail", results = { @Result(name = "success", type = "ftl", location = "/myAccount/messages/viewMail.html") })
	public String viewMail() throws Exception {// 回复信件
		int id = NumberUtils.getInt(request.getParameter("id"));
		String type = StringUtils.isNull(request.getParameter("type"));
		if (id < 1) {
			message("您操作有误，请勿乱操作！", "/member/myAccount/inbox.html");
			return ADMINMSG;
		}

		Message msg = messageService.getMessageById(id);
		if (msg == null) {
			message("您操作有误，请勿乱操作！", "/member/myAccount/inbox.html");
			return ADMINMSG;
		}
		User friend = userService.getUserById(msg.getSentUser().getUserId());
		msg.setStatus(1);
		messageService.modifyMessge(msg);
		request.setAttribute("msg", msg);
		request.setAttribute("friend", friend);
		request.setAttribute("type", type);
		return SUCCESS;
	}

	/* 设置信件 标记为已读 未读 删除 */
	@Action(value = "setMail", results = { @Result(name = "success", type = "ftl", location = "/myAccount/messages/hasSend.html") })
	public String setMail() throws Exception {
		String type = StringUtils.isNull(request.getParameter("type"));
		String ids = paramString("ids");
		if (ids.length() > 1) {
			ids = ids.substring(0, ids.length() - 1);
		}
		String allId[] = ids.split(",");
		Integer all[] = new Integer[allId.length];
		for (int i = 0; i < allId.length; i++) {
			all[i] = new Integer(allId[i].trim());
		}
		if (all.length < 1) {
			return MSG;
		}
		String tip = "";
		if (type.equals(Constant.DEL_MSG)) {
			tip = "删除信息成功!";
			messageService.deleteMessage(all);
		} else if (type.equals(Constant.SET_READ_MSG)) {
			tip = "已标记已读!";
			messageService.setReadMessage(all);
		} else if (type.equals(Constant.SET_UNREAD__MSG)) {
			tip = "已标记未读!";
			messageService.setUnreadMessage(all);
		}
		message(tip, "/member/myAccount/inbox.html");
		return ADMINMSG;
	}

	/* 未读消息 */
	@Action(value = "getUnRead")
	public String getUnRead() throws Exception {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		User sessionUser = this.getSessionUser();
		int unRead = messageService.getUnreadMessageCount(sessionUser);
		dataMap.put("unRead", unRead);
		Json json = new Json("", dataMap, "");
		super.writeJson(json);
		return null;
	}
	/* message by liruonan 20161122 END */
	
	
	/* 未使用奖品个数   add by lxm 2016年12月15日15:56:31*/
	@Action(value = "getUnUsedPrize")
	public String getUnUsedPrize() throws Exception {
		User user = getSessionUser();
		List<MyPrize> myPrizes = new ArrayList<MyPrize>();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		SearchParam param = new SearchParam();
		param.addParam("user", user);
		param.addParam("status", 0);
		List<PrizeUserRelationship> purList = prizeUserRelationshipService.findByParam(param);
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
				List<PrizeDetail> pdList = this.prizeDetailService.findByParam(params);
				logger.info("查询是否有兑换码：" + pdList.size());
				if (!pdList.isEmpty()) {
					mp.setExchangeCode(pdList.get(0).getDetail());
				} else {
					mp.setExchangeCode("");
				}
				myPrizes.add(mp);

			}
		
		ExperienceMoney em = this.experienceMoneyService.getExperenceMoney(SearchParam.getInstance().addParam("user", user).addParam("useStatus", 0));
		int unUsedPrize=0;
		if( em != null ){
			unUsedPrize = myPrizes.size()+1;
		}else{
			unUsedPrize = myPrizes.size();
		}
		dataMap.put("unUsedPrize", unUsedPrize);
		Json json = new Json("", dataMap, "");
		super.writeJson(json);
		return null;
	}

}
