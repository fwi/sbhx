package com.github.fwi.sbhx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties( SbhxProperties.class )
public class AppMain {

    public static void main(String[] args) {
        SpringApplication.run(AppMain.class, args);
    }

    @Bean
	HomeController homeController(SbhxProperties appProps, ServerModel serverModel) {
		return new HomeController(appProps, serverModel);
	}

    @Bean
    ServerModel serverModel() {
        return new ServerModel();
    }
    
}
