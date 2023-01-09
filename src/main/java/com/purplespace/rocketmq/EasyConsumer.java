package com.purplespace.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author loongzhang
 * @Description DOING
 * @date 2023-01-05-9:28
 */
@Slf4j
@RestController
@RequestMapping("/rmConsumer")
public class EasyConsumer {
    String ipAndPort="192.168.1.1:8849";
    String longPort="192.168.100.145:9876;192.168.100.146:9876;192.168.100.149:9876;192.168.100.239:9876";
    @RequestMapping("/consumer")
    public void consumer() throws MQClientException {
        // 1.创建一个接收消息的对象Consumer
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("kleinConsumerOne");
        // 设置接收服务器地址
        consumer.setNamesrvAddr(ipAndPort);
        consumer.subscribe("topic2","*");

        // 设置接收消息对应的topic 对应的sub标签为任意 此处理解 为 主题作为大类区分,sub区分细节
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                for(MessageExt message: list){
                    synchronized (MessageExt.class){
                        System.out.println("发送消息内容ID:"+message.getMsgId());
                        System.out.println("发送消息内容:"+new String(message.getBody()));
                    }

                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        // 启动接收消息服务
        consumer.start();
        log.info("接收消息服务已经开启");
        // 此处不用关闭
    }

    /**
     * 顺序消费 带事务的消费方式
     */
    @RequestMapping("/consumerByOrder")
    public void consumerByOrder() throws MQClientException {

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumerByOrder");
        consumer.setNamesrvAddr(longPort);
        // 设置消费第一次启动是从队列头部开始消费还是从队列尾部开始消费
        // 若不是第一次启动哪那么按照上次消费的顺序继续进行消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe("topic24","*");
        // 创建按照顺序消费的监听器
        consumer.registerMessageListener(new MessageListenerOrderly() {
            // todo  没看懂
             //AtomicLong consumerTimes=new AtomicLong(2);
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> list, ConsumeOrderlyContext consumeOrderlyContext) {
                // todo
                //  设置自动提交
                consumeOrderlyContext.setAutoCommit(true);
                synchronized (MessageExt.class){
                    System.out.println("这里是consumer1执行的↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
                    list.forEach(messageExt -> {
                        System.out.println("消费ID"+messageExt.getMsgId());
                        System.out.println("消费消息内容"+new String(messageExt.getBody()));
                    });
                    System.out.println("这里是consumer1执行的↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");

                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return ConsumeOrderlyStatus.SUCCESS;
            }
        });
        consumer.start();
        System.out.println("顺序消费端启动了 O(∩_∩)O");
    }
    @RequestMapping("/consumerByOrderTwo")
    public void consumerByOrderTwo() throws MQClientException {

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumerByOrderTwoZ");
        consumer.setNamesrvAddr(longPort);
        // 设置消费第一次启动是从队列头部开始消费还是从队列尾部开始消费
        // 若不是第一次启动哪那么按照上次消费的顺序继续进行消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe("topic24","*");
        // 创建按照顺序消费的监听器
        consumer.registerMessageListener(new MessageListenerOrderly() {
            // todo  没看懂
            //AtomicLong consumerTimes=new AtomicLong(2);
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> list, ConsumeOrderlyContext consumeOrderlyContext) {
                // todo
                //  设置自动提交
                consumeOrderlyContext.setAutoCommit(true);
                synchronized (MessageExt.class){
                    System.out.println("这里是consumer2执行的↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
                    list.forEach(messageExt -> {
                        System.out.println("消费ID"+messageExt.getMsgId());
                        System.out.println("消费消息内容"+new String(messageExt.getBody()));
                    });
                    System.out.println("这里是consumer2执行的↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return ConsumeOrderlyStatus.SUCCESS;
            }
        });
        consumer.start();
        System.out.println("顺序消费端启动了 O(∩_∩)O");
    }
    // todo 这种消费模式没写晚哈
    @RequestMapping("/pullConsumer")
    public void pullConsumer() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("pullConsumer");
        consumer.setNamesrvAddr(ipAndPort);
        consumer.setInstanceName("consumer");
        consumer.start();
        Set<MessageQueue> topicSet = consumer.fetchSubscribeMessageQueues("topic2");
        topicSet.forEach(messageQueue -> {
            System.out.println(messageQueue.getQueueId());
            System.out.println(messageQueue.getTopic());
        });
    }
    @RequestMapping("/transactionConsumer")
    public void transactionConsumer() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("transactionConsumerII");
        //consumer.setNamesrvAddr(longPort);
        consumer.setNamesrvAddr("192.168.1.888:9876");
        // todo wsm
        consumer.setConsumeMessageBatchMaxSize(10);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe("please_rename_unique_group_name", "*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                list.forEach(messageExt ->{
                            System.out.println("发送的消息ID"+messageExt.getMsgId());
                            System.out.println("发送的消息内容"+new String(messageExt.getBody()));
                        }
                );

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        System.out.println("消费成功 O(∩_∩)O");
    }
}
