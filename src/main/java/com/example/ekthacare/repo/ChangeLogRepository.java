package com.example.ekthacare.repo;

import com.example.ekthacare.entity.ChangeLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {
    // You can define custom query methods here if needed
}
