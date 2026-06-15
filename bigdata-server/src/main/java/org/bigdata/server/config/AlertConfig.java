package org.bigdata.server.config;

import org.bigdata.alert.registry.SenderRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlertConfig {

    @Bean
    public SenderRegistry senderRegistry() {
        return new SenderRegistry();
    }
}
