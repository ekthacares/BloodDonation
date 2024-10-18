package com.example.ekthacare.services;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ekthacare.entity.SearchRequest;
import com.example.ekthacare.repo.SearchRequestRepository;

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
        searchRequest.setTimestamp(LocalDateTime.now());
        repository.save(searchRequest);
    }
    
    
}
