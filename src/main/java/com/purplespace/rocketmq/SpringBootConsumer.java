package com.purplespace.rocketmq;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(consumerGroup = "springBootGroup", topic = "TestTopic")
public class SpringBootConsumer implements RocketMQListener {

    @Override
    public void onMessage(Object message) {
        System.out.println("Received message : "+ message);
    }
}
