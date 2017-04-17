package com.liangyou.tool.javamail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.log4j.Logger;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.liangyou.context.Global;
import com.liangyou.dao.MsgTemplateDao;
import com.liangyou.dao.OperateLogDao;
import com.liangyou.domain.User;
import com.liangyou.model.SystemInfo;
import com.liangyou.tool.coder.BASE64Encoder;
import com.liangyou.tool.coder.MD5;
import com.liangyou.util.DateUtils;
import com.liangyou.util.StringUtils;

public class Mail {
	
	private static Logger logger = Logger.getLogger(Mail.class);
	
	private String host;
	private String from;
	private String to ;
	private String subject ;
	private String body ;
	private EmailAutherticator auth;
	private static Mail mail;
	
	 static {
		 mail = new Mail();
	 }
	
	private Mail() {
		init();
	}
	
	private Mail(EmailAutherticator auth,String host,String subject,String from){
		this.setAuth(auth);
		this.setHost(host);
		this.setSubject(subject);
		this.setFrom(from);
	}

	private void init(){
		SystemInfo info=Global.SYSTEMINFO;
		EmailAutherticator auth = new EmailAutherticator(
				info.getValue("email_email"),
				info.getValue("email_pwd"));
		host=info.getValue("email_host");
		from=auth.getUsername();
		logger.info("From："+from);
		subject=info.getValue("email_from_name");
		logger.info("数据库读取："+subject);
		this.setAuth(auth);
		this.setHost(host);
		this.setSubject(subject);
		this.setFrom(from);
	}
	
	public static Mail getInstance(){
		return mail;
	}
	
	public String transferChinese(String strText)   {   
		try{   
			strText = MimeUtility.encodeText(new String(strText.getBytes(), "utf-8"), "utf-8", "B");   
        }catch(Exception e){   
            logger.error(e);   
        }   
        return strText;   
    }
	
	public void sendMail() throws Exception {
			Properties props = new Properties();
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.auth", "true");
			System.out.println(props);
			Session session = Session.getDefaultInstance(props, auth);

			MimeMessage message = new MimeMessage(session);
			message.setContent("Hello", "text/plain");
			logger.info(subject);
			message.setSubject(subject,"utf-8");// 设置邮件主题   
			message.setSentDate(new Date());// 设置邮件发送时期
			Address address = new InternetAddress(from, subject,"utf-8");
			
			message.setFrom(address);// 设置邮件发送者的地址
			Address toaddress = new InternetAddress(to);// 设置邮件接收者的地址
			message.addRecipient(Message.RecipientType.TO, toaddress);
			// 创建一个包含HTML内容的MimeBodyPart    
			Multipart mainPart = new MimeMultipart();    
		    BodyPart html = new MimeBodyPart();     
		    html.setContent(body, "text/html; charset=utf-8");    
		    mainPart.addBodyPart(html);    
		    // 将MiniMultipart对象设置为邮件内容
		    message.setContent(mainPart);    
			
			System.out.println(message);
			logger.debug("TO:"+to);
			Transport.send(message);
			System.out.println("Send Mail Ok!");
	}
	
	public void sendMail(String to,String subject,String content) throws Exception {
		this.setTo(to);
		this.setSubject(subject);
		this.setBody(content);
		this.sendMail();
	}
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public EmailAutherticator getAuth() {
		return auth;
	}
	public void setAuth(EmailAutherticator auth) {
		this.auth = auth;
	}
	
	public void readMailCodeMsg(){//邮件发送验证码内容
		String msg = "";
		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		MsgTemplateDao msgTemplateDao = (MsgTemplateDao) wac.getBean("msgTemplateDao");  
		msg = msgTemplateDao.find(2).getContent();
		setBody(msg);
	}
	
	public void readActiveMailMsg(){//激活邮件模版内容
		String msg = "";
		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		MsgTemplateDao msgTemplateDao = (MsgTemplateDao) wac.getBean("msgTemplateDao");  
		msg = msgTemplateDao.find(1).getContent();
		setBody(msg);
	}
	
	public void readGetpwdMailMsg(){//获取密码邮件内容
		String msg = "";
		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		MsgTemplateDao msgTemplateDao = (MsgTemplateDao) wac.getBean("msgTemplateDao");  
		msg = msgTemplateDao.find(3).getContent();
		setBody(msg);
	}
	
