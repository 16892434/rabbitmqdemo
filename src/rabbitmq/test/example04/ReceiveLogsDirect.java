package rabbitmq.test.example04;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class ReceiveLogsDirect {
	
	private static final String EXCHANGE_NAME = "direct_logs";
	
	/**
	 * 需要启动两个客户端，分别在args中指定：info和{info, warning, error} 
	 */
	public static void main(String [] args) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		
		channel.exchangeDeclare(EXCHANGE_NAME, "direct");
		String queueName = channel.queueDeclare().getQueue();
		
		if(args.length < 1) {
			System.out.println("Usage: ReceivedLogsDirect [info] [warning] [error]");
			System.exit(1);
		}
		
		for(String severity : args) {
			channel.queueBind(queueName, EXCHANGE_NAME, severity);
		}
		
		System.out.println(" [*] Waiting for messages. To exit press CTRL + C");
		
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume(queueName, false, consumer);
		
		while(true) {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String message = new String(delivery.getBody());
			String routingKey = delivery.getEnvelope().getRoutingKey();
			
			System.out.println(" [x] Received '" + routingKey + "' : '" + message + "'");
		}
	}

}
