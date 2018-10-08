package com.example.demo.client;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class ConnectionUtils {

	/**
	 * 获取rabbitmq的链接
	 * @throws TimeoutException 
	 * @throws IOException 
	 */
	public static Connection getConnection() throws IOException, TimeoutException {
		//定义一个连接工厂
		ConnectionFactory factory=new ConnectionFactory();
		//设置服务器地址
		factory.setHost("118.126.91.157");
		//AMQP 
		factory.setPort(5672);
		
		factory.setVirtualHost("/");
		
		factory.setUsername("guest");
		
		factory.setPassword("guest");
		return factory.newConnection();
	}
	
}
