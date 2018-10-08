package com.example.demo.mailet;

import java.util.Collection;


import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;
import org.apache.mailet.base.GenericMatcher;

import com.example.demo.client.RedisClient;


public class BizMatcher extends GenericMatcher{

	RedisClient client = new RedisClient();

//	@Override
//	public boolean matchRecipient(MailAddress recipient) throws MessagingException {
//		
//    }

	@Override
	public Collection match(Mail mail) throws MessagingException {
		try {
			
		String senderMessage = mail.getSender().getLocalPart()+"@"+mail.getSender().getDomain();
		//判断用户发邮件的次数是否超标
		Boolean exceedTheStandard = client.setRedisCount(senderMessage);
		if(exceedTheStandard) {
			//写入黑名单30天
			client.setRedisBlack(senderMessage);
		}
		//是否在黑白名单中找到
		Boolean white = client.getRedisWhite(senderMessage);
		System.out.println("=========================User===================="+senderMessage);
		//log("=========================User===================="+senderMessage);
		if(white) {
			Boolean contains = containsMailAddress(mail);
			if(contains) {
				return mail.getRecipients();
			}else {
				mail.getRecipients().clear();
				throw new Exception("邮件域名不正确。");
				
			}
		}
		Boolean black = client.getRedisBlack(senderMessage);
		if(black) {
			mail.getRecipients().clear();
			throw new Exception("你已被列入黑名单，无法将邮件传达。");
		}
		
		
		Boolean contains = containsMailAddress(mail);
		System.out.println("=================================contains:"+ contains);
	//	log("=================================contains:"+ contains);
		if(!contains) {
			throw new Exception("邮件域名不正确。");
		}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mail.getRecipients();
	}
	
	public Boolean containsMailAddress(Mail mail) {
		Collection<MailAddress> addressList =  mail.getRecipients();
		for (MailAddress mailAddress : addressList) {
			InternetAddress address = mailAddress.toInternetAddress();
			if(address.getAddress().contains("mdfk.com")) {
				System.out.println("邮件地址:"+mailAddress.getUser());				
				//log("邮件地址:"+mailAddress.getUser());
				return true;
			}
		}
		return false;
	}
	
	
	
//	private static String[] hosts = null;
//	
//	@Override
//	public Collection match(Mail mail) throws MessagingException {
//		System.out.println("getCondition:" + getCondition());
//		if (hosts == null && getCondition() != null)
//		{
//			hosts = getCondition().split(",");
//		}
//		if (hosts == null || hosts.length == 0 || getCondition() == null) 
//		{
//			return mail.getRecipients();
//		}
//		
//		MailAddress mailAddress = mail.getSender();
//		
//		System.out.println("mailAddress.toString():" + mailAddress.toString());
//		
//		for (String strTemp : hosts) {
//			if (mailAddress.toString().toLowerCase().indexOf(strTemp) != -1) {
//				System.out.println(mailAddress.toString().toLowerCase().indexOf(strTemp));
//				return mail.getRecipients();
//				}
//			}
//		 mail.getRecipients().clear();
//		 return null;
//		 
//	}
//

	
	}



	
