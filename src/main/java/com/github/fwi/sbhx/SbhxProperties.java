package com.github.fwi.sbhx;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "sbhx")
@Data
public class SbhxProperties {

    Duration responseDelay;
    
}
