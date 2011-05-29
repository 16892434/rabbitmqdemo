package rabbitmq.test.example03;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Emitlog {
	
	private static final String EXCHANGE_NAME = "logs";
	
	public static void main(String [] args) throws Exception {
		for(int i = 0; i < 20; i++) {
			StringBuilder msg = new StringBuilder("hello");
			
			for(int j = 0; j <= i; j++) {
				msg.append(".");
			}
			
			sender(new String[]{msg.toString()});
		}
	}
	
	public static void sender(String [] args) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		
		channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
		
		String message = getMessage(args);
		
		channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
		System.out.println(" [x] Sent '" + message + "'");
		
		channel.close();
		connection.close();
	}
	
	private static String getMessage(String [] args) {
		if(args.length < 1)
			return "Hello World !";
		return joinString(args, " ");
	}
	
	private static String joinString(String [] args, String delimiter) {
		int length = args.length;
		if(length == 0) 
			return "";
		
		StringBuilder words = new StringBuilder(args[0]);
		for(int i = 1; i < length; i++) {
			words.append(delimiter).append(args[i]);
		}
		return words.toString();
	}
}
