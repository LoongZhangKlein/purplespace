package com.purplespace.rocketmq;

import com.alibaba.fastjson.JSON;
import com.purplespace.entity.TransferRecords;
import com.purplespace.service.BusinessService;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
@Component
public class TransactionListenerImpl implements TransactionListener {
    @Resource
    private BusinessService businessService;

    /**
     * 执行本地事务方法
     * @param message
     * @param o
     * @return
     */
    @Override
    public LocalTransactionState executeLocalTransaction(Message message, Object o) {
        // 组装发送事务实体类
        TransferRecords transferRecords = JSON.parseObject(message.getBody(), TransferRecords.class);
        // 本地事务标记
        LocalTransactionState localTransactionState=LocalTransactionState.UNKNOW;
        // 执行本地事务
        try {
            businessService.doTransfer(transferRecords.getFromUserId(),transferRecords.getToUserId()
                    ,transferRecords.getChangeMoney(),transferRecords.getRecordNo()
                    ,transferRecords.getTransactionId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 若事务提交 则标记状态为成功
        // 若事务失败则 标记状态为回滚
        return localTransactionState;
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
        LocalTransactionState localTransactionState=LocalTransactionState.UNKNOW;
        // 获取本地事务ID
        try{
            boolean isCommit = businessService.checkTransferStatus(messageExt.getTransactionId());
            //
            if (isCommit){
                // 本地事务提交成功 设置标记未成功
                localTransactionState=LocalTransactionState.COMMIT_MESSAGE;
            }else{
                localTransactionState=LocalTransactionState.ROLLBACK_MESSAGE;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return localTransactionState;
    }
}
