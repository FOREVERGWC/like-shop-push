package org.example.likeshoppush;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LikeShopPushApplication {

    public static void main(String[] args) {
        SpringApplication.run(LikeShopPushApplication.class, args);
    }

}
