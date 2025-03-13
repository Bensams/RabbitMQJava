package org.example;

import com.rabbitmq.client.*;

public class ConsumerWithRetry {
    public static void main(String[] args) throws Exception {
        // Set up connection factory
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        // Create a connection and a channel
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String QUEUE_NAME = "task_queue";

        // Declare a queue
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages...");

        // Define the deliver callback
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Processing: '" + message + "'");

            try {
                // Simulate processing failure
                if (Math.random() > 0.5) {
                    throw new RuntimeException("Simulated failure!");
                }
                System.out.println(" [✓] Processed successfully");
                // Acknowledge message
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            } catch (Exception e) {
                System.out.println(" [!] Error processing message, retrying...");
                // Negative acknowledgment for requeue
                channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
            }
        };

        boolean autoAck = false; // Enable manual acknowledgment
        channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, consumerTag -> {});
    }
}