	private void readMsg(String filename){
		StringBuffer sb=new StringBuffer();
		InputStream fis=Mail.class.getResourceAsStream(filename);
		InputStreamReader isr=null;
		try {
			isr=new InputStreamReader(fis,"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			logger.error("读取文件遇见不正确的文件编码！");
		}
		BufferedReader br=new BufferedReader(isr);
		String line="";
		try {
			while((line=br.readLine())!=null){
				sb.append(line);
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		String msg=sb.toString();
		logger.info(msg);
		setBody(msg);
	}
	
	public String getdecodeIdStr(User user){
		String chars="0123456789qwertyuiopasdfghjklmnbvcxz-=~!@#$%^*+-._/*<>|";
		int length=chars.length();
		StringBuffer url=new StringBuffer();
		StringBuffer rancode=new StringBuffer();
		String timeStr=System.currentTimeMillis()/1000+"";
		String userIDAddtime = user.getUserId() + "" + DateUtils.getYYYYMMddHHmmss(user.getAddtime());
		
		MD5 md5=new MD5();
		userIDAddtime =  md5.getMD5ofStr(userIDAddtime);
		url.append(user.getUserId()).append(",").append(userIDAddtime).append(",").append(timeStr).append(",");
		for(int i=0;i<10;i++){
		int num=(int)(Math.random()*(length-2))+1;
		rancode.append(chars.charAt(num));
		}
		url.append(rancode);
		String idurl=url.toString();
		BASE64Encoder encoder=new BASE64Encoder();
		String s=encoder.encode(idurl.getBytes());
		return s;
		}
	
	public void replace(String webname,String host,String username,String email,String url){
		SystemInfo info=Global.SYSTEMINFO;
		String fuwutel = info.getValue("fuwutel");//服务热线
		String msg=this.getBody();
		msg=msg.replace("$webname$", webname).replace("$email$", email).replace("$host$", host).replace("$username$", username)
				.replace("$url$", host+url).replace("$fuwutel$", fuwutel);
		this.setBody(msg);
	}
	
	public void replace(String username,String email,String url){
		SystemInfo info=Global.SYSTEMINFO;
		String weburl=info.getValue("weburl");
		String webname=info.getValue("webname");
		replace(webname,weburl,username,email,url);
	}
	
	public void replace(Map<String,String> map){
		SystemInfo info=Global.SYSTEMINFO;
		String weburl=info.getValue("weburl");//官网
		String webname=info.getValue("webname");//网站名称
		String fuwutel = info.getValue("fuwutel");//服务热线
		String api_name = info.getValue("api_name");//接口名称
		
		String username = map.get("username")!=null?map.get("username"):"";//用户名
		String realname = map.get("realname")!=null?map.get("realname"):"";//姓名
		String no = map.get("no")!=null?map.get("no"):"";//手机号或者身份证号
		if(!StringUtils.isEmpty(no)){
			no = no.substring(0, no.length()-4)+ "****";
		}
		String email = map.get("email")!=null?map.get("email"):"";//邮箱
		
		String status = map.get("status")!=null?map.get("status"):"";//认证状态
		String type = map.get("type")!=null?map.get("type"):"";//“手机”或者“身份证”
		
		String money = map.get("money")!=null?map.get("money"):"";//提现、充值金额
		String time = map.get("time")!=null?map.get("time"):"";//提现充值时间
		String m = map.get("msg")!=null?map.get("msg"):"";//提示信息
		//v1.8.0.3 TGPROJECT-1 lx 2014-04-02 start
		String fee= map.get("fee")!=null?map.get("fee"):"0";//手续费
		//v1.8.0.3 TGPROJECT-1 lx 2014-04-02 end
		String borrowname = map.get("borrowname")!=null?map.get("borrowname"):"";//标名
		String account = map.get("account")!=null?map.get("account"):"";//借款总额
		String apr = map.get("apr")!=null?map.get("apr"):"";//年利率
		String borrowUrl = map.get("borrowUrl")!=null?map.get("borrowUrl"):"";//标的地址
		
		String timelimit = map.get("timelimit")!=null?map.get("timelimit"):"";//借款期限
		
		String order = map.get("order")!=null?map.get("order"):"";//借款当前期
		
		//V1.8.0.4 TGPROJECT-97  显示月利率的方法  start
		String monthapr = map.get("monthapr")!=null?map.get("monthapr"):"";//月利率
		//V1.8.0.4 TGPROJECT-97  显示月利率的方法  end
		
		//v1.8.0.3 TGPROJECT-12 lx 2014-04-02 start
		String tenderAccount = map.get("tenderAccount")!=null?map.get("tenderAccount"):"";
		String tenderInterest = map.get("tenderInterest")!=null?map.get("tenderInterest"):"";
		String tenderRepaymentYesAccount = map.get("tenderRepaymentYesAccount")!=null?map.get("tenderRepaymentYesAccount"):"";
		//v1.8.0.3 TGPROJECT-30 lx 2014-04-09 start
		String endTime = map.get("endTime") != null ? map
				.get("endTime") : "";
		String valueDate = map.get("valueDate") != null ? map
				.get("valueDate") : "";
		String repaymentTime = map.get("repaymentTime") != null ? map
				.get("repaymentTime") : "";
		
		String msg=this.getBody();
		msg=msg.replace("$webname$", webname).replace("$host$", weburl).replace("$username$", username).replace("$realname$", realname)
				.replace("$NO$", no).replace("$status$", status).replace("$type$", type)
				.replace("$money$", money).replace("$time$", time).replace("$msg$", m).replace("$fee$", fee)
				.replace("$fuwutel$", fuwutel).replace("$borrowname$", borrowname).replace("$account$", account)
				.replace("$apr$", apr).replace("$timelimit$", timelimit).replace("$order$", order)
				.replace("$tenderAccount$", tenderAccount).replace("$tenderInterest$", tenderInterest).replace("$tenderRepaymentYesAccount$", tenderRepaymentYesAccount)
				.replace("$endTime$", endTime).replace("$valueDate$", valueDate).replace("$repaymentTime$", repaymentTime)
				.replace("$email$", email).replace("$borrowUrl$", borrowUrl).replace("$monthapr$", monthapr).replace("$api_name$", api_name);
		//v1.8.0.3 TGPROJECT-30 lx 2014-04-09 end
		//v1.8.0.3 TGPROJECT-12 lx 2014-04-02 end
		//V1.8.0.4 TGPROJECT-97 zxc  显示月利率的方法  end
		
		this.setBody(msg);
		if(map.get("subject")!=""&&map.get("subject")!=null){
			this.subject = map.get("subject");
		}
	}
	
	
	public static void main(String[] args) {
		Mail m =  Mail.getInstance();
		User u=new User();
		u.setUserId(2675);
		u.setEmail("449038680@qq.com");
		m.readActiveMailMsg();
		m.replace( "义乌贷", "http://localhost","fuljia",u.getEmail(),"http://localhost?id=");
		try {
			m.sendMail();
		} catch (Exception e) {
			
		}
	}

	
}
