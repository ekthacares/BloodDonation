package com.example.ekthacare.services;



import com.example.ekthacare.entity.SentEmail;
import com.example.ekthacare.repo.SentEmailRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class SentEmailService {

    private final SentEmailRepository sentEmailRepository;
    
    

    
    public SentEmailService(SentEmailRepository sentEmailRepository) {
        this.sentEmailRepository = sentEmailRepository;
    }

    public List<SentEmail> getSentEmailsByUserId(Long userId) {
        return sentEmailRepository.findByLoggedInUserId(userId);
            }
    
    public List<SentEmail> findSentEmailsByRecipientAndUser(Long recipientId, Long userId) {
        return sentEmailRepository.findByRecipientIdAndLoggedInUserId(recipientId, userId);
    }
    
    public List<SentEmail> getSentEmailsByRecipientId(Long recipientId) {
        // Call repository method to retrieve sent emails by recipientId
        return sentEmailRepository.findByRecipientId(recipientId);
    }
    
    


}
