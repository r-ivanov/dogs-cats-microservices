package com.example.dogs;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class DogsServiceApplicationTests {

  @Test
  void contextLoads() {
  }
  
  @Test
  void main() {

    try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {

      DogsServiceApplication.main(new String[]{});

      mocked.verify(() ->
        SpringApplication.run(
          DogsServiceApplication.class,
          new String[]{}
        )
      );
    }
  }
}
