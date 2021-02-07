package com.ttpai.framework.rose.boot.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * @author Kail
 */

@SpringBootApplication
public class ExampleApp extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ExampleApp.class);
        application.run(args);
    }

}
