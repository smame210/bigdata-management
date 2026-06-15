package org.bigdata.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.bigdata")
public class BigDataServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(BigDataServerApplication.class, args);
    }
}
