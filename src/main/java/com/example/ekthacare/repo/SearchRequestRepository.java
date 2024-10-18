package com.example.ekthacare.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ekthacare.entity.SearchRequest;

public interface SearchRequestRepository extends JpaRepository<SearchRequest, Long> {
}