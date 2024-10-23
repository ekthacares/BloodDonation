package com.example.ekthacare.repo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.ekthacare.entity.SearchRequest;

public interface SearchRequestRepository extends JpaRepository<SearchRequest, Long> {
	
	 @Query("SELECT sr FROM SearchRequest sr WHERE sr.timestamp >= :start AND sr.timestamp <= :end")
	    List<SearchRequest> findRequestsThisWeek(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	    @Query("SELECT sr FROM SearchRequest sr WHERE sr.timestamp >= :start AND sr.timestamp <= :end")
	    List<SearchRequest> findRequestsThisMonth(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	    @Query("SELECT sr FROM SearchRequest sr WHERE sr.timestamp >= :start AND sr.timestamp <= :end")
	    List<SearchRequest> findRequestsLast3Months(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
	}
	
