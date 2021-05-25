package com.kmtp.composite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.kmtp"})
public class CompositeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CompositeServiceApplication.class, args);
    }

}
