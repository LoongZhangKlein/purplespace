package com.purplespace.controller;


import com.purplespace.config.RedisConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author jiava
 * @Description DOING
 * @date 2022-12-09-11:08
 */
@RestController
@RequestMapping("/test")
public class TestController {
    @Resource
    DataSource dataSource;
    @Resource
    RedisConfig redisConfig;
    @GetMapping("/test")
    public String test(){

        System.out.println("key");

        return "设置成功";
    }
    @GetMapping("/testDataSource")
    public String testDataSource() throws SQLException {
        Connection connection = dataSource.getConnection();
        System.out.println("默认数据源:"+dataSource.getClass());
        System.out.println("当前数据源:"+connection);
        return "设置成功";
    }
}
