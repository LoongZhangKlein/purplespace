package com.purplespace.controller;

import com.purplespace.rocketmq.SpringBootProducer;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientConfigurationBuilder;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;

@RestController
@RequestMapping("/mqtest")
@Slf4j
public class MQTestController {
    @Resource
    private RocketMQTemplate rocketMQTemplate;
    private final String topic = "TestTopic";
    private final String address="127.0.0.1:9876";
    @Resource
    private SpringBootProducer producer;

    /**
     * 生产者
     *
     * @param msg
     * @return
     * @throws ClientException
     */
    @GetMapping("/sendMessage")
    public String sendMessage() throws ClientException, MQClientException, InterruptedException, MQBrokerException, RemotingException {
        DefaultMQProducer producer = new DefaultMQProducer("LoongGroup");
        // 输入nameServer地址
        producer.setNamesrvAddr(address);
        //设置实例名称
        producer.setInstanceName("producer");
        // 只需要开启一次即可 无需每次开启
        producer.start();
        for (int i = 0; i < 3; i++) {
            Thread.sleep(1000);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
            // 创建消息
            Message message = new Message("Topic-test",
                    "testTag",
                    ("消息内容RocketMQ test msg" + i+dateFormat.format(new Date())).getBytes());
            SendResult sendResult = producer.send(message);
            System.out.println("消息ID==>"+sendResult.getMsgId());
            System.out.println("队列信息==>"+sendResult.getMessageQueue());
            System.out.println("发送结果==>"+sendResult.getSendStatus());
            System.out.println("下个消费信息==>"+sendResult.getOffsetMsgId());
            System.out.println("队列偏移量==>"+sendResult.getQueueOffset());
            System.out.println("+++++++++++++++++++++++++");
            System.out.println();
        }
        producer.shutdown();
        return "消息发送完成";
    }

    //这个发送事务消息的例子中有很多问题，需要注意下。
    @RequestMapping("/sendTransactionMessage")
    public String sendTransactionMessage(String message) throws InterruptedException {
        producer.sendMessageInTransaction(topic, message);
        return "消息发送完成";
    }

    @GetMapping("/customMessage")
    public String message() throws ClientException, InterruptedException, IOException, MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("LoongGroup");
        consumer.setNamesrvAddr(address);
        consumer.setInstanceName("consumer");
        // 订阅某该主题,订阅所有主题用*
        consumer.subscribe("Topic-test","testTag");
        // 注册监听回调实现类来处理broker推送过来的消息MessageListenerConcurrently是并发消费
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messageExtList, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                messageExtList.stream().forEach(messageExt->{
                    System.out.println("消费信息id==>"+messageExt.getMsgId());
                    System.out.println("消费信息内容++>"+new String(messageExt.getBody()));
                    System.out.println("√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√");
                    System.out.println();
                });
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        log.info("消费启动完成: 开始表演吧");
        return "消费消息完成";
    }

    /**
     * 同步发送是指消息发送方发出数据后，会在收到接收方发回响应之后才会发送下一个数据包的通讯方式。
     * todo 接收方响应了吗?
     */
    @GetMapping("/testSync")
    public void testSync(){
        for (int i = 0; i < 3; i++) {
            SendResult sendResult = rocketMQTemplate.syncSend("test-topic", "同步消息测试");
            System.out.println("发送结果"+sendResult.getSendStatus());
        }
    }


}
