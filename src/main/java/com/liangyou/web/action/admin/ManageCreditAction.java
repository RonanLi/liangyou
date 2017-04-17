package com.liangyou.web.action.admin;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.context.Constant;
import com.liangyou.context.CreditType;
import com.liangyou.domain.Goods;
import com.liangyou.domain.GoodsCategory;
import com.liangyou.domain.GoodsPic;
import com.liangyou.domain.User;
import com.liangyou.domain.UserCredit;
import com.liangyou.domain.UserCreditConvert;
import com.liangyou.domain.UserCreditLog;
import com.liangyou.domain.UserCreditType;
import com.liangyou.exception.BussinessException;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.service.AccountService;
import com.liangyou.service.ApiService;
import com.liangyou.service.CreditConvertService;
import com.liangyou.service.GoodsService;
import com.liangyou.service.LogService;
import com.liangyou.service.MsgService;
import com.liangyou.service.OperateService;
import com.liangyou.service.RuleService;
import com.liangyou.service.UserService;
import com.liangyou.service.UserinfoService;
import com.liangyou.util.CompressImg;
import com.liangyou.util.DateUtils;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.BaseAction;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Namespace("/admin/credit")
@ParentPackage("p2p-default")
@InterceptorRefs(@InterceptorRef("manageAuthStack"))
public class ManageCreditAction extends BaseAction {
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(ManageCreditAction.class);
	@Autowired
	private AccountService accountService;
	@Autowired
	private UserinfoService userinfoService;
	@Autowired
	private UserService userService;
	@Autowired
	private MsgService msgService;
	@Autowired
	private LogService logService;
	@Autowired
	private RuleService ruleService;
	@Autowired
	private OperateService operateService;
	@Autowired
	private ApiService apiService;
	@Autowired
	private CreditConvertService creditConvertService;
	@Autowired
	private GoodsService goodsService;
	//v1.8.0.4_u1 TGPROJECT-252  lx start
	private File pic_url;
	public File getPic_url() {
		return pic_url;
	}


	public void setPic_url(File pic_url) {
		this.pic_url = pic_url;
	}
	//v1.8.0.4_u1 TGPROJECT-252  lx end

