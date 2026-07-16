package com.example.dogs.mapper;

import org.mapstruct.Mapper;

import com.example.dogs.dto.JokeApiResponse;
import com.example.dogs.dto.JokeResponse;

@Mapper(componentModel = "spring")
public interface JokeMapper {
  default JokeResponse toJokeResponse(JokeApiResponse response) {

    String content;

    if ("single".equals(response.type())) {
      content = response.joke();
    } else {
      content = response.setup() + " - " + response.delivery();
    }

    return new JokeResponse(
      response.type(),
      content
    );
  }
}
