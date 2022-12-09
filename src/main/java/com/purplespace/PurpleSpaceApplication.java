package com.purplespace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PurpleSpaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PurpleSpaceApplication.class, args);
        System.out.println("PurplespaceApplication启动成功");
    }

}
