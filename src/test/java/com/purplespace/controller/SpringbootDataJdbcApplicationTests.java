package com.purplespace.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest
class SpringbootDataJdbcApplicationTests {

    //DI注入数据源
    @Resource
    DataSource dataSource;

    @Test
    public void contextLoads() throws SQLException {
        //看一下默认数据源 com.zaxxer.hikari.HikariDataSource
        System.out.println("默认数据源: " + dataSource.getClass());
        //获得连接
        Connection connection = dataSource.getConnection();
        System.out.println("当前连接: " + connection);
        //关闭连接
        connection.close();
    }

}
