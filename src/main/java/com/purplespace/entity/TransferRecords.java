package com.purplespace.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author loongzhang
 * @Description DOING
 * @date 2023-01-09-15:51
 */
@Data
public class TransferRecords implements Serializable {
    private static final long serialVersionUID = -5428805041928669973L;
    private String id;
    private Long fromUserId;
    private Long changeMoney;
    private String transactionId;
    private Long toUserId;
    private String recordNo;
}
