package com.liangyou.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 标的子类型表
 * 
 */
@Entity(name="borrow_property")
public class BorrowProperty {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="borrow_id",updatable=false)
	private Borrow borrow;
	
	@Column(name="property_type")
	private String propertyType;
	//v1.8.0.4_u1 TGPROJECT-385 wsl start
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="warrant_id",updatable=true)
	private Warrant warrant;
	//v1.8.0.4_u1 TGPROJECT-385 wsl end
	//无名重庆 WMCQ-1 2014-09-09 wsl start
	@Column(name="project_type")
	private String projectType;//项目类型
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="business_type_id",updatable=true)
	private BusinessType businessType;//业务部门
	//无名重庆 WMCQ-1 2014-09-09 wsl end
	@Temporal(TemporalType.TIMESTAMP)
	private Date endTime;  //投标有效时间
	
	@Column(name="borrow_no")
	private String borrowNo;  //借款申请编号
	
	@Column(name="borrowagreement_NO")
	private String borrowagreementNO; //借款合同编号
	
	@Column(name="verify_borrow_no")
	private String verifyBorrowNo; //审核后台标编号
	
	
	@Column(name="lowest_money")
	private double lowestMoney;  //最低筹集金额
	// v1.8.0.4 TGPROJECT-140 lx start
	@Column(name="file_name")
	private String fileName;
	
	// v1.8.0.4 TGPROJECT-140 lx end
	
	//v1.8.0.4_u2    TGPROJECT-297  lx start
	/**
	 * 房龄
	 */
	@Column(name="house_year")
	private int houseYear;
	/**
	 * 抵押顺序
	 */
	@Column(name="secured_order")
	private int securedOrder;
	/**
	 * 房屋地址
	 */
	@Column(name="house_address")
	private String houseAddress;
	/**
	 * 房屋面积
	 */
	@Column(name="house_area")
	private double houseArea;
	/**
	 * 房屋价格
	 */
	@Column(name="house_price")
	private double housePrice;
	
	
	/**
	 * 借款成数：用房产价值的几成抵押借款
	 */
	@Column(name="borrow_proportion")
	private double borrowProportion;
	
	/**
	 * 用款时间
	 */
	@Column(name="usefundstime")
	private Date usefundstime;
	
	
	/**
	 * 风险等级
	 */
	@Column(name="risk_level")
	private int riskLevel;
	
	
	
	public int getRiskLevel() {
		return riskLevel;
	}
	public void setRiskLevel(int riskLevel) {
		this.riskLevel = riskLevel;
	}
	public Date getUsefundstime() {
		return usefundstime;
	}
	public void setUsefundstime(Date usefundstime) {
		this.usefundstime = usefundstime;
	}
	public double getBorrowProportion() {
		return borrowProportion;
	}
	public void setBorrowProportion(double borrowProportion) {
		this.borrowProportion = borrowProportion;
	}
	
	

	public int getHouseYear() {
		return houseYear;
	}
	public void setHouseYear(int houseYear) {
		this.houseYear = houseYear;
	}
	public int getSecuredOrder() {
		return securedOrder;
	}
	public void setSecuredOrder(int securedOrder) {
		this.securedOrder = securedOrder;
	}
	public String getHouseAddress() {
		return houseAddress;
	}
	public void setHouseAddress(String houseAddress) {
		this.houseAddress = houseAddress;
	}
	public double getHouseArea() {
		return houseArea;
	}
	public void setHouseArea(double houseArea) {
		this.houseArea = houseArea;
	}
	public double getHousePrice() {
		return housePrice;
	}
	public void setHousePrice(double housePrice) {
		this.housePrice = housePrice;
	}
	//v1.8.0.4_u2    TGPROJECT-297  lx end
	public String getVerifyBorrowNo() {
		return verifyBorrowNo;
	}
	public void setVerifyBorrowNo(String verifyBorrowNo) {
		this.verifyBorrowNo = verifyBorrowNo;
	}
	public String getPropertyType() {
		return propertyType;
	}
	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Borrow getBorrow() {
		return borrow;
	}
	public void setBorrow(Borrow borrow) {
		this.borrow = borrow;
	}
	public Warrant getWarrant() {
		return warrant;
	}
	public void setWarrant(Warrant warrant) {
		this.warrant = warrant;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public String getBorrowNo() {
		return borrowNo;
	}
	public void setBorrowNo(String borrowNo) {
		this.borrowNo = borrowNo;
	}
	public String getBorrowagreementNO() {
		return borrowagreementNO;
	}
	public void setBorrowagreementNO(String borrowagreementNO) {
		this.borrowagreementNO = borrowagreementNO;
	}
	public double getLowestMoney() {
		return lowestMoney;
	}
	public void setLowestMoney(double lowestMoney) {
		this.lowestMoney = lowestMoney;
	}
	//v1.8.0.4 TGPROJECT-140 lx 2014-04-23 start
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	//v1.8.0.4 TGPROJECT-140 lx 2014-04-23 end
	//无名重庆 WMCQ-1 2014-09-09 wsl start
	public BusinessType getBusinessType() {
		return businessType;
	}
	public void setBusinessType(BusinessType businessType) {
		this.businessType = businessType;
	}
	public String getProjectType() {
		return projectType;
	}
	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}
	
	//无名重庆 WMCQ-1 2014-09-09 wsl end

}
