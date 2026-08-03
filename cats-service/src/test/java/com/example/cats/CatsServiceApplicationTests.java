package com.example.cats;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class CatsServiceApplicationTests {

  @Test
  void contextLoads() {
  }

  @Test
  void main() {

    try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {

      CatsServiceApplication.main(new String[]{});

      mocked.verify(() ->
        SpringApplication.run(
          CatsServiceApplication.class,
          new String[]{}
        )
      );
    }
  }
}
