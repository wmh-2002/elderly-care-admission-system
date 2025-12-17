package com.example.elderlycaresystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.elderlycaresystem.repository")
public class ElderlyCareSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElderlyCareSystemApplication.class, args);
    }

}