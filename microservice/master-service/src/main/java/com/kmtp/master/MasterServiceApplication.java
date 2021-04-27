package com.kmtp.master;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
public class MasterServiceApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(MasterServiceApplication.class, args);

        String h2DbUrl = ctx.getEnvironment().getProperty("spring.datasource.hikari.jdbc-url");
        log.info("Connected to h2Db: {}", h2DbUrl);
//        SpringApplication.run(MasterServiceApplication.class, args);
    }

}
