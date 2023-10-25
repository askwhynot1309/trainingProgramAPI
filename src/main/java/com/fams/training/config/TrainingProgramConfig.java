package com.fams.training.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class TrainingProgramConfig {
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    public Properties properties() throws IOException {
        File file = new File("src/main/resources/training-program-management.properties");
        Properties properties = new Properties();
        properties.load(new FileInputStream(file));

        return properties;
    }
}
