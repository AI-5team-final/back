package com.example.backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackEndApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();  // .env 파일 로드

        SpringApplication.run(BackEndApplication.class, args);

    }

}
