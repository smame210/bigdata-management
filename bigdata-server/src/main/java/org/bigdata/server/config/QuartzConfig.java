package org.bigdata.server.config;

import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class QuartzConfig {

    @Bean
    @DependsOn("databaseInitialize")
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource,
                                                      QuartzProperties quartzProperties,
                                                      AutowiringSpringBeanJobFactory jobFactory) {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setDataSource(dataSource);
        schedulerFactory.setAutoStartup(true);
        schedulerFactory.setStartupDelay(10);
        schedulerFactory.setJobFactory(jobFactory);

        Properties properties = new Properties();
        properties.putAll(quartzProperties.getProperties());
        properties.remove("org.quartz.jobStore.class");
        schedulerFactory.setQuartzProperties(properties);
        return schedulerFactory;
    }
}