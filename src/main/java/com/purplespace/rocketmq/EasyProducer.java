package com.purplespace.rocketmq;

import com.mysql.cj.MessageBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.PushConsumerBuilder;
import org.apache.rocketmq.client.apis.consumer.SimpleConsumerBuilder;
import org.apache.rocketmq.client.apis.producer.ProducerBuilder;
import org.apache.rocketmq.client.apis.producer.TransactionChecker;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author loongzhang
 * @Description DOING
 * @date 2023-01-05-9:13
 */
@RestController
@Slf4j
@RequestMapping("/rmProducer")
public class EasyProducer {
    @Resource
    TransactionProducer transactionProducer;
    String ipAndPort = "192.168.1.1:8849";
    String longAddress = "192.168.100.145:9876;192.168.100.146:9876;192.168.100.149:9876;192.168.100.239:9876";

    @RequestMapping("/syncProducer")
    public void syncProducer() throws MQClientException, UnsupportedEncodingException, MQBrokerException, RemotingException, InterruptedException {
        // 创建发送消息的Producer
        DefaultMQProducer defaultMQProducer = new DefaultMQProducer("klein");
        // 设置发送的命名服务器地址 服务器地址应该为IP+端口号
        defaultMQProducer.setNamesrvAddr(ipAndPort);
        // 启动发送服务抛出发送异常
        defaultMQProducer.start();
        for (int i = 0; i < 5; i++) {
            Message message = new Message("topic1", (("我又来了,ZiDong") + i).getBytes("UTF-8"));
            SendResult send = defaultMQProducer.send(message);
            log.info("发送消息的返回结果{}", send);

        }
        // 同步发送消息 即时性较强，重要的消息，且必须有回执的消息，例如短信，通知（转账成功
        // 关闭连接
        defaultMQProducer.shutdown();
    }

    /**
     * 异步消息特征  及时性若,但是需要有回执信息,如订单中的某些信息
     *
     * @throws MQClientException
     * @throws UnsupportedEncodingException
     * @throws RemotingException
     * @throws InterruptedException
     */
    @RequestMapping("/asyncProducer")
    public void asyncProducer() throws MQClientException, UnsupportedEncodingException, RemotingException, InterruptedException {
        DefaultMQProducer producer = new DefaultMQProducer("kleinAsync");
        producer.setNamesrvAddr(ipAndPort);
        producer.start();
        for (int i = 0; i < 5; i++) {
            Message message = new Message("topic2", (("我又来了,shiro") + i).getBytes("UTF-8"));
            producer.send(message, new SendCallback() {
                /**
                 * 发送成功的结果
                 * @param sendResult
                 */
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.println("发送成功的结果" + sendResult);
                }

                /**
                 * 发送失败的结果
                 * @param throwable
                 */
                @Override
                public void onException(Throwable throwable) {
                    System.out.println();
                }
            });
        }
        // 异步发送消息

