package com.algaworks.brewer.config;

import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeZoneConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeZoneConfig.class);

    @Bean
    public TimeZone timeZone(){
    	System.out.println(">>>>>>>>>> TimeZone");
        TimeZone defaultTimeZone = TimeZone.getTimeZone("America/Sao_Paulo");
        TimeZone.setDefault(defaultTimeZone);
        LOGGER.info("Spring boot application running in UTC timezone :"+new Date());
        return defaultTimeZone;
    }

}