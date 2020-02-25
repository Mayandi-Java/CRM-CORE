package com.sevael.lgtool.utils;

import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import com.google.gson.JsonObject;

public class SevaelEmailAlerter {

	private Session getSession() {
		Properties props = new Properties();

		String host = "smtp.gmail.com";
		String user = "qatestingsevael@gmail.com";
		String password = "Welcome@123";
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.starttls.enable", "true");

		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, password);
			}
		});
		return session;
	}

	public static void sendMail(JsonObject jsonObj) {
		System.out.println("mail sending...");
		String filename = "";
		Session session = new SevaelEmailAlerter().getSession();
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress("info@aatralz.com"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("manikandan@sevael.net"));
			message.setSubject("Aatralz");
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent("Testing mail", "text/html");
//			Multipart multipart = new MimeMultipart();
//			multipart.addBodyPart(messageBodyPart);

//			messageBodyPart = new MimeBodyPart();
//
//			filename = jsonObj.get("file").getAsString();
//			DataSource source = new FileDataSource(filename);
//			messageBodyPart.setDataHandler(new DataHandler(source));
//			messageBodyPart.setFileName("ITGrievances.xlsx");
//			multipart.addBodyPart(messageBodyPart);
			message.setContent("Testing mail", "text/html");
			Transport.send(message);
			System.out.println("message sent successfully...");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("mail..."+ex.toString());
		}
	}

}
