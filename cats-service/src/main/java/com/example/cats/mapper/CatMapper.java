package com.example.cats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.cats.domain.Cat;
import com.example.cats.dto.CatRequest;
import com.example.cats.dto.CatResponse;

@Mapper(componentModel = "spring")
public interface CatMapper {

  @Mapping(target = "id", ignore = true)
  Cat toEntity(CatRequest request);

  CatResponse toResponse(Cat cat);
}