	//v1.8.0.4_u1 TGPROJECT-242  
	/**
	 * 商品分类管理
	 * @return
	 * @throws Exception
	 */
	@Action(value="goodsCatagoryList",
		results={@Result(name="goodsCatagoryList",type="ftl",location="/admin/credit/goodsCatagoryList.html")}
		)
	public String goodsCatagoryList() throws Exception {
		List<GoodsCategory> parentList = this.creditConvertService.getGoodsCategoryListByParentId(0);
		String catetoryType = paramString("catetoryType");
		request.setAttribute("parentList", parentList);
		request.setAttribute("catetoryType", catetoryType);
		SearchParam param=SearchParam.getInstance().addPage(paramInt("page"));
		if(paramInt("catetoryType")>0 ){
			param.addParam("parentId", paramInt("catetoryType"));
		}
		PageDataList<GoodsCategory> list=creditConvertService.getGoodsCategoryList(param);
		setPageAttribute(list, param);
		setPageUrl();
		if(StringUtils.isBlank(actionType)){
			return "goodsCatagoryList";
		}else{
			return null;
			
		}
	}
	
	
	/**
	 * 新增商品分类
	 * @return
	 */
	@Action(value="addGoodsCategory",
			results={@Result(name="success",type="ftl",location="/admin/credit/goodsCatagoryList.html"),
			@Result(name="child",type="ftl",location="/admin/credit/addChildGoodsCategory.html"),
			@Result(name="parent",type="ftl",location="/admin/credit/addParentGoodsCategory.html")}
			)
	public String addGoodsCategory(){
		String type = paramString("type");
		String actionType = paramString("actionType");
		try {
			if (!StringUtils.isBlank(actionType)) {
				GoodsCategory category = new GoodsCategory();
				if ((("parent").equals(type))) {
					String parentName = paramString("parent_name");
					category.setName(parentName);
					category.setParentId(0);
					category.setAddtime(new Date());
					category.setIsVirtual(3);
				} else if ((("child").equals(type))) {
					String childName = paramString("child_name");
					int isVirtual = paramInt("is_virtual");
					int parentId = paramInt("parent_id");
					category.setName(childName);
					category.setAddtime(new Date());
					category.setIsVirtual(isVirtual);
					category.setParentId(parentId);
				}
				this.creditConvertService.saveGoodsCategory(category);
				message("新增成功","/admin/credit/goodsCatagoryList.html");
				return ADMINMSG;
			} else {
				if (("parent").equals(type)) {
					return "parent";
				} else {
					List<GoodsCategory> parentList = this.creditConvertService.getGoodsCategoryListByParentId(0);
					request.setAttribute("parentList", parentList);
					return "child";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return SUCCESS;
    }
	
	/**
	 * 积分兑换商品列表
	 * @return
	 */
	@Action(value="creditGoodsList",
			results={@Result(name="success",type="ftl",location="/admin/credit/creditGoodsList.html")}
			)
	public String creditGoodsList(){
		SearchParam param = SearchParam.getInstance().addPage(paramInt("page"));
		String name=paramString("name");
		if(!StringUtils.isBlank(name)){
			param.addParam("name", Operator.LIKE, name);
		}
		String startTime = paramString("add_time_start");
		String endTime = paramString("add_time_end");
		
		if(!StringUtils.isBlank(startTime)){
			param.addParam("addtime", Operator.GTE , DateUtils.getDate(startTime, "yyyy-MM-dd HH:mm:ss"));
		}
		if(!StringUtils.isBlank(endTime)){
			param.addParam("addtime", Operator.LTE ,DateUtils.getDate(endTime, "yyyy-MM-dd HH:mm:ss"));
		}
		PageDataList<Goods> pageDataList = goodsService.findPageList(param);
		setPageAttribute(pageDataList, param);
		return SUCCESS;
	}
		
	/**
	 * 删除商品分类
	 * @return
	 */
	@Action(value="delGoodsCategory",
			results={@Result(name="success",type="ftl",location="/admin/credit/goodsCatagoryList.html")
			}
			)
	public String delGoodsCategory() {
		int cateId = paramInt("cateId");
		if (cateId>0) {
			List<GoodsCategory> list = this.creditConvertService.getGoodsCategoryListByParentId(cateId);
			if(list!=null){
				if(list.size()>0){
					message("该分类下有子分类，请删除子分类后再删除该分类","/admin/credit/goodsCatagoryList.html");
					return ADMINMSG;
				}
			}
			this.creditConvertService.delGoodsCategory(cateId);
			message("删除成功","/admin/credit/goodsCatagoryList.html");
			return ADMINMSG;
		}
		return "success";
	}

	// v1.8.0.4_u1 TGPROJECT-244   qj   2014-05-04 start 
	 /**
     * 审核后保存
     * @return
     */
	@Action(value="saveAuditGoods",
			results={@Result(name="success",type="ftl",location="/admin/credit/checkCreditGoods.html")}
			)
    public String saveAuditGoods(){
    	User auth_user = (User) session.get(Constant.AUTH_USER);
    	int status= paramInt("status");
    	int id= paramInt("id");
    	checkAdminValidImgWithUrl("/admin/credit/checkCreditGoods.html");
    	UserCreditLog creditLog=new UserCreditLog();
    	if(id != 0){
    		UserCreditConvert convert = creditConvertService.getCreditConvertById(id);
    		if(convert.getStatus() != 0){
    			throw new BussinessException("状态异常！不能进行审核!");
    		}
    		String remark="";
    		//如果是虚拟 dw_goods_category is_vartural= 1:vip 2: 现金 特殊处理
    		//通过dw_credit_convert 得到商品ID 然后根据商品ID得到分类
    		Goods goods = convert.getGoods();
    		UserCredit credit = convert.getUser().getUserCredit();
    		int isVirtual= goods.getCategory().getIsVirtual();
    		convert.setStatus(status);
    		if(status==1){ //审核通过
    			if(isVirtual == 0){  //实物
    				creditConvertService.updateCreditConvertByAuthUser(auth_user,convert);
	    			creditLog.setOp(2); //减少积分 
		    		remark="通过，扣除积分"+convert.getCreditValue();
		    		creditLog.setValue(credit.getValidValue());;
    			}else if(isVirtual == 1){ //vip
    				remark="通过，扣除积分"+convert.getCreditValue();
        			convert.setStatus(status);
    				creditConvertService.auditCreditConvertVip(convert);
    				creditLog.setValue(credit.getValidValue());
        		}else if(isVirtual == 2){ //现金
        			remark="通过，扣除积分"+convert.getCreditValue();
        			convert.setStatus(status);
        			creditConvertService.auditCreditConvert(convert);
        			creditLog.setValue(credit.getValidValue());
        		}
    		}else{//审核不通过 积分不变
    			this.creditConvertService.updateCreditConvertByAuthUser(auth_user,convert);
    			creditLog.setOp(CreditType.OP_TYPE_FAIL); 
    			remark="不通过，积分不变！";
    			//对应商品数量+
    			goods.setStore(goods.getStore() + convert.getConvertNum());
    			this.creditConvertService.addGoods(goods);
    			//用户有效积分增加 消费积分减少
    			this.creditConvertService.auditFailCreditConvert(convert);
    			creditLog.setValue(credit.getValidValue()+convert.getCreditValue());
    		}
		
    		//记录dw_credit_log明细
    		creditLog.setOperateValue(convert.getCreditValue());
		    creditLog.setUser(convert.getUser());
		    creditLog.setUserCreditType(new UserCreditType(CreditType.CREDIT_TYPE_INTEGRAL_VIP)); //消费积分  对应dw_credit_type表
		    creditLog.setValidValue(convert.getCreditValue());
		    creditLog.setRemark("您所兑换的商品，经管理员审核"+remark);
		    creditLog.setAddip(auth_user.getAddip());
		    creditLog.setAddtime(new Date());
		    this.creditConvertService.saveCreditLog(creditLog);
		    message("提交成功", "/admin/credit/checkCreditGoods.html");
		    return ADMINMSG;
    	}
    	return "success";
    }
 // v1.8.0.4_u1 TGPROJECT-244   qj   2014-05-04 end 
 
	//v1.8.0.4_u1 TGPROJECT-  zf start
	// 积分兑换审核管理
	@Action(value="checkCreditGoods",
			results={@Result(name="success",type="ftl",location="/admin/credit/checkCreditGoods.html")}
			)
	public String checkCreditGoods(){
		int page = paramInt("page") == 0 ? 1 : paramInt("page");
		SearchParam param = SearchParam.getInstance();
		param.addPage(page);
		param.addOrder(OrderType.DESC, "id");
		String username = paramString("name");
		if(username!=null&&username.trim()!=""){
			param.addParam("user.username",Operator.LIKE,username.trim());
			request.setAttribute("name", username.trim());
		}
		if(paramString("status")!=""&&paramInt("status")!=-1){
			param.addParam("status",paramInt("status"));
			request.setAttribute("status", paramInt("status"));
		}else{
			request.setAttribute("status", -1);
		}
		PageDataList<UserCreditConvert> pageDataList = creditConvertService.findUserCreditConvertList(param);
		setPageAttribute(pageDataList, param);
		return SUCCESS;
	}
 
		
	
	//跳转审核页面
	@Action(value="auditGoods",results={
			@Result(name="success", type="ftl",location="/admin/credit/auditGoods.html")			
			})
	public String auditGoods() {
		UserCreditConvert userCreditConvert = creditConvertService.findUserCreditConvert(paramInt("id"));
		request.setAttribute("userCreditConvert", userCreditConvert);
		return SUCCESS;
	}
	
	//v1.8.0.4_u1 TGPROJECT-  zf end
	
	//v1.8.0.4_u1 TGPROJECT-252  lx start

	 /**
     * 修改商品
     * @return
     */
	@Action(value="updateGoods",
			results={
			@Result(name="updateGoods",type="ftl",location="/admin/credit/updateGoods.html")
			}
			)
    public String updateGoods(){
    	// 参数为0 表示得到二级菜单
    	List<GoodsCategory> cateList=this.creditConvertService.getChildList();
		request.setAttribute("cateList", cateList);
    	int id =paramInt("id");
    	String actionType=paramString("actionType");
    	try{
	    	if(!StringUtils.isBlank(actionType)){
	    		Goods model=this.creditConvertService.getGoodsById(id);
	    		 String name=StringUtils.isNull(request.getParameter("name"));
	    		 int credit_value=paramInt("credit_value");
					int store=paramInt("store");
					 int category_id=paramInt("child_category_id");
					double cost=paramDouble("cost");
					 double market_price=paramDouble("market_price");
					 String attribute=StringUtils.isNull(request.getParameter("attribute"));
					 String description=StringUtils.isNull(request.getParameter("description"));
					 String remarks=StringUtils.isNull(request.getParameter("remarks"));
					 String shelves_time=StringUtils.isNull(request.getParameter("shelves_time"));
				 String fileValueList=paramString("fileValue");
				 if(fileValueList.startsWith(",")){
					 fileValueList=fileValueList.substring(1, fileValueList.length());
				 }
				 List<GoodsPic> gpList=new ArrayList<GoodsPic>();
				 if(fileValueList!=null){
					 String [] str=fileValueList.split(",");
					 int order=1;
					 for(int i=0;i<str.length;i++){
						 if(StringUtils.isBlank(str[i])){
							 continue;
						 }
						 GoodsPic goodsPic=new GoodsPic();
						 goodsPic.setGoods(model);
						 goodsPic.setPicName(str[i]);
						 goodsPic.setPicUrl("/data/goodsImg/"+str[i]);
						 goodsPic.setOrdering(order);
						 goodsPic.setCompressPicUrl("/data/goodsImg/compress/"+str[i]);
						 goodsPic.setAddtime( new Date());
						 gpList.add(goodsPic);
						 order++;
					 }
				 }
				 if(gpList!=null && gpList.size()>0){
					 this.creditConvertService.delAllGoodsPicByGoodsId(model.getId());
					 for (GoodsPic goodsPic : gpList) {
						 this.creditConvertService.saveGoodsPic(goodsPic);
					}
				 }else{
					 message("商品图未上传！","/admin/credit/creditGoodsList.html");
					 return ADMINMSG;
				 }
				 
				 model.setName(name);
				 model.setCreditValue(credit_value);
				 model.setStore(store);
				 model.setCategory(new GoodsCategory(category_id));
				 model.setCost(cost);
				 model.setMarketPrice(market_price);
				 model.setAttribute(attribute);
				 model.setDescription(description);
				 model.setRemarks(remarks);
				 model.setCreateUser(getAuthUser());
				 model.setUpdatetime(new Date());
				 model.setShelvestime(DateUtils.getDate2(shelves_time));
				 this.creditConvertService.saveGoods(model);
				 message("修改商品成功！","/admin/credit/creditGoodsList.html");
				 return ADMINMSG;
	    	}else{
	    		Goods model=this.creditConvertService.getGoodsById(id);
	    		List<GoodsPic> gpList=this.creditConvertService.getGoodsPicByGoodsId(Integer.parseInt(model.getId()+""));
	    		String allFileName="";
	    		for (GoodsPic goodsPic : gpList) {
	    			allFileName+=goodsPic.getPicName()+",";
				}
	    		if(allFileName.endsWith(",")){
	    			allFileName=allFileName.substring(0,allFileName.length()-1);
	    		}
	    		request.setAttribute("gpList", gpList);
	    		request.setAttribute("allFileName", allFileName);
	    		request.setAttribute("goods", model);
	    		request.setAttribute("sessionId", request.getSession().getId());
	    		return "updateGoods";
	    	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return null;
    }
	/**
	 * 删除商品
	 * @return
	 */
	@Action(value="delGoods",
			results={@Result(name="success",type="ftl",location="/admin/credit/creditGoodsList.html")
			}
			)
	public String delGoods(){
    	int id=paramInt("id");
    	Goods goods=creditConvertService.getGoodsById(id);
    	if(goods!=null){
    		this.creditConvertService.delGoods(goods);
    		message("删除商品成功！","/admin/credit/creditGoodsList.html");
    		return ADMINMSG;
    	}
    	return SUCCESS;
    }
	/**
	 * 新增商品
	 * @return
	 */
	@Action(value="addGoods",
			results={@Result(name="success",type="ftl",location="/admin/credit/creditGoodsList.html"),
			@Result(name="addGoods",type="ftl",location="/admin/credit/addGoods.html")})
	public String addGoods(){
		String actionType = paramString("actionType");
		try {
			if (!StringUtils.isBlank(actionType)) {
				String name=StringUtils.isNull(request.getParameter("name"));
				int credit_value=paramInt("credit_value");
				int store=paramInt("store");
				 int category_id=paramInt("category_id");
				double cost=paramDouble("cost");
				 double market_price=paramDouble("market_price");
				 String attribute=StringUtils.isNull(request.getParameter("attribute"));
				 String description=StringUtils.isNull(request.getParameter("description"));
				 String remarks=StringUtils.isNull(request.getParameter("remarks"));
				 String shelves_time=StringUtils.isNull(request.getParameter("shelves_time"));
				 Goods model=new Goods();
				 model.setName(name);
				 model.setCreditValue(credit_value);
				 model.setStore(store);
				 model.setCategory(new GoodsCategory(category_id));
				 model.setCost(cost);
				 model.setMarketPrice(market_price);
				 model.setAttribute(attribute);
				 model.setDescription(description);
				 model.setRemarks(remarks);
				 model.setCreateUser(getAuthUser());
				 model.setAddtime(new Date());
				 model.setUpdatetime(new Date());
				 model.setShelvestime(DateUtils.getDate2(shelves_time));
				 Goods g=this.creditConvertService.saveGoods(model);
				 
				 String fileValueList=paramString("fileValue");
				 if(fileValueList.startsWith(",")){
					 fileValueList=fileValueList.substring(1, fileValueList.length());
				 }
				 List<GoodsPic> gpList=new ArrayList<GoodsPic>();
				 if(fileValueList!=null){
					 String [] str=fileValueList.split(",");
					 int order=1;
					 for(int i=0;i<str.length;i++){
						 if(StringUtils.isBlank(str[i])){
							 continue;
						 }
						 GoodsPic goodsPic=new GoodsPic();
						 goodsPic.setGoods(g);
						 goodsPic.setPicName(str[i]);
						 goodsPic.setPicUrl("/data/goodsImg/"+str[i]);
						 goodsPic.setOrdering(order);
						 goodsPic.setCompressPicUrl("/data/goodsImg/compress/"+str[i]);
						 goodsPic.setAddtime( new Date());
						 gpList.add(goodsPic);
						 order++;
					 }
					 
				 }
				 if(gpList!=null && gpList.size()>0){
					 for (GoodsPic goodsPic : gpList) {
						this.creditConvertService.saveGoodsPic(goodsPic);
					}
				 }else{
					 this.creditConvertService.delGoods(g);
					 message("商品图未上传！","/admin/credit/creditGoodsList.html");
					 return ADMINMSG;
				 }
				 
				message("新增成功","/admin/credit/creditGoodsList.html");
				return ADMINMSG;
			}else{
				List<GoodsCategory> parentList = this.creditConvertService.getGoodsCategoryListByParentId(0);
				request.setAttribute("parentList", parentList);
				return "addGoods";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return SUCCESS;
    }
	/**
	 * 根据选择商品类别 加载子类
	 * @return
	 */
	@Action(value="childCreditCatagory")
	public String childCreditCatagory(){
		String categoryId=paramString("id");
		try{
			if(!StringUtils.isBlank(categoryId)){
				List<GoodsCategory> list=this.creditConvertService.getGoodsCategoryListByParentId(Integer.parseInt(categoryId));
				List<Map<String,Object>> categoryList=new ArrayList<Map<String,Object>>();
				if(list!=null && list.size()>0){
					for (GoodsCategory category : list) {
						Map<String,Object> map=new HashMap<String, Object>(); 
						map.put("category_id", category.getId());
						map.put("name", category.getName());
						categoryList.add(map);
					}
				}
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("goryList", categoryList);
				response.getWriter().print(jsonObj.toString());
	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	  /**
     * 上传文件
     * @return
     */
	@Action(value="uploadImg")
    public String uploadImg()throws Exception{
    	 String savePath = ServletActionContext.getServletContext().getRealPath("/")+"/data/goodsImg/";
		 File f=new File(savePath);
		 if(!f.exists()){
			 f.mkdir();
		 }
		 String fileName=this.convertFileName(pic_url.getName());
		 File file=new File(savePath+"/"+fileName+".jpg");
		 FileUtils.copyFile(pic_url, file);
		 String outPutPath=ServletActionContext.getServletContext().getRealPath("/")+"/data/goodsImg/compress";
		 File imgFile=new File(outPutPath);
		 if(!imgFile.exists()){
			 imgFile.mkdir();
		 }
		 new CompressImg().compressPic(savePath+"/",outPutPath+"/", fileName+".jpg",fileName+".jpg", 560, 600, true);
		 response.getWriter().print(JSONArray.toJSON(fileName+".jpg"));
    	 return null;
    }
    
	private String convertFileName(String fileName){
     	  return System.currentTimeMillis()+"";
    }
  //v1.8.0.4_u1 TGPROJECT-252  lx end
}
