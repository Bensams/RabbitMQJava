package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Scanner;

public class Producer {
    private final static String QUEUE_NAME = "task_queue1";

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.55.104");
        String message = "";

        do {
            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {

                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                System.out.print("Enter message to send to consumer or type 'exit' to exit: ");
                message = sc.nextLine();
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes());

                System.out.println(" [x] Sent '" + message + "'");

            }
            } while (!message.equals("exit"));


    }
}
