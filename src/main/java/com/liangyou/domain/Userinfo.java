package com.liangyou.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.liangyou.context.Global;
import com.liangyou.dao.AreaDao;
import com.liangyou.dao.BorrowRepaymentDao;
import com.liangyou.dao.LinkageDao;
import com.liangyou.util.ReflectUtils;


/**
 * The persistent class for the userinfo database table.
 * 
 */
@Entity
@Table(name= Global.TABLE_PREFIX + "userinfo")
public class Userinfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	private String address;

	private int area;

	private int car;

	private int child;

	private int city;

	@Column(name="company_address")
	private String companyAddress;

	@Column(name="company_industry")
	private int companyIndustry;

	@Column(name="company_jibie")
	private int companyJibie;

	@Column(name="company_name")
	private String companyName;

	@Column(name="company_office")
	private int companyOffice;

	@Column(name="company_reamrk")
	private String companyReamrk;

	@Column(name="company_tel")
	private String companyTel;

	@Column(name="company_type")
	private String companyType;

	@Column(name="company_weburl")
	private String companyWeburl;

	@Column(name="company_worktime1")
	private Date companyWorktime1;

	@Column(name="company_worktime2")
	private Date companyWorktime2;

	@Column(name="company_workyear")
	private String companyWorkyear;

	private int education;

	@Column(name="education_record")
	private int educationRecord;

	@Column(name="education_school")
	private String educationSchool;

	@Column(name="education_study")
	private String educationStudy;

	@Column(name="education_time1")
	private Date educationTime1;

	@Column(name="education_time2")
	private Date educationTime2;

	@Column(name="finace_creditcard")
	private double finaceCreditcard;

	@Column(name="finance_amount")
	private double financeAmount;

	@Column(name="finance_car")
	private int financeCar;

	@Column(name="finance_caramount")
	private double financeCaramount;

	@Column(name="finance_property")
	private int financeProperty;

	@Column(name="finance_repayment")
	private double financeRepayment;

	@Column(name="house_year")
	private Date houseYear;

	@Column(name="house_address")
	private String houseAddress;

	@Column(name="house_area")
	private double houseArea;

	@Column(name="house_balance")
	private double houseBalance;

	@Column(name="house_bank")
	private String houseBank;

	@Column(name="house_holder1")
	private String houseHolder1;

	@Column(name="house_holder2")
	private String houseHolder2;

	@Column(name="house_loanprice")
	private double houseLoanprice;

	@Column(name="house_loanyear")
	private int houseLoanyear;

	@Column(name="house_right1")
	private double houseRight1;

	@Column(name="house_right2")
	private double houseRight2;

	@Column(name="house_status")
	private double houseStatus;

	private int housing;

	private int income;

	private int late;

	private String linkman1;

	private String linkman2;

	private int marry;

	@Column(name="mate_address")
	private String mateAddress;

	@Column(name="mate_income")
	private double mateIncome;

	@Column(name="mate_name")
	private String mateName;

	@Column(name="mate_office")
	private int mateOffice;

	@Column(name="mate_phone")
	private String matePhone;

	@Column(name="mate_salary")
	private double mateSalary;

	@Column(name="mate_tel")
	private String mateTel;

	@Column(name="mate_type")
	private int mateType;

	private String name;

	private String phone;

	private String phone1;

	private String post;

	@Column(name="private_commerceid")
	private String privateCommerceid;

	@Column(name="private_date")
	private Date privateDate;

	@Column(name="private_employee")
	private int privateEmployee;

	@Column(name="private_income")
	private double privateIncome;

	@Column(name="private_place")
	private String privatePlace;

	@Column(name="private_rent")
	private double privateRent;

	@Column(name="private_taxid")
	private String privateTaxid;

	@Column(name="private_type")
	private int privateType;

	@Column(name="private_term")
	private int privateTerm;

	private int province;

	private int relation1;

	private int relation2;

	private int shebao;

	private String shebaoid;

	private int status;

	private String tel;

	private String tel1;
	
	@OneToOne(fetch=FetchType.LAZY)   
	@JoinColumn(name="user_id", updatable=false)  
	private User user;
	
	public Userinfo() {
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getArea() {
		return area;
	}

	public void setArea(int area) {
		this.area = area;
	}

	public int getCar() {
		return car;
	}

	public void setCar(int car) {
		this.car = car;
	}

	public int getChild() {
		return child;
	}

	public void setChild(int child) {
		this.child = child;
	}

	public int getCity() {
		return city;
	}

	public void setCity(int city) {
		this.city = city;
	}

	public String getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}

	public int getCompanyIndustry() {
		return companyIndustry;
	}

	public void setCompanyIndustry(int companyIndustry) {
		this.companyIndustry = companyIndustry;
	}

	public int getCompanyJibie() {
		return companyJibie;
	}

	public void setCompanyJibie(int companyJibie) {
		this.companyJibie = companyJibie;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public int getCompanyOffice() {
		return companyOffice;
	}

	public void setCompanyOffice(int companyOffice) {
		this.companyOffice = companyOffice;
	}

	public String getCompanyReamrk() {
		return companyReamrk;
	}

	public void setCompanyReamrk(String companyReamrk) {
		this.companyReamrk = companyReamrk;
	}

	public String getCompanyTel() {
		return companyTel;
	}

	public void setCompanyTel(String companyTel) {
		this.companyTel = companyTel;
	}

	public String getCompanyType() {
		return companyType;
	}

	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}

	public String getCompanyWeburl() {
		return companyWeburl;
	}

	public void setCompanyWeburl(String companyWeburl) {
		this.companyWeburl = companyWeburl;
	}

	public Date getCompanyWorktime1() {
		return companyWorktime1;
	}

	public void setCompanyWorktime1(Date companyWorktime1) {
		this.companyWorktime1 = companyWorktime1;
	}

	public Date getCompanyWorktime2() {
		return companyWorktime2;
	}

	public void setCompanyWorktime2(Date companyWorktime2) {
		this.companyWorktime2 = companyWorktime2;
	}

	public String getCompanyWorkyear() {
		return companyWorkyear;
	}

	public void setCompanyWorkyear(String companyWorkyear) {
		this.companyWorkyear = companyWorkyear;
	}

	public int getEducation() {
		return education;
	}

	public void setEducation(int education) {
		this.education = education;
	}

	public int getEducationRecord() {
		return educationRecord;
	}

	public void setEducationRecord(int educationRecord) {
		this.educationRecord = educationRecord;
	}

	public String getEducationSchool() {
		return educationSchool;
	}

	public void setEducationSchool(String educationSchool) {
		this.educationSchool = educationSchool;
	}

	public String getEducationStudy() {
		return educationStudy;
	}

	public void setEducationStudy(String educationStudy) {
		this.educationStudy = educationStudy;
	}

	public Date getEducationTime1() {
		return educationTime1;
	}

	public void setEducationTime1(Date educationTime1) {
		this.educationTime1 = educationTime1;
	}

	public Date getEducationTime2() {
		return educationTime2;
	}

	public void setEducationTime2(Date educationTime2) {
		this.educationTime2 = educationTime2;
	}

	public double getFinaceCreditcard() {
		return finaceCreditcard;
	}

	public void setFinaceCreditcard(double finaceCreditcard) {
		this.finaceCreditcard = finaceCreditcard;
	}

	public double getFinanceAmount() {
		return financeAmount;
	}

	public void setFinanceAmount(double financeAmount) {
		this.financeAmount = financeAmount;
	}

	public int getFinanceCar() {
		return financeCar;
	}

	public void setFinanceCar(int financeCar) {
		this.financeCar = financeCar;
	}

	public double getFinanceCaramount() {
		return financeCaramount;
	}

	public void setFinanceCaramount(double financeCaramount) {
		this.financeCaramount = financeCaramount;
	}

	public int getFinanceProperty() {
		return financeProperty;
	}

	public void setFinanceProperty(int financeProperty) {
		this.financeProperty = financeProperty;
	}

	public double getFinanceRepayment() {
		return financeRepayment;
	}

	public void setFinanceRepayment(double financeRepayment) {
		this.financeRepayment = financeRepayment;
	}

	public Date getHouseYear() {
		return houseYear;
	}

	public void setHouseYear(Date houseYear) {
		this.houseYear = houseYear;
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

	public double getHouseBalance() {
		return houseBalance;
	}

	public void setHouseBalance(double houseBalance) {
		this.houseBalance = houseBalance;
	}

	public String getHouseBank() {
		return houseBank;
	}

	public void setHouseBank(String houseBank) {
		this.houseBank = houseBank;
	}

	public String getHouseHolder1() {
		return houseHolder1;
	}

	public void setHouseHolder1(String houseHolder1) {
		this.houseHolder1 = houseHolder1;
	}

	public String getHouseHolder2() {
		return houseHolder2;
	}

	public void setHouseHolder2(String houseHolder2) {
		this.houseHolder2 = houseHolder2;
	}

	public double getHouseLoanprice() {
		return houseLoanprice;
	}

	public void setHouseLoanprice(double houseLoanprice) {
		this.houseLoanprice = houseLoanprice;
	}

	public int getHouseLoanyear() {
		return houseLoanyear;
	}

	public void setHouseLoanyear(int houseLoanyear) {
		this.houseLoanyear = houseLoanyear;
	}

	public double getHouseRight1() {
		return houseRight1;
	}

	public void setHouseRight1(double houseRight1) {
		this.houseRight1 = houseRight1;
	}

	public double getHouseRight2() {
		return houseRight2;
	}

	public void setHouseRight2(double houseRight2) {
		this.houseRight2 = houseRight2;
	}

	public double getHouseStatus() {
		return houseStatus;
	}

	public void setHouseStatus(double houseStatus) {
		this.houseStatus = houseStatus;
	}

	public int getHousing() {
		return housing;
	}

	public void setHousing(int housing) {
		this.housing = housing;
	}

	public int getIncome() {
		return income;
	}

	public void setIncome(int income) {
		this.income = income;
	}

	public int getLate() {
		return late;
	}

	public void setLate(int late) {
		this.late = late;
	}

	public String getLinkman1() {
		return linkman1;
	}

	public void setLinkman1(String linkman1) {
		this.linkman1 = linkman1;
	}

	public String getLinkman2() {
		return linkman2;
	}

	public void setLinkman2(String linkman2) {
		this.linkman2 = linkman2;
	}

	public int getMarry() {
		return marry;
	}

	public void setMarry(int marry) {
		this.marry = marry;
	}

	public String getMateAddress() {
		return mateAddress;
	}

	public void setMateAddress(String mateAddress) {
		this.mateAddress = mateAddress;
	}

	public double getMateIncome() {
		return mateIncome;
	}

	public void setMateIncome(double mateIncome) {
		this.mateIncome = mateIncome;
	}

	public String getMateName() {
		return mateName;
	}

	public void setMateName(String mateName) {
		this.mateName = mateName;
	}

	public int getMateOffice() {
		return mateOffice;
	}

	public void setMateOffice(int mateOffice) {
		this.mateOffice = mateOffice;
	}

	public String getMatePhone() {
		return matePhone;
	}

	public void setMatePhone(String matePhone) {
		this.matePhone = matePhone;
	}

	public double getMateSalary() {
		return mateSalary;
	}

	public void setMateSalary(double mateSalary) {
		this.mateSalary = mateSalary;
	}

	public String getMateTel() {
		return mateTel;
	}

	public void setMateTel(String mateTel) {
		this.mateTel = mateTel;
	}

	public int getMateType() {
		return mateType;
	}

	public void setMateType(int mateType) {
		this.mateType = mateType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhone1() {
		return phone1;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}

	public String getPrivateCommerceid() {
		return privateCommerceid;
	}

	public void setPrivateCommerceid(String privateCommerceid) {
		this.privateCommerceid = privateCommerceid;
	}

	public Date getPrivateDate() {
		return privateDate;
	}

	public void setPrivateDate(Date privateDate) {
		this.privateDate = privateDate;
	}

	public int getPrivateEmployee() {
		return privateEmployee;
	}

	public void setPrivateEmployee(int privateEmployee) {
		this.privateEmployee = privateEmployee;
	}

	public double getPrivateIncome() {
		return privateIncome;
	}

	public void setPrivateIncome(double privateIncome) {
		this.privateIncome = privateIncome;
	}

	public String getPrivatePlace() {
		return privatePlace;
	}

	public void setPrivatePlace(String privatePlace) {
		this.privatePlace = privatePlace;
	}

	public double getPrivateRent() {
		return privateRent;
	}

	public void setPrivateRent(double privateRent) {
		this.privateRent = privateRent;
	}

	public String getPrivateTaxid() {
		return privateTaxid;
	}

	public void setPrivateTaxid(String privateTaxid) {
		this.privateTaxid = privateTaxid;
	}

	public int getPrivateType() {
		return privateType;
	}

	public void setPrivateType(int privateType) {
		this.privateType = privateType;
	}

	public int getPrivateTerm() {
		return privateTerm;
	}

	public void setPrivateTerm(int privateTerm) {
		this.privateTerm = privateTerm;
	}

	public int getProvince() {
		return province;
	}

	public void setProvince(int province) {
		this.province = province;
	}

	public int getRelation1() {
		return relation1;
	}

	public void setRelation1(int relation1) {
		this.relation1 = relation1;
	}

	public int getRelation2() {
		return relation2;
	}

	public void setRelation2(int relation2) {
		this.relation2 = relation2;
	}

	public int getShebao() {
		return shebao;
	}

	public void setShebao(int shebao) {
		this.shebao = shebao;
	}

	public String getShebaoid() {
		return shebaoid;
	}

	public void setShebaoid(String shebaoid) {
		this.shebaoid = shebaoid;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getTel1() {
		return tel1;
	}

	public void setTel1(String tel1) {
		this.tel1 = tel1;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Map getAllStatus(){
		String building[]= new String[]{"houseAddress","houseArea","houseYear"};
		String company[] = new String[]{"companyName","companyType","companyIndustry","companyOffice","companyJibie","companyWorktime1","companyWorktime2","companyWorkyear","companyTel","companyAddress","companyWeburl","companyReamrk"};
		String firm[] = new String[]{"privateType","privateDate","privatePlace","privateRent","privateTerm","privateTaxid","privateCommerceid","privateIncome","privateEmployee"};
		String finance[] = new String[]{"financeRepayment","financeProperty","financeAmount","financeCar","financeCaramount"};
		String contact[] = new String[]{"tel","phone","post","address","province","city","area","linkman1","relation1","tel1","phone1"};
		String mate[] = new String[]{"mateName","mateSalary","matePhone","mateTel","mateType","mateOffice","mateAddress","mateIncome"};
		String edu[]  = new String[]{"educationRecord","educationSchool","educationStudy","educationTime1","educationTime2"};
	//	String job[]  = new String[]{"interest","others","experience"};
		String info[][]=new String[][]{building,company,firm,contact,mate,edu};
		
		boolean building_status=false;
		boolean company_status=false;
		boolean firm_status=false;
		boolean finance_status=false;
		boolean contact_status=false;
		boolean mate_status=false;
		boolean edu_status=false;
		//boolean job_status=false;
		
		building_status=getStatus(building);
		company_status=getStatus(company);
		firm_status=getStatus(firm);
		finance_status=getStatus(finance);
		contact_status=getStatus(contact);
		mate_status=getStatus(mate);
		edu_status=getStatus(edu);
		//job_status=getStatus(job);
		double rate = calculateRate(info);
		Map map = new HashMap();
		map.put("building_status",building_status);
		map.put("company_status",company_status);
		map.put("firm_status",firm_status);
		map.put("finance_status",finance_status);
		map.put("contact_status",contact_status);
		map.put("mate_status",mate_status);
		map.put("edu_status",edu_status);
		map.put("rate",rate);
		return map;
	}
	
	private boolean getStatus(String[] args){
		boolean status = true;
		for(int i=0;i<args.length;i++){
			Object ret=ReflectUtils.invokeGetMethod(this.getClass(), this, args[i]);
			if (ret== null){
				status=false;
				break;
			}
		}
		return status;
	}
	
	private double calculateRate(String[][] info){

		double rate;
		int count=0,total=0;
		for(int i=0;i<info.length;i++){
			total+=info[i].length;
			for(int j=0;j<info[i].length;j++){
				Object ret=ReflectUtils.invokeGetMethod(this.getClass(), this, info[i][j]);
				if (ret!= null){
					count++;
				}
			}
		}
		if(count==0||total==0){
			rate=0;
		}
		rate=(count+0.0)/total;
		return rate;
	}
	
	public String getProvinceStr(){
		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		AreaDao areaDao = (AreaDao) wac.getBean("areaDao"); 
		if(this.province == 0){
			return "未添加";
		}
		Area area = areaDao.find(this.province);
		if(area != null ){
			return area.getName();
		}
		return "未添加";
	}
	
	public String getCompanyIndustryStr(){
		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		LinkageDao linkageDao = (LinkageDao) wac.getBean("linkageDao"); 
		if(this.companyIndustry == 0){
			return "未添加";
		}
		Linkage linkage = linkageDao.find(this.companyIndustry);
		if(linkage != null ){
			return linkage.getName();
		}
		return "未添加";
	}
	
}