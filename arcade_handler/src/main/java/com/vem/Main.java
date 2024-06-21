package com.vem;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

import com.vem.handler.ArcadeDBHandler;
import com.vem.handler.RabbitMQHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import com.rabbitmq.client.*;
import java.util.concurrent.TimeoutException;

public class Main {
    static String fakeJson = """
                {
                       "policyid" : "23",
                        "srcport" : "64994",
                       "sentbyte" : "67",
                        "dstintf" : "eth1",
                          "srcip" : "192.168.1.1",
                    "srcintfrole" : "undefined",
                        "srcname" : "AD",
                        "srcintf" : "eth0",
                       "trandisp" : "snat",
                        "devname" : "FGT_LabSecurity",
                        "dstport" : "53",
                          "proto" : "17",
                    "dstintfrole" : "undefined",
                          "dstip" : "10.0.0.1"
                }
            """;

    private static final String EXCHANGE_NAME = "test_exchange";
    private static final String QUEUE_NAME = "test_queue";
    private static final String BINDING_KEY = "policy_match";
            
    public static void main(String[] args) throws IOException, TimeoutException {
        ArcadeDBHandler handler = new ArcadeDBHandler();
        // handler.main(fakeJson);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("10.255.6.31");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, BINDING_KEY);

        // Processazione messaggio
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            // System.out.println("Il mio messaggio nel main è : "+ message);
            handler.main(message);
        };

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });

        // RabbitMQHandler rabbitHandler = new RabbitMQHandler();
        // System.out.println("il nessaggio arruvato nel main è " + rabbitHandler.getMessage());
    }
}
