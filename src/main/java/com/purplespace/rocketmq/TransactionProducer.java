package com.purplespace.rocketmq;

import com.alibaba.fastjson.JSON;
import com.purplespace.entity.TransferRecords;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 在初始化bean的时候会执行该方法。
 */
@Component
public class TransactionProducer  implements InitializingBean {
    private static TransactionMQProducer producer = new TransactionMQProducer("please_rename_unique_group_name");

    @Resource
    private TransactionListenerImpl transactionListener;


    @Override
    public void afterPropertiesSet() throws Exception {
        producer.setNamesrvAddr("192.168.1.888:9876");
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 5, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2000), new ThreadFactory() {
            @Override
            public Thread newThread(@NotNull Runnable r) {
                return new Thread("client-transaction-msg-check-thread");
            }
        });
        // 执行事务检测线程
        producer.setExecutorService(threadPoolExecutor);
        // 设置回调监视
        producer.setTransactionListener(transactionListener);
        try{
            producer.start();
        }catch (Exception e){
           e.printStackTrace();

        }

    }
    public void test(){
        //单次转账唯一编号
        String businessNo = UUID.randomUUID().toString();

        //要发送的事务消息 设置转账人用户id为1， 被转账人用户id为2  转账金额100元，单次转账唯一标识businessNo
        TransferRecords transferRecord = new TransferRecords();
        transferRecord.setFromUserId(1L);
        transferRecord.setToUserId(2L);
        transferRecord.setChangeMoney(100L);
        transferRecord.setRecordNo(businessNo);
        try {
            Message message = new Message("TransanctionMessage", "tag", businessNo, JSON.toJSONString(transferRecord).getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult sendResult = producer.sendMessageInTransaction(message, null);
            System.out.println("prepare事务消息发送结果:"+sendResult.getSendStatus());

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (MQClientException e) {
            throw new RuntimeException(e);
        }

    }
}
