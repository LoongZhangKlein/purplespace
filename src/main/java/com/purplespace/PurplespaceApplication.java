package com.purplespace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PurplespaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PurplespaceApplication.class, args);
        System.out.println("PurplespaceApplication start successful");
    }

}
