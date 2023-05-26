package com.mikhail.tarasevich.socialmedia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.mikhail.tarasevich.socialmedia")
public class RestApiSocialMediaApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestApiSocialMediaApplication.class, args);
    }

}
