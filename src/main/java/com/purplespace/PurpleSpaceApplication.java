package com.purplespace;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
/**
 * 开启定时任务
 */
@EnableScheduling
public class PurpleSpaceApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(PurpleSpaceApplication.class, args);
        Environment environment = context.getBean(Environment.class);
        System.out.println("访问链接：http://localhost:" +environment.getProperty("server.port")+environment.getProperty("server.servlet.context-path"));
        System.out.println("PurpleSpaceApplication启动成功");
    }

}
