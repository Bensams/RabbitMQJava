package org.example;

import com.rabbitmq.client.*;

import java.util.concurrent.CountDownLatch;

public class Consumer {
    private final static String QUEUE_NAME = "task_queue1";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.55.104");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            System.out.println(" [*] Waiting for messages. To exit press Ctrl+C");

            CountDownLatch latch = new CountDownLatch(1);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
            };
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });

            latch.await(); // Keep the main thread alive
        }
    }
}