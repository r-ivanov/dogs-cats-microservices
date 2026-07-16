package com.example.dogs.dto;

public record JokeApiResponse(
  String type,
  String joke,
  String setup,
  String delivery
) {
}
