package com.purplespace.listener;


import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @author loongzhang
 * @Description DOING
 * @date 2022-12-14-14:57
 */
@org.apache.rocketmq.spring.annotation.RocketMQMessageListener(
        topic = "test_topic", 					//topic：和消费者发送的topic相同
        consumerGroup = "test_my-consumer",     //group：不用和生产者group相同
        selectorExpression = "*")
@Component
public class RocketMQMessageListener  implements RocketMQListener<Object> {
    @Override
    public void onMessage(Object obj) {
        System.out.println(obj);
    }
}
