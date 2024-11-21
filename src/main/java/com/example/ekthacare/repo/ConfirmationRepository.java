package com.example.ekthacare.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ekthacare.entity.Confirmation;

public interface ConfirmationRepository extends JpaRepository<Confirmation, Long> {
    Confirmation findByRecipientIdAndLoggedInUserId(Long recipientId, Long loggedInUserId);

Confirmation findFirstByRecipientIdAndLoggedInUserIdAndStoppedAtIsNull(Long recipientId, Long loggedInUserId);

List<Confirmation> getConfirmationsByLoggedInUserId(Long loggedInUserId);

Confirmation findFirstByRecipientIdAndLoggedInUserIdOrderByStoppedAtDesc(Long recipientId, Long loggedInUserId);
    
List<Confirmation> findAll();

List<Confirmation> findByLoggedInUserId(Long loggedInUserId);

Confirmation findFirstByLoggedInUserIdAndStoppedAtIsNotNull(Long loggedInUserId);

Confirmation findTopByRecipientIdOrderByIdDesc(Long recipientId);
    


}

