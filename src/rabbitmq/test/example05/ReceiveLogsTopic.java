package rabbitmq.test.example05;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class ReceiveLogsTopic {

	private static final String EXCHANGE_NAME = "topic_logs";
	
	/**
	 * 分别指定下列routingKey接收：
	 * 	#
	 * 	kern.*
	 * 	*.critical
	 * 	kern.* *.critical
	 */
	public static void main(String [] args) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		
		channel.exchangeDeclare(EXCHANGE_NAME, "topic");
		String queueName = channel.queueDeclare().getQueue();
		
		if(args.length < 1) {
			System.err.println("Usage: ReceiveLogsTopic [binding_key]...");
			System.exit(1);
		}
		
		for(String bindingKey : args) {
			channel.queueBind(queueName, EXCHANGE_NAME, bindingKey);
		}
		
		System.out.println(" [*] Waiting for messages. To exit press CTRL + C");
		
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume(queueName, true, consumer);
		
		while(true) {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String message = new String(delivery.getBody());
			String routingKey = delivery.getEnvelope().getRoutingKey();
			
			System.out.println(" [x] Received '" + routingKey + "' : '" + message + "'");
		}
	}
}
