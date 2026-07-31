package com.example.dogs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dogs.domain.Dog;

public interface DogRepository extends JpaRepository<Dog, Long> {

}
