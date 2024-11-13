package com.example.ekthacare.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ekthacare.entity.OtpVerification;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {
    
    Optional<OtpVerification> findByRecipientId(Long recipientId);

    void deleteByRecipientId(Long recipientId);
    
    Optional<OtpVerification> findByRecipientIdAndLoggedInUserId(Long recipientId, Long loggedInUserId);
    
    Optional<OtpVerification> findTopByRecipientIdAndLoggedInUserIdOrderByGeneratedAtDesc(Long recipientId, Long loggedInUserId);

}
