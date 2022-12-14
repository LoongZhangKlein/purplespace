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
        // ?????????????????????Producer
        DefaultMQProducer defaultMQProducer = new DefaultMQProducer("klein");
        // ???????????????????????????????????? ????????????????????????IP+?????????
        defaultMQProducer.setNamesrvAddr(ipAndPort);
        // ????????????????????????????????????
        defaultMQProducer.start();
        for (int i = 0; i < 5; i++) {
            Message message = new Message("topic1", (("????????????,ZiDong") + i).getBytes("UTF-8"));
            SendResult send = defaultMQProducer.send(message);
            log.info("???????????????????????????{}", send);

        }
        // ?????????????????? ??????????????????????????????????????????????????????????????????????????????????????????????????????
        // ????????????
        defaultMQProducer.shutdown();
    }

    /**
     * ??????????????????  ????????????,???????????????????????????,???????????????????????????
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
            Message message = new Message("topic2", (("????????????,shiro") + i).getBytes("UTF-8"));
            producer.send(message, new SendCallback() {
                /**
                 * ?????????????????????
                 * @param sendResult
                 */
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.println("?????????????????????" + sendResult);
                }

                /**
                 * ?????????????????????
                 * @param throwable
                 */
                @Override
                public void onException(Throwable throwable) {
                    System.out.println();
                }
            });
        }
        // ??????????????????

        System.out.println("????????????????????????  anytime is free");
        Thread.sleep(5000);
        producer.shutdown();
    }

    /**
     * ????????????
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
        Message message = new Message("topic2", "????????????".getBytes("UTF-8"));
        producer.sendOneway(message);
        log.info("??????????????????{}", new String(message.getBody()));
        return "????????????";
    }

    /**
     * ???????????????????????????
     * 1.???????????????topic
     * 2.?????????waitStoreM
     * 3.?????????????????????  [??????? try it again]
     * 4. ???????????????????????????4M
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
            Message message = new Message("topic2", (("???????????????") + i).getBytes("UTF-8"));
            // ???????????????????????? ?????????????????????????????????????????? ??????????????????,???????????????
            message.setDelayTimeLevel(3);
            long start = System.currentTimeMillis();
            producer.send(message);
            System.out.println("??????????????????:" + (System.currentTimeMillis() - start));
        }
        return "??????????????????";
    }

    @RequestMapping("/branchProducer")
    public String branchProducer() throws MQClientException, MQBrokerException, RemotingException, InterruptedException {
        DefaultMQProducer producer = new DefaultMQProducer("branchProducer");
        producer.setNamesrvAddr(ipAndPort);
        producer.start();
        List<Message> messageArrayList = new ArrayList<>();
        String topicName = "topic2";
        Message message0 = new Message(topicName, "??????001".getBytes());
        Message message1 = new Message(topicName, "??????002".getBytes());
        Message message2 = new Message(topicName, "??????003".getBytes());
        messageArrayList.add(message0);
        messageArrayList.add(message1);
        messageArrayList.add(message2);
        SendResult send = producer.send(messageArrayList);
        System.out.println("???????????????Id:" + send.getMsgId());
        System.out.println("?????????????????????" + messageArrayList);
        return "????????????";
    }

    @RequestMapping("/byOrderProducer")
    public String byOrderProducer() throws MQClientException, MQBrokerException, RemotingException, InterruptedException {
        String longAddress = "192.168.100.145:9876;192.168.100.146:9876;192.168.100.149:9876;192.168.100.239:9876";
        DefaultMQProducer producer = new DefaultMQProducer("byOrderProducer");
        producer.setNamesrvAddr(longAddress);
        producer.start();
        for (int i = 0; i < 5; i++) {
            Message message = new Message("topic24", (("????????????") + i).getBytes());
            SendResult sendResult = producer.send(message, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> messageQueueList, Message message, Object o) {
                    Integer id = (Integer) o;
                    int index = id % messageQueueList.size();
                    return messageQueueList.get(index);
                }
            }, 0);
            System.out.println("?????????????????????" + sendResult);
        }
        producer.shutdown();
        return "??????????????????";
    }

    @RequestMapping("/byOrderBranchProducer")
    public String byOrderBranchProducer() throws MQClientException, MQBrokerException, RemotingException, InterruptedException {
        DefaultMQProducer producer = new DefaultMQProducer("byOrderProducer");
        producer.setNamesrvAddr(longAddress);
        producer.start();
        for (int i = 0; i < 5; i++) {
            Message message = new Message("topic24", (("????????????") + i).getBytes());
            SendResult sendResult = producer.send(message, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> messageQueueList, Message message, Object o) {
                    Integer id = (Integer) o;
                    int index = id % messageQueueList.size();
                    return messageQueueList.get(index);
                }
            }, 0);
            System.out.println("?????????????????????" + sendResult);
        }

        for (int i = 0; i < 5; i++) {
            Message message = new Message("topic24", (("????????????") + i).getBytes());
            SendResult sendResult = producer.send(message, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> messageQueueList, Message message, Object o) {
                    Integer id = (Integer) o;
                    int index = id % messageQueueList.size();
                    return messageQueueList.get(index);
                }
            }, 0);
            System.out.println("?????????????????????" + sendResult);
        }
        for (int i = 0; i < 5; i++) {
            Message message = new Message("topic24", (("????????????") + i).getBytes());
            SendResult sendResult = producer.send(message, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> messageQueueList, Message message, Object o) {
                    Integer id = (Integer) o;
                    int index = id % messageQueueList.size();
                    return messageQueueList.get(index);
                }
            }, 0);
            System.out.println("?????????????????????" + sendResult);
        }
        producer.shutdown();
        return "??????????????????";
    }


    /**
     * ??????rm??????DEMO??????????????????
     *
     */
    @RequestMapping("/transactionProducer")
    public void transactionProducer() throws ClientException {
        transactionProducer.test();

    }
}
