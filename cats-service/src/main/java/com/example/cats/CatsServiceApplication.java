package com.example.cats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CatsServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(CatsServiceApplication.class, args);
  }

}
