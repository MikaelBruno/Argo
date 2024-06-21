package com.vem.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import com.rabbitmq.client.*;
import java.util.concurrent.TimeoutException;

class Wrapper<T> {
    private T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}

public class RabbitMQHandler {
    private static final String EXCHANGE_NAME = "test_exchange";
    private static final String QUEUE_NAME = "test_queue";
    private static final String BINDING_KEY = "policy_match";
    
    public String getMessage() throws IOException, TimeoutException {
        Wrapper<String> wrapper = new Wrapper<>();

        // Connessione a RabbitMQ
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
            wrapper.setValue(message);
        };

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });

        String message = wrapper.getValue();
        System.out.println(("Il mio messaggio è " + message));
        if(message != null){
            return message;
        }
        return "";
    }
}