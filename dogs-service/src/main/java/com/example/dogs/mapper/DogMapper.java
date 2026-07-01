package com.example.dogs.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.dogs.domain.Dog;
import com.example.dogs.dto.DogRequest;
import com.example.dogs.dto.DogResponse;

@Mapper(componentModel = "spring")
public interface DogMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "photoUrl", ignore = true)
  Dog toEntity(DogRequest request);

  DogResponse toResponse(Dog dog);
}