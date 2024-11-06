package com.example.ekthacare.repo;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
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
	
	    @Query("SELECT sr FROM SearchRequest sr WHERE sr.timestamp >= :startDate")
	    List<SearchRequest> findByTimestampThisWeek(@Param("startDate") LocalDateTime startDate, org.springframework.data.domain.Pageable pageable);

	    // Custom query to find requests within the last month
	    @Query("SELECT sr FROM SearchRequest sr WHERE sr.timestamp >= :startDate")
	    List<SearchRequest> findByTimestampThisMonth(@Param("startDate") LocalDateTime startDate, org.springframework.data.domain.Pageable pageable);

	    // Custom query to find requests within the last three months
	    @Query("SELECT sr FROM SearchRequest sr WHERE sr.timestamp >= :startDate")
	    List<SearchRequest> findByTimestampLast3Months(@Param("startDate") LocalDateTime startDate, org.springframework.data.domain.Pageable pageable);

	    // Count the requests within the last week
	    @Query("SELECT COUNT(sr) FROM SearchRequest sr WHERE sr.timestamp >= :startDate")
	    long countRequestsThisWeek(@Param("startDate") LocalDateTime startDate);

	    // Count the requests within the last month
	    @Query("SELECT COUNT(sr) FROM SearchRequest sr WHERE sr.timestamp >= :startDate")
	    long countRequestsThisMonth(@Param("startDate") LocalDateTime startDate);

	    // Count the requests within the last three months
	    @Query("SELECT COUNT(sr) FROM SearchRequest sr WHERE sr.timestamp >= :startDate")
	    long countRequestsLast3Months(@Param("startDate") LocalDateTime startDate);

}
	
