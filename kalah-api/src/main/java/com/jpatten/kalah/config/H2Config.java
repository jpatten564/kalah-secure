package com.jpatten.kalah.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:h2.properties")
public class H2Config {
}
