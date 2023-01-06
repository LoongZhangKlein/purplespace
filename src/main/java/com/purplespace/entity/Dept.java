package com.purplespace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author loongzhang
 * @Description DOING
 * @date 2022-12-21-16:58
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dept {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String dName;
    private String dbSource;

}
