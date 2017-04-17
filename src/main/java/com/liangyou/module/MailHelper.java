package com.liangyou.module;

import java.sql.Timestamp;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.liangyou.util.ConfigHelper;

/**
 * @author scuchen
 * @version 1.0
 */
public class MailHelper {

	private static Logger logger = Logger.getLogger(MailHelper.class.getName());
	
	public static boolean send(String title, String content, String tomail) {
		try {
			String host = ConfigHelper.getProperty("mail.host");
			String from = ConfigHelper.getProperty("mail.username");
			String to = tomail;
			String user = ConfigHelper.getProperty("mail.username");
			String password = ConfigHelper.getProperty("mail.password");
			String auth = ConfigHelper.getProperty("mail.auth");

			Properties props = new Properties();
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.port", "25");
			if (auth != null && auth.equals("1"))
				props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.user", user);
			props.put("mail.smtp.password", password);
			Session sendMailSession;
			if (auth != null && auth.equals("1")) {
				Authenticator passwordAuth = new DefaultAuthenticator(user,password);
				sendMailSession = Session.getDefaultInstance(props,passwordAuth);
			}
			else {
				sendMailSession = Session.getDefaultInstance(props, null);
			}
			sendMailSession.setDebug(true);
			MimeMessage newMessage = new MimeMessage(sendMailSession);
			Transport transport = sendMailSession.getTransport("smtp");
			transport.connect(host, user, password);
			newMessage.setRecipients(Message.RecipientType.TO,InternetAddress.parse(to));
			newMessage.setFrom(new InternetAddress(from));
			newMessage.setSubject(title);
			Timestamp now = new Timestamp(System.currentTimeMillis());
			newMessage.setSentDate(now);
			newMessage.setContent(content, "text/plain; charset=GBK");
			Transport.send(newMessage);
			transport.close();
		}
		catch (Exception e) {
			logger.error(e.getMessage(),e);
			return false;
		}
		return true;
	}
}