        System.out.println("异步发送消息结束  anytime is free");
        Thread.sleep(5000);
        producer.shutdown();
    }

    /**
     * 单项消息
     *
     * @return
     * @throws MQClientException
     * @throws UnsupportedEncodingException
     * @throws RemotingException
     * @throws InterruptedException
     */
    @RequestMapping("/oneWayProducer")
    public String oneWayProducer() throws MQClientException, UnsupportedEncodingException, RemotingException, InterruptedException {
        DefaultMQProducer producer = new DefaultMQProducer("oneWayProducer");
        producer.setNamesrvAddr(ipAndPort);
        producer.start();
        Message message = new Message("topic2", "是赵雷啊".getBytes("UTF-8"));
        producer.sendOneway(message);
        log.info("消息发送完成{}", new String(message.getBody()));
        return "发送成功";
    }

    /**
     * 批量发送消息时注意
     * 1.具有相同的topic
     * 2.相同的waitStoreM
     * 3.不能是延时消息  [为甚? try it again]
     * 4. 消息总长度不能超过4M
     *
     * @return
     * @throws UnsupportedEncodingException
     * @throws MQBrokerException
     * @throws RemotingException
     * @throws InterruptedException
     * @throws MQClientException
     */
    @RequestMapping("/delayMessageProducer")
    public String delayMessageProducer() throws UnsupportedEncodingException, MQBrokerException, RemotingException, InterruptedException, MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer("delayMessageProducer");
        producer.setNamesrvAddr(ipAndPort);
        producer.start();
        for (int i = 0; i < 5; i++) {
            Message message = new Message("topic2", (("迟来的小张") + i).getBytes("UTF-8"));
            // 设置延迟延迟等级 默认是十八个等级前四个是秒级 后两个是小时,中间是分钟
            message.setDelayTimeLevel(3);
            long start = System.currentTimeMillis();
            producer.send(message);
            System.out.println("发送消耗时间:" + (System.currentTimeMillis() - start));
        }
        return "延迟发送成功";
    }

    @RequestMapping("/branchProducer")
    public String branchProducer() throws MQClientException, MQBrokerException, RemotingException, InterruptedException {
        DefaultMQProducer producer = new DefaultMQProducer("branchProducer");
        producer.setNamesrvAddr(ipAndPort);
        producer.start();
        List<Message> messageArrayList = new ArrayList<>();
        String topicName = "topic2";
        Message message0 = new Message(topicName, "小张001".getBytes());
        Message message1 = new Message(topicName, "小赵002".getBytes());
        Message message2 = new Message(topicName, "小李003".getBytes());
        messageArrayList.add(message0);
        messageArrayList.add(message1);
        messageArrayList.add(message2);
        SendResult send = producer.send(messageArrayList);
        System.out.println("发送的消息Id:" + send.getMsgId());
        System.out.println("发送的消息内容" + messageArrayList);
        return "发送成功";
    }

    @RequestMapping("/byOrderProducer")
    public String byOrderProducer() throws MQClientException, MQBrokerException, RemotingException, InterruptedException {
        String longAddress = "192.168.100.145:9876;192.168.100.146:9876;192.168.100.149:9876;192.168.100.239:9876";
        DefaultMQProducer producer = new DefaultMQProducer("byOrderProducer");
        producer.setNamesrvAddr(longAddress);
        producer.start();
        for (int i = 0; i < 5; i++) {
            Message message = new Message("topic24", (("订单流程") + i).getBytes());
            SendResult sendResult = producer.send(message, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> messageQueueList, Message message, Object o) {
                    Integer id = (Integer) o;
                    int index = id % messageQueueList.size();
                    return messageQueueList.get(index);
                }
            }, 0);
            System.out.println("这里是发送结果" + sendResult);
        }
        producer.shutdown();
        return "顺序生产成功";
    }

    @RequestMapping("/byOrderBranchProducer")
    public String byOrderBranchProducer() throws MQClientException, MQBrokerException, RemotingException, InterruptedException {
        DefaultMQProducer producer = new DefaultMQProducer("byOrderProducer");
        producer.setNamesrvAddr(longAddress);
        producer.start();
        for (int i = 0; i < 5; i++) {
            Message message = new Message("topic24", (("预售流程") + i).getBytes());
            SendResult sendResult = producer.send(message, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> messageQueueList, Message message, Object o) {
                    Integer id = (Integer) o;
                    int index = id % messageQueueList.size();
                    return messageQueueList.get(index);
                }
            }, 0);
            System.out.println("这里是发送结果" + sendResult);
        }

        for (int i = 0; i < 5; i++) {
            Message message = new Message("topic24", (("购买流程") + i).getBytes());
            SendResult sendResult = producer.send(message, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> messageQueueList, Message message, Object o) {
                    Integer id = (Integer) o;
                    int index = id % messageQueueList.size();
                    return messageQueueList.get(index);
                }
            }, 0);
            System.out.println("这里是发送结果" + sendResult);
        }
        for (int i = 0; i < 5; i++) {
            Message message = new Message("topic24", (("下单流程") + i).getBytes());
            SendResult sendResult = producer.send(message, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> messageQueueList, Message message, Object o) {
                    Integer id = (Integer) o;
                    int index = id % messageQueueList.size();
                    return messageQueueList.get(index);
                }
            }, 0);
            System.out.println("这里是发送结果" + sendResult);
        }
        producer.shutdown();
        return "顺序生产成功";
    }


    /**
     * 根据rm官网DEMO模拟发送事务
     *
     */
    @RequestMapping("/transactionProducer")
    public void transactionProducer() throws ClientException {
        transactionProducer.test();

    }
}
