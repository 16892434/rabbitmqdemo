package rabbitmq.test.example02;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class NewTask {

	private static final String TASK_QUEUE_NAME = "task_queue";
	
	public static void main(String [] args) throws Exception {
		for(int i = 0; i < 20; i++) {
			StringBuilder msg = new StringBuilder("hello");
			
			for(int j = 0; j <= i; j++) {
				msg.append(".");
			}
			
			sender(new String[]{msg.toString()});
		}
	}

	public static void sender(String[] args) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		// true 表示将消息持久化
		channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);

		String message = getMessage(args);

		// 持久化时，同时需要设置这里的messageProperties
		channel.basicPublish("", TASK_QUEUE_NAME,
				MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
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
