package com.example.ekthacare.services;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.ekthacare.entity.SearchRequest;
import com.example.ekthacare.repo.SearchRequestRepository;
import org.springframework.data.domain.Pageable;
@Service
public class SearchRequestService {
    @Autowired
    private SearchRequestRepository repository;
    
    public List<SearchRequest> getAllSearchRequests() {
        return repository.findAll();
    }
    
    public SearchRequest getSearchRequestByUserId(Long userId) {
        return repository.findById(userId).orElse(null);
    }

    public void saveSearchRequest(Long userId, String bloodgroup, String city, String state) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setUserId(userId);       
        searchRequest.setBloodgroup(bloodgroup);
        searchRequest.setCity(city);
        searchRequest.setState(state);
        searchRequest.updateTimestamp();
        repository.save(searchRequest);
    }    
   
    public List<SearchRequest> findByTimestampThisWeek() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
                                       .withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfWeek = now.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY))
                                     .withHour(23).withMinute(59).withSecond(59);

        List<SearchRequest> searchRequests = repository.findRequestsThisWeek(startOfWeek, endOfWeek);
        
        // Return list, let Thymeleaf handle the formatting via getFormattedTimestamp()
        return searchRequests;
    }

    public List<SearchRequest> findByTimestampThisMonth() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);

        List<SearchRequest> searchRequests = repository.findRequestsThisMonth(startOfMonth, endOfMonth);

        // Return list, let Thymeleaf handle the formatting via getFormattedTimestamp()
        return searchRequests;
    }

    public List<SearchRequest> findByTimestampLast3Months() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOf3MonthsAgo = now.minusMonths(3).with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOf3Months = now.withHour(23).withMinute(59).withSecond(59).withNano(999_999_999);

        List<SearchRequest> searchRequests = repository.findRequestsLast3Months(startOf3MonthsAgo, endOf3Months);

        // Return list, let Thymeleaf handle the formatting via getFormattedTimestamp()
        return searchRequests;
    }

    public List<SearchRequest> findAll(int page, int size) {
        // Fetch the requested page of search requests
        Page<SearchRequest> searchRequestPage = repository.findAll(PageRequest.of(page, size));
        return searchRequestPage.getContent(); // Get the content of the page
    }


    public long countAllRequests() {
        return repository.count();
    }
    
    

    public List<SearchRequest> findByTimestampThisWeek(int page, int pageSize) {
        LocalDateTime startDate = LocalDateTime.now().minus(1, ChronoUnit.WEEKS);
        Pageable pageable = PageRequest.of(page, pageSize);
        return repository.findByTimestampThisWeek(startDate, pageable);
    }

    public long countRequestsThisWeek() {
        LocalDateTime startDate = LocalDateTime.now().minus(1, ChronoUnit.WEEKS);
        return repository.countRequestsThisWeek(startDate);
    }

    public List<SearchRequest> findByTimestampThisMonth(int page, int pageSize) {
        LocalDateTime startDate = LocalDateTime.now().minus(1, ChronoUnit.MONTHS);
        Pageable pageable = PageRequest.of(page, pageSize);
        return repository.findByTimestampThisMonth(startDate, pageable);
    }

    public long countRequestsThisMonth() {
        LocalDateTime startDate = LocalDateTime.now().minus(1, ChronoUnit.MONTHS);
        return repository.countRequestsThisMonth(startDate);
    }

    public List<SearchRequest> findByTimestampLast3Months(int page, int pageSize) {
        LocalDateTime startDate = LocalDateTime.now().minus(3, ChronoUnit.MONTHS);
        Pageable pageable = PageRequest.of(page, pageSize);
        return repository.findByTimestampLast3Months(startDate, pageable);
    }

    public long countRequestsLast3Months() {
        LocalDateTime startDate = LocalDateTime.now().minus(3, ChronoUnit.MONTHS);
        return repository.countRequestsLast3Months(startDate);
    }
}
