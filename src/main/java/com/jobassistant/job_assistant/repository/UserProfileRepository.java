package com.jobassistant.job_assistant.repository;

import com.jobassistant.job_assistant.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    java.util.List<UserProfile> findAllByUserId(Long userId);
    java.util.Optional<UserProfile> findFirstByUserIdOrderByIdAsc(Long userId);
    java.util.Optional<UserProfile> findByIdAndUserId(Long id, Long userId);
}
