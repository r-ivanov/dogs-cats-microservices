package com.example.dogs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DogsServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(DogsServiceApplication.class, args);
  }

}
