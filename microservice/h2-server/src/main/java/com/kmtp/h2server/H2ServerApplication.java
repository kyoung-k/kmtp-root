package com.kmtp.h2server;

import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.Arrays;

@SpringBootApplication
public class H2ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(H2ServerApplication.class, args);
    }
}
