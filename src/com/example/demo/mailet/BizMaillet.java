package com.example.demo.mailet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.mail.MessagingException;

import org.apache.mailet.Mail;
import org.apache.mailet.base.GenericMailet;

import com.example.demo.client.ConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;


public class BizMaillet extends GenericMailet{
	
	private static final String QUEUE_NAME="emailAccount";
	
	@Override
	public void init() throws MessagingException {  
    }  

	@Override
	public void service(Mail mail) throws MessagingException {
		System.out.println("====================邮件大小:"+mail.getMessageSize());
		System.out.println("====================地址:"+mail.getSender().getDomain());
		System.out.println("====================host:"+mail.getSender().getHost());
		System.out.println("====================localpart:"+mail.getSender().getLocalPart());
		
		/*log("====================邮件大小:"+mail.getMessageSize());
		log("====================地址:"+mail.getSender().getDomain());
		log("====================host:"+mail.getSender().getHost());
		log("====================localpart:"+mail.getSender().getLocalPart());*/
		System.out.println(mail.getMessageSize());
		if(mail.getMessageSize() > 10485760) {
			System.out.println("大于10M");
			try {
				mail.getRecipients().clear();
				throw new Exception("文件上传大小最大10M。");
			} catch (Exception e) {
				System.out.println("异常："+e.getStackTrace());
				e.printStackTrace();
			}
		}
	//	log("匹配成功开始调打印");
		System.out.println("匹配成功开始调打印");
		//System.out.println("mail.getRecipients():"+mail.getRecipients());
		Collection<String> recipients = mail.getRecipients();
		System.out.println("mail.getRecipients():"+recipients);
		System.out.println("mail.getRecipients()大小:"+recipients.size());
		/*log("mail.getRecipients():"+recipients);
		log("mail.getRecipients()大小:"+recipients.size());*/
		List<Object> reList=new ArrayList<Object>(recipients);
		StringBuffer buffer=new  StringBuffer();
		System.out.println("recipients大小："+recipients.size());
		if(recipients.size()>0 && recipients!=null) {
			System.out.println("recipients判断");
			for(int i=0;i<reList.size();i++) {
				System.out.println(reList.get(i));
				buffer.append(reList.get(i));
				buffer.append(",");
				System.out.println("reList循环");
			}
			try {
				this.sendMsg(buffer);
				System.out.println("无异常，成功存入");
			} catch (IOException e) {
				mail.getRecipients().clear();
				System.err.println("异常1"+e.getStackTrace());
				e.printStackTrace();
			} catch (TimeoutException e) {
				mail.getRecipients().clear();
				System.out.println("异常2"+e.getStackTrace());
				e.printStackTrace();
			}
		}
	}

	
	//存入队列
	public void sendMsg(StringBuffer buffer) throws IOException, TimeoutException {
		System.out.println("mq存入连接");
		//获取一个链接
		Connection connection=ConnectionUtils.getConnection();
		//从链接中获取一个通道
		Channel channel =  connection.createChannel();
		channel.queueDeclare(QUEUE_NAME, false, false,false, null);
			
		channel.basicPublish("",QUEUE_NAME,null,buffer.toString().getBytes());
		channel.close();
		connection.close();
		System.out.println("完成mq存入连接");
	}
	
    
	

}
