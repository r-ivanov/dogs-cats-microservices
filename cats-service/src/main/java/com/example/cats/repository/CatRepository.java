package com.example.cats.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cats.domain.Cat;

public interface CatRepository extends JpaRepository<Cat, Long> {

}
