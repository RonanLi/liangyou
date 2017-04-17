package com.liangyou.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.liangyou.context.Global;
import com.liangyou.dao.BorrowTenderDao;
import com.liangyou.exception.BussinessException;
import com.liangyou.util.StringUtils;
import com.liangyou.web.action.admin.AdminUserinfoAction;


/**
 * The persistent class for the user database table.
 * 
 */
@Entity
@Table(name = Global.TABLE_PREFIX +  "user")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	public User() {
	}

	public User(long userId) {
		super();
		this.userId = userId;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="user_id")
	private long userId;

	private String addip;

	private String address;
	@Temporal(TemporalType.TIMESTAMP)
	private Date addtime;

	private String answer;

	@ManyToOne(fetch=FetchType.LAZY)  
	private Area area;

	@Column(name="avatar_status")
	private int avatarStatus;

	private String birthday;

	@Column(name="card_id")
	private String cardId;

	@Column(name="card_pic1")
	private String cardPic1;

	@Column(name="card_pic2")
	private String cardPic2;

	@Column(name="card_type")
	private String cardType;

	@Column(name="card_off")//身份证到期时间
	private String cardOff;

	@ManyToOne(fetch=FetchType.LAZY)  
	private Area city;

	private String email;

	@Column(name="email_status")
	private int emailStatus;

	private String hongbao;

	@Column(name="invite_money")
	private String inviteMoney;

	private int islock;

	private String lastip;

	@Temporal(TemporalType.TIMESTAMP)
	private Date lasttime;

	@Temporal(TemporalType.TIMESTAMP)
	private Date logintime;

	@ManyToOne(fetch=FetchType.LAZY)
	private Linkage nation; 

	private String password;

	private String paypassword;

	private String phone;

	@Column(name="phone_status")
	private int phoneStatus;

	private String privacy;

	@ManyToOne(fetch=FetchType.LAZY)  
	private Area province;

	private String question;

	@Column(name="real_status")
	private int realStatus;

	private String realname;

	private String remind;

	@Column(name="scene_status")
	private int sceneStatus;

	@Column(name="serial_id")
	private String serialId;

	@Column(name="serial_status")
	private String serialStatus;

	private String sex;

	private int status;

	private String upip;

	private String uptime;

	private String username;

	private String qq;

	private String wangwang;

	private String tel;
	
	@Column(name="from_be")
	private String fromBe;//注册来源
	
	private String channel;
	
	@Column(name="channel_code")
	private String channelCode;
	

	//v1.8.0.1 TGPROJECT-14 lx 2014-04-01 start
	@Column(name="activity_status")
	private int activityStatus;
	public int getActivityStatus() {
		return activityStatus;
	}

	public void setActivityStatus(int activityStatus) {
		this.activityStatus = activityStatus;
	}
	//v1.8.0.1 TGPROJECT-14 lx 2014-04-01 end

	//v1.8.0.1 TGPROJECT-42 lx 2014-04-01 start
	@Column(name="api_loan_authorize")
	private int apiLoanAuthorize;//双乾授权（投标、还款、扣除管理费授权）

	public int getApiLoanAuthorize() {
		return apiLoanAuthorize;
	}

	public void setApiLoanAuthorize(int apiLoanAuthorize) {
		this.apiLoanAuthorize = apiLoanAuthorize;
	}
	//v1.8.0.1 TGPROJECT-42 lx 2014-04-01 stop

	@Column(name="video_status")
	private int videoStatus;
	@Column(name="api_id")
	private String apiId;//钱多多标识

	@Column(name="api_status")
	private int apiStatus;  // 1 激活 ， 0未激活。

	@Column(name="api_usercust_id")
	private String apiUsercustId;  // 用户客户号

	@OneToOne(cascade={CascadeType.PERSIST},fetch=FetchType.LAZY, mappedBy="user")  //查询推荐用户。
	private InviteUser inviteUser;

	@OneToOne(cascade={CascadeType.PERSIST},fetch=FetchType.LAZY, mappedBy="user")  
	private Account account;
	
	//wsl 满标前补偿金功能【心意贷】2014-08-25 start
	@OneToOne(cascade={CascadeType.PERSIST},fetch=FetchType.LAZY, mappedBy="user")  
	private CompensationAccount compensationAccount;
	//wsl 满标前补偿金功能【心意贷】2014-08-25 end
	
	@OneToMany(mappedBy="user", fetch=FetchType.LAZY)
	private List<AccountBank> accountBanks;

	@OneToMany(mappedBy="user", fetch=FetchType.LAZY)
	private List<AccountCash> accountCashs;

	@OneToMany(mappedBy="user", fetch=FetchType.LAZY)
	private List<AccountLog> accountLogs;

	@OneToMany(mappedBy="user", fetch=FetchType.LAZY)
	private List<AccountRecharge> accountRecharges;

	@OneToMany(mappedBy="user", fetch=FetchType.LAZY)
	private List<Attestation> attestations;

	@OneToOne(cascade={CascadeType.PERSIST}, fetch=FetchType.LAZY, mappedBy="user")     
	private Credit credit;

	@OneToOne(cascade={CascadeType.PERSIST}, fetch=FetchType.LAZY, mappedBy="user")   
	private UserAmount userAmount;

	@OneToMany(mappedBy="user")
	private List<UserAmountApply> userAmountapplies;

	@OneToOne(cascade={CascadeType.PERSIST}, fetch=FetchType.LAZY, mappedBy="user")   
	private UserCache userCache;

	@OneToOne(cascade={CascadeType.PERSIST}, fetch=FetchType.LAZY, mappedBy="user")   
	private UserCredit userCredit;

	@OneToOne(fetch=FetchType.LAZY, mappedBy="user")
	private Userinfo userinfo;

	@ManyToOne()
	@JoinColumn(name="type_id")
	private UserType userType;

	@OneToMany(mappedBy="user")
	private List<Borrow> borrows;

	@Column(name="ywd_user_no")
	private String ywdUserNo;//义乌贷活动用户的编号。

	// v1.8.0.3_u3  XINHEHANG-66  wujing 2014-06-20  start
	//v1.8.0.4_u3   TGPROJECT-340   qinjun  2014-06-23 start
	private String customerType;   //客户类型，合信贷添加
	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}
	//v1.8.0.4_u3   TGPROJECT-340   qinjun  2014-06-23 end
	// v1.8.0.3_u3  XINHEHANG-66  wujing 2014-06-20  end 

	// v1.8.0.3_u3  TGPROJECT-332  qinjun 2014-06-06  start 
	@OneToOne(fetch=FetchType.LAZY, mappedBy="user") 
	private UserProperty userProperty;
	public UserProperty getUserProperty() {
		return userProperty;
	}
	public void setUserProperty(UserProperty userProperty) {
		this.userProperty = userProperty;
	}
	//v1.8.0.3_u3  TGPROJECT-332  qinjun 2014-06-06 end

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getAddip() {
		return this.addip;
	}

	public void setAddip(String addip) {
		this.addip = addip;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getAddtime() {
		return this.addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public String getAnswer() {
		return this.answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}


	public Area getArea() {
		return this.area;
	}


	public void setArea(Area area) {
		this.area = area;
	}

	public int getAvatarStatus() {
		return this.avatarStatus;
	}

	public void setAvatarStatus(int avatarStatus) {
		this.avatarStatus = avatarStatus;
	}

	public String getBirthday() {
		return this.birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	//v1.8.0.4 GPROJECT-390 wsl 2014-08-07 start
	public String getBirthday(String cardId) throws BussinessException{
		StringBuffer tempStr=null;
		if(cardId!=null&&cardId.trim().length()>0){
			if(cardId.trim().length()==15){
				tempStr=new StringBuffer(cardId.substring(6, 12));
				tempStr.insert(4, '-');
				tempStr.insert(2, '-');
				tempStr.insert(0, "19");
			}else if(cardId.trim().length()==18){
				tempStr=new StringBuffer(cardId.substring(6, 14));
				tempStr.insert(6, '-');
				tempStr.insert(4, '-');
			}
		}
		if(tempStr!=null&&tempStr.toString().trim().length()>0){
			try{
				birthday=tempStr.toString();
			}catch(Exception e){
				throw new BussinessException("输入的身份证错误，不能转换为相应的出生日期");
			}
		}
		return birthday;
	}
	//v1.8.0.4 GPROJECT-390 wsl 2014-08-07 end
	public String getCardId() {
		return this.cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getCardPic1() {
		return this.cardPic1;
	}

	public void setCardPic1(String cardPic1) {
		this.cardPic1 = cardPic1;
	}

	public String getCardPic2() {
		return this.cardPic2;
	}

	public void setCardPic2(String cardPic2) {
		this.cardPic2 = cardPic2;
	}

	public String getCardType() {
		return this.cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}


	public Area getCity() {
		return this.city;
	}

	public void setCity(Area city) {
		this.city = city;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getEmailStatus() {
		return this.emailStatus;
	}

	public void setEmailStatus(int emailStatus) {
		this.emailStatus = emailStatus;
	}

	public String getHongbao() {
		return this.hongbao;
	}

	public void setHongbao(String hongbao) {
		this.hongbao = hongbao;
	}

	public String getInviteMoney() {
		return this.inviteMoney;
	}

	public void setInviteMoney(String inviteMoney) {
		this.inviteMoney = inviteMoney;
	}

	public InviteUser getInviteUser() {
		return inviteUser;
	}

	public void setInviteUser(InviteUser inviteUser) {
		this.inviteUser = inviteUser;
	}

	public int getIslock() {
		return this.islock;
	}

	public void setIslock(int islock) {
		this.islock = islock;
	}

	public String getLastip() {
		return this.lastip;
	}

	public void setLastip(String lastip) {
		this.lastip = lastip;
	}

	public Date getLasttime() {
		return this.lasttime;
	}

	public void setLasttime(Date lasttime) {
		this.lasttime = lasttime;
	}

	public Date getLogintime() {
		return this.logintime;
	}

	public void setLogintime(Date logintime) {
		this.logintime = logintime;
	}


	public Linkage getNation() {
		return nation;
	}


	public void setNation(Linkage nation) {
		this.nation = nation;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPaypassword() {
		return this.paypassword;
	}

	public void setPaypassword(String paypassword) {
		this.paypassword = paypassword;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getPhoneStatus() {
		return this.phoneStatus;
	}

	public void setPhoneStatus(int phoneStatus) {
		this.phoneStatus = phoneStatus;
	}

	public String getPrivacy() {
		return this.privacy;
	}

	public void setPrivacy(String privacy) {
		this.privacy = privacy;
	}


	public Area getProvince() {
		return this.province;
	}


	public void setProvince(Area province) {
		this.province = province;
	}

	public String getQuestion() {
		return this.question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public int getRealStatus() {
		return this.realStatus;
	}

	public void setRealStatus(int realStatus) {
		this.realStatus = realStatus;
	}

	public String getRealname() {
		return this.realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getRemind() {
		return this.remind;
	}

	public void setRemind(String remind) {
		this.remind = remind;
	}

	public int getSceneStatus() {
		return this.sceneStatus;
	}

	public void setSceneStatus(int sceneStatus) {
		this.sceneStatus = sceneStatus;
	}

	public String getSerialId() {
		return this.serialId;
	}

	public void setSerialId(String serialId) {
		this.serialId = serialId;
	}

	public String getSerialStatus() {
		return this.serialStatus;
	}

	public void setSerialStatus(String serialStatus) {
		this.serialStatus = serialStatus;
	}

	public String getSex() {
		return this.sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}


	public String getUpip() {
		return this.upip;
	}

	public void setUpip(String upip) {
		this.upip = upip;
	}

	public String getUptime() {
		return this.uptime;
	}

	public void setUptime(String uptime) {
		this.uptime = uptime;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getVideoStatus() {
		return this.videoStatus;
	}

	public void setVideoStatus(int videoStatus) {
		this.videoStatus = videoStatus;
	}


	public Account getAccount() {
		return this.account;
	}


	public void setAccount(Account account) {
		this.account = account;
	}


	public List<AccountBank> getAccountBanks() {
		return this.accountBanks;
	}


	public void setAccountBanks(List<AccountBank> accountBanks) {
		this.accountBanks = accountBanks;
	}


	public AccountBank addAccountBank(AccountBank accountBank) {
		getAccountBanks().add(accountBank);
		accountBank.setUser(this);

		return accountBank;
	}


	public AccountBank removeAccountBank(AccountBank accountBank) {
		getAccountBanks().remove(accountBank);
		accountBank.setUser(null);

		return accountBank;
	}


	public List<AccountCash> getAccountCashs() {
		return this.accountCashs;
	}


	public void setAccountCashs(List<AccountCash> accountCashs) {
		this.accountCashs = accountCashs;
	}


	public AccountCash addAccountCash(AccountCash accountCash) {
		getAccountCashs().add(accountCash);
		accountCash.setUser(this);

		return accountCash;
	}


	public AccountCash removeAccountCash(AccountCash accountCash) {
		getAccountCashs().remove(accountCash);
		accountCash.setUser(null);

		return accountCash;
	}


	public List<AccountLog> getAccountLogs() {
		return this.accountLogs;
	}


	public void setAccountLogs(List<AccountLog> accountLogs) {
		this.accountLogs = accountLogs;
	}


	public AccountLog addAccountLog(AccountLog accountLog) {
		getAccountLogs().add(accountLog);
		accountLog.setUser(this);

		return accountLog;
	}


	public AccountLog removeAccountLog(AccountLog accountLog) {
		getAccountLogs().remove(accountLog);
		accountLog.setUser(null);

		return accountLog;
	}


	public List<AccountRecharge> getAccountRecharges() {
		return this.accountRecharges;
	}


	public void setAccountRecharges(List<AccountRecharge> accountRecharges) {
		this.accountRecharges = accountRecharges;
	}


	public AccountRecharge addAccountRecharge(AccountRecharge accountRecharge) {
		getAccountRecharges().add(accountRecharge);
		accountRecharge.setUser(this);

		return accountRecharge;
	}


	public AccountRecharge removeAccountRecharge(AccountRecharge accountRecharge) {
		getAccountRecharges().remove(accountRecharge);
		accountRecharge.setUser(null);

		return accountRecharge;
	}


	public List<Attestation> getAttestations() {
		return this.attestations;
	}


	public void setAttestations(List<Attestation> attestations) {
		this.attestations = attestations;
	}


	public Attestation addAttestation(Attestation attestation) {
		getAttestations().add(attestation);
		attestation.setUser(this);

		return attestation;
	}


	public Attestation removeAttestation(Attestation attestation) {
		getAttestations().remove(attestation);
		attestation.setUser(null);

		return attestation;
	}

	public Credit getCredit() {
		return this.credit;
	}


	public void setCredit(Credit credit) {
		this.credit = credit;
	}


	public UserAmount getUserAmount() {
		return userAmount;
	}


	public void setUserAmount(UserAmount userAmount) {
		this.userAmount = userAmount;
	}


	public List<UserAmountApply> getUserAmountapplies() {
		return this.userAmountapplies;
	}


	public void setUserAmountapplies(List<UserAmountApply> userAmountapplies) {
		this.userAmountapplies = userAmountapplies;
	}


	public UserAmountApply addUserAmountapply(UserAmountApply userAmountapply) {
		getUserAmountapplies().add(userAmountapply);
		userAmountapply.setUser(this);

		return userAmountapply;
	}


	public UserAmountApply removeUserAmountapply(UserAmountApply userAmountapply) {
		getUserAmountapplies().remove(userAmountapply);
		userAmountapply.setUser(null);

		return userAmountapply;
	}


	public UserCache getUserCache() {
		return this.userCache;
	}


	public void setUserCache(UserCache userCache) {
		this.userCache = userCache;
	}


	public Userinfo getUserinfo() {
		return userinfo;
	}


	public void setUserinfo(Userinfo userinfo) {
		this.userinfo = userinfo;
	}


	public UserType getUserType() {
		return userType;
	}


	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public String getApiId() {
		return apiId;
	}

	public void setApiId(String api) {
		this.apiId = api;
	}

	public int getApiStatus() {
		return apiStatus;
	}

	public void setApiStatus(int apiStatus) {
		this.apiStatus = apiStatus;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getWangwang() {
		return wangwang;
	}

	public void setWangwang(String wangwang) {
		this.wangwang = wangwang;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}


	public List<Borrow> getBorrows() {
		return borrows;
	}


	public void setBorrows(List<Borrow> borrows) {
		this.borrows = borrows;
	}

	public String getCardOff() {
		return cardOff;
	}

	public void setCardOff(String cardOff) {
		this.cardOff = cardOff;
	}

	public String getSexStr(){
		if(this.sex == null){
			return "-";
		}else if(this.sex.equals("1")){
			return "男";
		}else if(this.sex.equals("2")){
			return "女";
		}else{
			return "-";
		}
	}

	public String getStatusStr(){
		if(this.islock ==0){
			return "开通";
		}else{
			return "关闭";
		}
	}

	public void hideChar(){
		if(StringUtils.isNull(this.getRealStatus()).equals("1")){
			if(getCardId()!=null){
				setCardId(StringUtils.hideLastChar(getCardId(), 4));
			}
			if(getRealname()!=null){
				int len=2;
				if(getRealname().length()<3) len=1;
				setRealname(StringUtils.hideLastChar(getRealname(), len));
			}
		}
		if(StringUtils.isNull(getPhoneStatus()).equals("1")){
			if(this.getPhone()!=null){
				setPhone(StringUtils.hideLastChar(getPhone(), 4));
			}
		}
		if(StringUtils.isNull(getEmailStatus()).equals("1")){
			if(this.getEmail()!=null){
				String[] temp=getEmail().split("@");
				if(temp.length>1){
					String newEmail=StringUtils.hideChar(temp[0],temp.length)+"@"+temp[1];
					setEmail(newEmail);
				}
			}
		}
	}

	public int getStarRank(){
		if(this.credit != null){
			return this.credit.findStarRank().getRank();
		}else{
			return 0;
		}
	}

	public String getCreditRank(){
		if(this.credit != null){
			return this.credit.findCreditRank().getName().toUpperCase();
		}else{
			return null;
		}

	}

	public String getApiUsercustId() {
		return apiUsercustId;
	}

	public void setApiUsercustId(String apiUsercustId) {
		this.apiUsercustId = apiUsercustId;
	}

	public UserCredit getUserCredit() {
		return userCredit;
	}

	public void setUserCredit(UserCredit userCredit) {
		this.userCredit = userCredit;
	}

	public String getYwdUserNo() {
		return ywdUserNo;
	}

	public void setYwdUserNo(String ywdUserNo) {
		this.ywdUserNo = ywdUserNo;
	}

	public CompensationAccount getCompensationAccount() {
		return compensationAccount;
	}

	public void setCompensationAccount(CompensationAccount compensationAccount) {
		this.compensationAccount = compensationAccount;
	}

	
	public String getFromBe() {
		return fromBe;
	}

	public void setFromBe(String fromBe) {
		this.fromBe = fromBe;
	}
	
	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	/**
	 * 根据时间段，获取投资的总和。
	 */
	public double countUserTenderMoney(){
		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		BorrowTenderDao borrowTenderDao = (BorrowTenderDao) wac.getBean("borrowTenderDao");  
		double d = borrowTenderDao.getTenderMoneyByTime(AdminUserinfoAction.getStartTime(), AdminUserinfoAction.getEndTime(), getUserId());
		return d;
	}

	public int countUserTender(){
		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		BorrowTenderDao borrowTenderDao = (BorrowTenderDao) wac.getBean("borrowTenderDao");  
		int d = borrowTenderDao.countUserTender(AdminUserinfoAction.getStartTime(), AdminUserinfoAction.getEndTime(),getUserId());
		return d;
	}

	public double countUserCollectionMoney(){
		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		BorrowTenderDao borrowTenderDao = (BorrowTenderDao) wac.getBean("borrowTenderDao"); 
		//v1.8.0.4_u1 TGPROJECT-249 lx start
		double d = borrowTenderDao.countUserCollectionMoney("", "", getUserId());
		//v1.8.0.4_u1 TGPROJECT-249 lx end
		return d;
	}
}
