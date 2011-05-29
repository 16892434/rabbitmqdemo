package rabbitmq.test.example05;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class EmitLogTopic {

	private static final String EXCHANGE_NAME = "topic_logs";
	
	public static void main(String [] args) throws Exception {
		sender(new String[]{});
		sender(new String[]{"kern.critical", "A critical kernel error"});
	}
	
	public static void sender(String [] args) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		
		channel.exchangeDeclare(EXCHANGE_NAME, "topic");
		
		String routingKey = getRouting(args);
		String message = getMessage(args);
		
		channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
		System.out.println(" [x] Sent '" + routingKey + "' : '" + message + "'");
		
		channel.close();
		connection.close();
	}
	
	private static String getRouting(String [] args) throws Exception {
		if(args.length < 1) 
			return "anonymous.info";
		return args[0];
	}
	
	private static String getMessage(String [] args) throws Exception {
		if(args.length < 2)
			return "Hello World !";
		return joinStrings(args, " ", 1);
	}
	
	private static String joinStrings(String [] args, String delimter, int startIndex) throws Exception {
		int length = args.length;
		if(length == 0) return "";
		if(length < startIndex) return "";
		StringBuilder words = new StringBuilder(args[startIndex]);
		for(int i = startIndex + 1; i < length; i++) {
			words.append(delimter).append(args[i]);
		}
		return words.toString();
	}
}
