package com.purplespace.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author loongzhang
 * @Description DOING
 * @date 2022-12-12-15:24
 */
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 3805422373979603110L;
    @TableId
    private Long id;
    private String name;
    private Integer age;
    private String email;
    private Integer deleteFlag;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


}
