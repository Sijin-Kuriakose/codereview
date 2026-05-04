package com.sijin.codereview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CodereviewApplication {

    public static void main(String[] args)
    {
        SpringApplication.run(CodereviewApplication.class, args);
    }
}